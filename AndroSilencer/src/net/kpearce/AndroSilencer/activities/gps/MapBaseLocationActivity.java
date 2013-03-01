package net.kpearce.AndroSilencer.activities.gps;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.AndroSilencer.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;



/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/20/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapBaseLocationActivity extends MapActivity implements GestureDetector.OnGestureListener {

    private ImageButton imageButton;
    private GestureDetector gestureDetector;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        gestureDetector = new GestureDetector(this,this);

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setImageAlpha(200);

        MapOverlay mapOverlay = new MapOverlay();
        mapView.getOverlays().add(mapOverlay);



    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onLayerButtonClick(View view){

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    class MapOverlay extends com.google.android.maps.Overlay{
        @Override

        public boolean onTouchEvent(MotionEvent event, MapView mapview){

            if (event.getAction()==1){
                GeoPoint p=mapview.getProjection().fromPixels((int)event.getX(), (int)event.getY());
                Toast.makeText(getBaseContext(),p.getLatitudeE6()/1E6 + "," + p.getLongitudeE6()/1E6+", zoom level: "+mapview.getZoomLevel(), Toast.LENGTH_SHORT).show();
                mapview.getController().zoomIn();

            }
            return false;
        }


    }
}