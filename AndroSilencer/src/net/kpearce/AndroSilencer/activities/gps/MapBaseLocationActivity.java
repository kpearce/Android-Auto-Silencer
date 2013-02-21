package net.kpearce.AndroSilencer.activities.gps;

import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import com.example.AndroSilencer.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/20/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapBaseLocationActivity extends MapActivity {

    private ImageButton imageButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setImageAlpha(200);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onLayerButtonClick(View view){

    }
}