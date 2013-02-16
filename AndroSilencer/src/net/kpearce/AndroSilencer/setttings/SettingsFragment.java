package net.kpearce.AndroSilencer.setttings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.example.AndroSilencer.R;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/15/13
 * Time: 7:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        addPreferencesFromResource(R.layout.preference_layout);
    }
}
