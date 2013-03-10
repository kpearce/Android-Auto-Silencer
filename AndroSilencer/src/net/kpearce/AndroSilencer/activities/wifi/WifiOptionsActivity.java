package net.kpearce.AndroSilencer.activities.wifi;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.StaticFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/24/13
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class WifiOptionsActivity extends ListActivity {

    private List<WifiResult> wifiResults;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        List<WifiResult> scanResults = convertScanResults(wifiManager.getScanResults());
        List<WifiResult> configurations = convertConfigurations(wifiManager.getConfiguredNetworks());
        wifiResults = merge(scanResults,configurations);
        setListAdapter(new WifiResultListAdapter(this, R.layout.wifi_result_layout, wifiResults));
    }

    private List<WifiResult> merge(List<WifiResult> scanResults, List<WifiResult> configurations) {
        List<WifiResult> toRemove = new ArrayList<WifiResult>();
        for (WifiResult scanResult : scanResults) {
            for (WifiResult configuration : configurations) {
                if(configuration.SSID.equals(scanResult.SSID)){
                    toRemove.add(configuration);
                }
            }
        }

        configurations.removeAll(toRemove);
        scanResults.addAll(configurations);
        Collections.sort(scanResults);
        return scanResults;
    }

    private List<WifiResult> convertConfigurations(List<WifiConfiguration> configuredNetworks) {
        List<WifiResult> ret = new ArrayList<WifiResult>();
        for (WifiConfiguration configuredNetwork : configuredNetworks) {
            ret.add(new WifiResult(configuredNetwork.SSID.substring(1, configuredNetwork.SSID.length() - 1),null));
        }
        return ret;
    }

    private List<WifiResult> convertScanResults(List<ScanResult> scanResults) {
        List<WifiResult> ret = new ArrayList<WifiResult>();
        for (ScanResult scanResult : clean(scanResults)) {
            ret.add(new WifiResult(scanResult.SSID,scanResult.level));
        }
        return ret;
    }

    private List<ScanResult> clean(List<ScanResult> scanResults) {
        List<ScanResult> toRemove = new ArrayList<ScanResult>();
        for (ScanResult scanResult : scanResults) {
            if (scanResult.SSID.trim().equals("")) {
                toRemove.add(scanResult);
            }
        }
        scanResults.removeAll(toRemove);
        return scanResults;
    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, long id) {
        new AlertDialog.Builder(this)
                .setMessage("Silence device when near " + ((TextView) v).getText())
                .setTitle("Add Location")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WifiResult wifiResult = (WifiResult) l.getItemAtPosition(position);
                        try {
                            StaticFileManager.saveSSID(v.getContext(), wifiResult.SSID);
                            wifiResults.remove(wifiResult);
                            l.invalidateViews();
                        } catch (IOException e) {
                            Log.e(getString(com.example.AndroSilencer.R.string.log_tag), "Failed to write to file", e);
                        }
                    }
                })
                .setNegativeButton("No", null).show();
    }

    class WifiResultListAdapter extends ArrayAdapter<WifiResult>{
        private final List<WifiResult> items;
        private final int textViewResourceId;

        public WifiResultListAdapter(Context context, int textViewResourceId, List<WifiResult> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.textViewResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            RelativeLayout view = (RelativeLayout) layoutInflater.inflate(R.layout.wifi_result_layout,null);
            ImageView wifiStrengthImageView = (ImageView) view.findViewById(R.id.wifi_strength_image_view);
            TextView textView = (TextView) view.findViewById(R.id.left_wifi_result_textbox);
            WifiResult wifiResult = items.get(position);
            textView.setText(wifiResult.SSID);

            if(wifiResult.dBm != null){
                int signalStrength = wifiResult.dBm;
                if(signalStrength > -60){
                    wifiStrengthImageView.setImageResource(R.drawable.stat_sys_wifi_signal_4);
                }
                else if(signalStrength > -70){
                    wifiStrengthImageView.setImageResource(R.drawable.stat_sys_wifi_signal_3);
                }
                else if(signalStrength > -80){
                    wifiStrengthImageView.setImageResource(R.drawable.stat_sys_wifi_signal_2);
                }
                else if(signalStrength > -90){
                    wifiStrengthImageView.setImageResource(R.drawable.stat_sys_wifi_signal_1);
                }
                else {
                    wifiStrengthImageView.setImageResource(R.drawable.stat_sys_wifi_signal_0);
                }
            }
            else {
                wifiStrengthImageView.setVisibility(View.INVISIBLE);
            }

            return view;
        }
    }

    class WifiResult implements Comparable<WifiResult>{
        String SSID;
        Integer dBm = Integer.MIN_VALUE;

        WifiResult(String SSID, Integer dBm) {
            this.SSID = SSID;
            this.dBm = dBm;
        }

        @Override
        public int compareTo(WifiResult another) {
            if(dBm != null && another.dBm != null){
                return another.dBm.compareTo(dBm);
            }
            else if(dBm != null){
                return -1;
            }
            else if(another.dBm != null){
                return 1;
            }
            else {
                return SSID.compareTo(another.SSID);
            }
        }
    }
}