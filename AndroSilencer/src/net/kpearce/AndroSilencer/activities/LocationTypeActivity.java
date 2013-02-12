package net.kpearce.AndroSilencer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.AndroSilencer.R;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 1/30/13
 * Time: 11:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocationTypeActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationtypeselection);
    }

    public void onChooseWifi(View view) {
        Intent intent = new Intent(this,SaveOrScanActivity.class);
        startActivity(intent);
    }

    public void onChooseGps(View view){
        Toast.makeText(view.getContext(), "Cheers GPS", Toast.LENGTH_LONG).show();
    }
}