package net.kpearce.AndroSilencer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.example.AndroSilencer.R;

public class MainActivity extends Activity {

    private LinearLayout switchView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        switchView = (LinearLayout) this.findViewById(R.id.switchLayout);
    }

    public void newLocationClick(View view){
        Intent newLocationIntent = new Intent(view.getContext(),LocationTypeActivity.class);
        startActivity(newLocationIntent);
//        WifiTypeSelection locationSelectionFragment = WifiTypeSelection.newInstance();
//        locationSelectionFragment.show(getFragmentManager(),"dialog");
    }
}
