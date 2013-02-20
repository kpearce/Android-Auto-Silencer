package net.kpearce.AndroSilencer.setttings;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/19/13
 * Time: 9:33 PM
 * To change this template use File | SettingsActivity | File Templates.
 */
public class SettingsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}