package net.kpearce.AndroSilencer.activities.gps.mapoverlay;

import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/20/13
 * Time: 10:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayerSelectorOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public LayerSelectorOverlay(Drawable drawable) {
        super(drawable);
    }

    @Override
    protected OverlayItem createItem(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
