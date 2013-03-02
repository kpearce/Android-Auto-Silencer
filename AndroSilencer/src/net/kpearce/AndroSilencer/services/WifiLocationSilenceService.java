package net.kpearce.AndroSilencer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.StaticFileManager;
import net.kpearce.AndroSilencer.activities.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/11/13
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class WifiLocationSilenceService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int REGISTER_CLIENT = 0;
    public static final int UNREGISTER_CLIENT = 1;
    public static final int REQUEST_CURRENT_SSID = 2;
    public static final int REQUEST_NEXT_POLL = 3;
    public static final int SEND_SSID = 4;
    public static final int SEND_POLL = 5;
    private final String ssid = "ssid";
    private ArrayList<Messenger> messengerClients = new ArrayList<Messenger>();
    private String WIFI_SAVE_FILE;
    private WifiManager wifiManager;
    private WifiScanTimerTask wifiScanTimerTask;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private Context applicationContext;
    private IntentFilter intentFilter;
    private AudioManager audioManager;
    private ScheduledThreadPoolExecutor executor;
    private Handler toastHandler;
    private FileObserver fileObserver;
    private static boolean isStarted = false;
    private String nearSSID;
    private static final String SILENCED_BY_SERVICE = "SILENCED_BY_SERVICE";
    private SharedPreferences sharedPreferences;
    private long nextPollTime = 0;
    private Messenger serviceMessenger = new Messenger(new MessageHandler());

    class MessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REGISTER_CLIENT:
                    messengerClients.add(msg.replyTo);
                    break;
                case UNREGISTER_CLIENT:
                    messengerClients.remove(msg.replyTo);
                    break;
                case REQUEST_CURRENT_SSID:
                    Message m = Message.obtain(null,SEND_SSID);
                    Bundle bundle = new Bundle();
                    bundle.putString(ssid,nearSSID);
                    m.setData(bundle);
                    try {
                        msg.replyTo.send(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_NEXT_POLL:
                    Message message = Message.obtain(null,SEND_POLL,nextPollTime);
                    try {
                        msg.replyTo.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void updateNextPollClients(){
        for (Messenger messengerClient : messengerClients) {
            Message message = Message.obtain(null,SEND_POLL,nextPollTime);
            try {
                messengerClient.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateSSIDClients(){
        for (Messenger messengerClient : messengerClients) {
            Message m = Message.obtain(null,SEND_SSID);
            Bundle bundle = new Bundle();
            bundle.putString(ssid,nearSSID);
            m.setData(bundle);
            try {
                messengerClient.send(m);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        WIFI_SAVE_FILE = this.getString(R.string.wifi_saves_file);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiBroadcastReceiver = new WifiBroadcastReceiver();
        intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        applicationContext = this;
        executor = getExecutor();
        File file = null;
        try {
            file = StaticFileManager.getOrCreateWifiSavesFile(this);
        } catch (IOException e) {
            Log.d(getString(R.string.log_tag), "could not access file system");
        }
        if(file != null){
            fileObserver = new FileObserver(file.getPath(),FileObserver.MODIFY) {
                @Override
                public void onEvent(int event, String path) {
                    int delay = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(applicationContext).
                            getString(getString(R.string.poll_time_minutes_key), getString(R.string.default_poll)));
                    startOrRestartTimer(delay);
                }
            };
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private ScheduledThreadPoolExecutor getExecutor() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        return executor;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toastHandler = new Handler(applicationContext.getMainLooper());
        fileObserver.startWatching();
        int delay = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.poll_time_minutes_key), getString(R.string.default_poll)));
        startOrRestartTimer(delay);
        isStarted = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isStarted = false;
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(getString(R.string.poll_time_minutes_key).equals(key)){
            int newDelay = Integer.valueOf(sharedPreferences.getString(key, getString(R.string.default_poll)));
            startOrRestartTimer(newDelay);
        }
    }

    private void startOrRestartTimer(int delay) {
        if(!executor.isShutdown()){
            executor.shutdown();
        }
        if(wifiScanTimerTask != null){
            wifiScanTimerTask.cancel();
        }
        wifiScanTimerTask = new WifiScanTimerTask();
        executor = getExecutor();
        executor.scheduleAtFixedRate(wifiScanTimerTask,0, delay, TimeUnit.MINUTES);
    }

    class WifiScanTimerTask extends TimerTask{

        @Override
        public void run() {
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                applicationContext.registerReceiver(wifiBroadcastReceiver, intentFilter);
                wifiManager.startScan();
            }
        }
    }

    class WifiBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int delay = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(applicationContext).
                    getString(getString(R.string.poll_time_minutes_key), getString(R.string.default_poll)));
            nextPollTime = System.currentTimeMillis() + delay*60*1000;
            updateNextPollClients();
            applicationContext.unregisterReceiver(wifiBroadcastReceiver);
            File wifiSaves = new File(getFilesDir(),WIFI_SAVE_FILE);
            if (wifiSaves.exists()) {
                try {
                    LinkedList<String> ssids = StaticFileManager.getSavedSSIDs(context);
                    boolean foundOne = false;
                    nearSSID = null;
                    for (ScanResult scanResult : wifiManager.getScanResults()) {
                        foundOne = ssids.contains(scanResult.SSID);
                        if(foundOne){
                            nearSSID = scanResult.SSID;
                            break;
                        }
                    }
                    updateSSIDClients();

                    if(foundOne){
                        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit();
                            editor.putBoolean(SILENCED_BY_SERVICE,true);
                            editor.commit();
                            toastHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Sound disabled", Toast.LENGTH_LONG).show();
                                }
                            });
                            Log.d("AndroSilencer", "Silencing the device");
                        }
                    }
                    else {
                        boolean silencedByService = sharedPreferences.getBoolean(SILENCED_BY_SERVICE,false);
                        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL && silencedByService) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit();
                            editor.putBoolean(SILENCED_BY_SERVICE,false);
                            editor.commit();
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            toastHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Sound restored", Toast.LENGTH_LONG).show();
                                }
                            });
                            Log.d("AndroSilencer", "Restoring sound");
                        }
                    }

                } catch (FileNotFoundException e) {
                    Log.d(getString(R.string.log_tag),"Wifi saved ssid file not found",e);
                } catch (IOException e) {
                    Log.d(getString(R.string.log_tag), "Failed reading wifi saved ssids", e);
                }
            }
            else {
                Log.d(getString(R.string.log_tag), "No saved wifi ssids "+new Date(System.currentTimeMillis()).toString());
            }
        }
    }

    public static boolean isStarted(){
        return isStarted;
    }
}
