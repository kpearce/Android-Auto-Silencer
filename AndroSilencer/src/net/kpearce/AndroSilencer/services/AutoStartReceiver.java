package net.kpearce.AndroSilencer.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/11/13
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class AutoStartReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context,WifiLocationSilenceService.class);
        context.startService(newIntent);
    }
}
