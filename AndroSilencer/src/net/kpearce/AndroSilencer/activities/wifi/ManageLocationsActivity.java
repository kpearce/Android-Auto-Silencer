package net.kpearce.AndroSilencer.activities.wifi;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.StaticFileManager;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/14/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageLocationsActivity extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        updateListView();
    }

    private void updateListView() {
        try {
            setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, StaticFileManager.getSavedSSIDs(this)));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, long id) {
        new AlertDialog.Builder(this).setMessage("Remove location "+((TextView)v).getText())
                .setTitle("Remove Location")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            StaticFileManager.removeSSID(v.getContext(), (String) l.getItemAtPosition(position));
                            updateListView();
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }).setNegativeButton("No",null).show();
    }
}
