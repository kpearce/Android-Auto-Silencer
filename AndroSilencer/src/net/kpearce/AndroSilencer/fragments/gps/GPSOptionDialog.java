package net.kpearce.AndroSilencer.fragments.gps;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.activities.MainActivity;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/27/13
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPSOptionDialog extends DialogFragment {

    public GPSOptionDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gps_type,container);
        Button mapButton = (Button) view.findViewById(R.id.mapoption);
        Button gpsButton = (Button) view.findViewById(R.id.gpsoption);
        getDialog().setTitle("Choose");

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.chooseMap();
                dismiss();
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.chooseMyLocation();
                dismiss();
            }
        });
        return view;
    }
}
