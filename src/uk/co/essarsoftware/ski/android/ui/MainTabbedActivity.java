package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * <p>Android Activity that displays main screen tabs.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (07 Feb 2012)
 *
 */
public class MainTabbedActivity extends TabActivity
{
    /* (non-Javadoc)
     * @see uk.co.essarsoftware.ski.ui.SkiDataActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintabbed);
        
        // The activity TabHost
        TabHost tabHost = getTabHost();

        // DataSummaryActivity
        {
        	// Create an Intent to launch an Activity for the tab
        	Intent intent = new Intent().setClass(this, DataSummaryActivity.class);
            TabSpec spec = tabHost.newTabSpec("summary")
            			  .setIndicator("Summary")
                          .setContent(intent);
            tabHost.addTab(spec);
        }
        
        // TrackListActivity
        {
        	Intent intent = new Intent().setClass(this, TrackListActivity.class);
            TabSpec spec = tabHost.newTabSpec("tracks")
            			.setIndicator("Tracks")
            			.setContent(intent);
            tabHost.addTab(spec); 
        }

        tabHost.setCurrentTab(0);
        
        Log.d(getLocalClassName(), "MainTabbedActivity created");
    }
}