package net.kpearce.AndroSilencer.activities.wifi;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import net.kpearce.AndroSilencer.StaticFileManager;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/24/13
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class WifiOptionsActivity extends ListActivity {

    private List<String> wifiResults;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        wifiResults = combineResults(scanResults, configurations);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, wifiResults));
    }

    private List<String> combineResults(List<ScanResult> scanResults, List<WifiConfiguration> configurations) {
        Set<String> results = new HashSet<String>();
        for (ScanResult scanResult : clean(scanResults)) {
            results.add(scanResult.SSID);
        }

        for (WifiConfiguration configuration : configurations) {
            results.add(configuration.SSID.substring(1, configuration.SSID.length() - 1));
        }
        try {
            results.removeAll(StaticFileManager.getSavedSSIDs(this));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        ArrayList<String> ret = new ArrayList<String>(results);
        Collections.sort(ret);
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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Silence device when near " + ((TextView) v).getText())
                .setTitle("Add Location")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String ssid = (String) l.getItemAtPosition(position);
                        try {
                            StaticFileManager.saveSSID(v.getContext(), ssid);
                            wifiResults.remove(ssid);
                            l.invalidateViews();
                        } catch (IOException e) {
                            Log.e(getString(com.example.AndroSilencer.R.string.log_tag), "Failed to write to file", e);
                        }
                    }
                })
                .setNegativeButton("No", null).show();
    }
}