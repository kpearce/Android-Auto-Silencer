package net.kpearce.AndroSilencer.activities.wifi;

import android.*;
import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import net.kpearce.AndroSilencer.StaticFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 1/31/13
 * Time: 12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class WifiScanResultsActivity extends ListActivity {

//    private ListView scanResultsListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        setListAdapter(new ScanResultAdapter(this, R.layout.simple_list_item_1,clean(wifiManager.getScanResults())));
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
                        String ssid = ((ScanResult) l.getItemAtPosition(position)).SSID;
                        try {
                            StaticFileManager.saveSSID(v.getContext(), ssid);
                        } catch (IOException e) {
                            Log.e(getString(com.example.AndroSilencer.R.string.log_tag), "Failed to write to file", e);
                        }
                    }
                })
                .setNegativeButton("No", null).show();
    }

    class ScanResultAdapter extends ArrayAdapter<ScanResult>{

        public ScanResultAdapter(Context context, int textViewResourceId, List<ScanResult> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ScanResult scanResult = getItem(position);
            LayoutInflater inflater = (LayoutInflater) getContext()
            			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TextView viewById = (TextView) inflater.inflate(R.layout.simple_list_item_1,parent,false);
            viewById.setText(scanResult.SSID);
            return viewById;
        }
    }
}