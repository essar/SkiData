package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.R;
import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.xyplot.XYPlot;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * <p>Android Activity that displays an XY plot (histogram) of data</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 *
 */
public class XYPlotActivity extends SkiDataActivity
{
	private XYPlotView view;
	
	private void highlightAltitude() {
		Track t = AppData.getAppData().track;
		if(t != null) {
			AltitudeRenderer ar = new AltitudeRenderer(t);
			view.setRenderer(ar, true);
		}
	}
	
	private void highlightMode() {
		ModeRenderer mr = new ModeRenderer();
		view.setRenderer(mr, true);
	}
	
	private void highlightSpeed() {
		Track t = AppData.getAppData().track;
		if(t != null) {
			SpeedRenderer sr = new SpeedRenderer(t);
			view.setRenderer(sr, true);
		}
	}
	
    /* (non-Javadoc)
     * @see uk.co.essarsoftware.ski.ui.SkiDataActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get plot data from singleton data structure
        // TODO more elegant way of doing this?
        XYPlot plot = AppData.getAppData().plot;
        
        if(plot != null) {
	        // Add view objects
	        view = new XYPlotView(this, plot, new ModeRenderer());
	        setContentView(view);
        } else {
        	Log.w(getLocalClassName(), "Plot data is null, unable to draw");
        }
        Log.d(getLocalClassName(), "XYPlotActivity created");
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.xyplotmenu, menu);
        return true;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.xyplot_menu_render_altitude:
	            highlightAltitude();
	            return true;
	        case R.id.xyplot_menu_render_mode:
	            highlightMode();
	            return true;
	        case R.id.xyplot_menu_render_speed:
	        	highlightSpeed();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
    }
}