package net.kpearce.AndroSilencer.activities;

import android.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import net.kpearce.AndroSilencer.StaticFileManager;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 1/31/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkConfigurationActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        setListAdapter(new WifiConfigurationListAdapter(this, R.layout.simple_list_item_1,wifiManager.getConfiguredNetworks()));
    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, long id) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Silence device when near " + ((TextView) v).getText())
                .setTitle("Add Location")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String ssid = ((WifiConfiguration) l.getItemAtPosition(position)).SSID;
                        ssid = ssid.substring(1,ssid.length()-1);
//                        saveSsid(ssid);
                        try {
                            StaticFileManager.saveSSID(v.getContext(),ssid);
                        } catch (IOException e) {
                            Log.e(getString(com.example.AndroSilencer.R.string.log_tag),"Failed to write to file",e);
                        }
                    }
                })
                .setNegativeButton("No", null).show();
    }

    class WifiConfigurationListAdapter extends ArrayAdapter<WifiConfiguration>{

        public WifiConfigurationListAdapter(NetworkConfigurationActivity networkConfigurationActivity, int simple_list_item_1, List<WifiConfiguration> configuredNetworks) {
            super(networkConfigurationActivity, simple_list_item_1, configuredNetworks);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           WifiConfiguration wifiConfiguration =  getItem(position);

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TextView viewById = (TextView) inflater.inflate(R.layout.simple_list_item_1,parent,false);
            viewById.setText(wifiConfiguration.SSID.substring(1,wifiConfiguration.SSID.length()-1));
            return viewById;
        }
    }
}