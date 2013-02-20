package net.kpearce.AndroSilencer.services;

import android.app.Service;
import android.content.*;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.StaticFileManager;

import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
    private static final int MINUTE_DELAY = 1;
    private String WIFI_SAVE_FILE;
    private WifiManager wifiManager;
    private WifiScanTimerTask wifiScanTimerTask;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private Context applicationContext;
    private IntentFilter intentFilter;
    private AudioManager audioManager;
    private ScheduledThreadPoolExecutor executor;
    private Handler toastHandler;
    private boolean silencedByService;
    private FileObserver fileObserver;

    public IBinder onBind(Intent intent) {
        return null;
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
        silencedByService = false;
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
                    int delay = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(applicationContext).getString(getString(R.string.poll_time_minutes_key), getString(R.string.default_poll)));
                    startOrRestartTimer(delay);
                }
            };
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    private ScheduledThreadPoolExecutor getExecutor() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        return executor;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        toastHandler = new Handler(applicationContext.getMainLooper());
        fileObserver.startWatching();
        int delay = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.poll_time_minutes_key), getString(R.string.default_poll)));
        startOrRestartTimer(delay);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"AndroSilencer might function incorrectly",Toast.LENGTH_SHORT).show();
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
            applicationContext.unregisterReceiver(wifiBroadcastReceiver);
            File wifiSaves = new File(getFilesDir(),WIFI_SAVE_FILE);
            if (wifiSaves.exists()) {
                try {
                    LinkedList<String> ssids = StaticFileManager.getSavedSSIDs(context);
                    boolean foundOne = false;
                    for (ScanResult scanResult : wifiManager.getScanResults()) {
                        foundOne = ssids.contains(scanResult.SSID);
                        if(foundOne){break;}
                    }

                    if(foundOne){
                        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            silencedByService = true;
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
                        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL && silencedByService) {
                            silencedByService = false;
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
}
