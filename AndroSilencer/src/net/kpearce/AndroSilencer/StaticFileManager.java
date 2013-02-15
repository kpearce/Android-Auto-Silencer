package net.kpearce.AndroSilencer;

import android.content.Context;
import com.example.AndroSilencer.R;

import java.io.*;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/12/13
 * Time: 12:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class StaticFileManager {

    public static LinkedList<String> getSavedSSIDs(Context context) throws IOException {
        File file = new File(context.getFilesDir(), context.getString(R.string.wifi_saves_file));
        LinkedList<String> ssids = new LinkedList<String>();
        if(!file.exists()){
            return ssids;
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String ssid = bufferedReader.readLine();
        while (ssid != null){
            if(!ssids.contains(ssid)){
                ssids.add(ssid);
            }
            ssid = bufferedReader.readLine();
        }
        bufferedReader.close();
        return ssids;
    }

    public static void saveSSID(Context context,String toSave) throws IOException {
        if(getSavedSSIDs(context).contains(toSave)){
            return;
        }
        FileOutputStream outputStream = context.openFileOutput(context.getString(R.string.wifi_saves_file),Context.MODE_APPEND);
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(toSave);
        writer.flush();
        writer.close();
    }

    public static void removeSSID(Context context, String toRemove) throws IOException {
        LinkedList<String> currentSSIDs = getSavedSSIDs(context);
        if(!currentSSIDs.contains(toRemove)){
            return;
        }

        currentSSIDs.remove(toRemove);
        FileWriter writer = new FileWriter(new File(context.getFilesDir(),context.getString(R.string.wifi_saves_file)),false);
        PrintWriter printWriter = new PrintWriter(writer);
        for (String currentSSID : currentSSIDs) {
            printWriter.println(currentSSID);
        }
        printWriter.flush();
        printWriter.close();
    }
}
