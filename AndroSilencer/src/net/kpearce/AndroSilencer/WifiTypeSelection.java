package net.kpearce.AndroSilencer;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.AndroSilencer.R;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 1/31/13
 * Time: 9:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class WifiTypeSelection extends DialogFragment {

    static WifiTypeSelection newInstance(){
        return new WifiTypeSelection();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.savedorscanswitch,container,true);
        return view;
    }
}
