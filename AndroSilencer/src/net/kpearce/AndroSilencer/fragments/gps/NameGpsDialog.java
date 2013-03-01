package net.kpearce.AndroSilencer.fragments.gps;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.AndroSilencer.R;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/27/13
 * Time: 11:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class NameGpsDialog extends DialogFragment {

    public NameGpsDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_gps_location,container);
        Button okButton = (Button) v.findViewById(R.id.gpsok);
        Button cancelButton = (Button) v.findViewById(R.id.gpscancel);
        EditText editText = (EditText) v.findViewById(R.id.gps_name_edittext);
        getDialog().setTitle("Name this Location");
        editText.requestFocus();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                dismiss();
                return true;
            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }
}
