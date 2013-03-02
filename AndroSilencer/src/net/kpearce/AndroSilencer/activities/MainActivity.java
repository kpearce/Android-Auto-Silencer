package net.kpearce.AndroSilencer.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.*;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.activities.gps.MapBaseLocationActivity;
import net.kpearce.AndroSilencer.activities.wifi.ManageLocationsActivity;
import net.kpearce.AndroSilencer.activities.wifi.WifiOptionsActivity;
import net.kpearce.AndroSilencer.fragments.gps.GPSOptionDialog;
import net.kpearce.AndroSilencer.fragments.gps.NameGpsDialog;
import net.kpearce.AndroSilencer.services.WifiLocationSilenceService;
import net.kpearce.AndroSilencer.setttings.SettingsActivity;

import java.util.Date;

public class MainActivity extends Activity {
    private static final String STATUS_STRING = "statusString";
    private TextView statusBox;
    private LocationManager locationManager;
    private TextView nextPollTimeBox;
    private boolean isBound;

    private Messenger mainMessenger = new Messenger(new IncomingMessageHandler());

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            Message message = Message.obtain(null,WifiLocationSilenceService.REGISTER_CLIENT);
            message.replyTo = mainMessenger;
            try {
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Message requestSSID = Message.obtain(null,WifiLocationSilenceService.REQUEST_CURRENT_SSID);
            requestSSID.replyTo = mainMessenger;
            try {
                serviceMessenger.send(requestSSID);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            Message requestPollTime = Message.obtain(null,WifiLocationSilenceService.REQUEST_NEXT_POLL);
            requestPollTime.replyTo = mainMessenger;
            try {
                serviceMessenger.send(requestPollTime);
            } catch (RemoteException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            isBound = false;
        }
    };
    private Messenger serviceMessenger;

    class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WifiLocationSilenceService.SEND_POLL:
                    long pollTime = (Long) msg.obj;
                    updateNextPollBox(pollTime);
                    break;
                case WifiLocationSilenceService.SEND_SSID:
                    String ssid = msg.getData().getString("ssid");
                    updateStatusBox(ssid);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main);
        if (!WifiLocationSilenceService.isStarted()) {
            Intent intent = new Intent(this, WifiLocationSilenceService.class);
            startService(intent);
        }

        statusBox = (TextView) findViewById(R.id.status_message);
        nextPollTimeBox = (TextView) findViewById(R.id.next_poll_time_text_box);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this,WifiLocationSilenceService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateStatusBox(String ssid) {
        if(ssid != null){
            statusBox.setText("Near "+ssid);
        }
        else {
            statusBox.setText("");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            Message message = Message.obtain(null,WifiLocationSilenceService.UNREGISTER_CLIENT);
            message.replyTo = mainMessenger;
            try {
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void updateNextPollBox(long pollTime) {
        if(pollTime != 0){
            Date d = new Date(pollTime);
            String formattedDate = DateFormat.format("h:mm", d).toString();
            nextPollTimeBox.setText("Next poll: "+formattedDate);
        }
        else {
            nextPollTimeBox.setText("Next poll: unknown");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATUS_STRING,statusBox.getText().toString());
    }

    public void addWifiLocationClick(View view){
        Intent intent = new Intent(this,WifiOptionsActivity.class);
        startActivity(intent);
    }

    public void addGpsLocationClick(View view){
        GPSOptionDialog dialog = new GPSOptionDialog();
        dialog.show(getFragmentManager(), "gps_type");

    }

    public void manageLocationsClick(View view){
        Intent manageLocationIntent = new Intent(view.getContext(),ManageLocationsActivity.class);
        startActivity(manageLocationIntent);
    }

    public void openSettings(View view){
        Intent settingsActivity = new Intent(view.getContext(),SettingsActivity.class);
        startActivity(settingsActivity);
    }

    public void chooseMap() {
        Intent intent = new Intent(this, MapBaseLocationActivity.class);
        startActivity(intent);
    }

    public void chooseMyLocation() {
        NameGpsDialog nameGpsDialog = new NameGpsDialog();
        nameGpsDialog.show(getFragmentManager(),"new_gps_location");
    }
}
