package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.R;
import uk.co.essarsoftware.ski.data.SkiData;
import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement.Mode;
import uk.co.essarsoftware.ski.plots.AltitudePlot;
import uk.co.essarsoftware.ski.plots.SpeedPlot;
import uk.co.essarsoftware.ski.plots.TrackPlot;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * <p>Android Activity that displays summary information about loaded data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 *
 */
public class DataSummaryActivity extends SkiDataActivity
{
	private SkiData data;
	
    /* (non-Javadoc)
     * @see uk.co.essarsoftware.ski.ui.SkiDataActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datasummary);
        
        // Retrieve loaded data
        data = AppData.getAppData().data;
        
        if(data != null) {
        	// Set summary values
        	Track t = data.getAllElements();
        	Track s = data.getAllInMode(Mode.SKI);
        	long tTime = ((t.getEndTime() - t.getStartTime()) * 1000L);
	        long sTime = (s.size() * 1000L);
	        
        	((SummaryTile) findViewById(R.id.datasummary_totaldist)).setValue(String.format("%.2fkm", (t.getDistance() / 1000.0f)));
	        ((SummaryTile) findViewById(R.id.datasummary_totalskidist)).setValue(String.format("%.2fkm", (s.getDistance() / 1000.0f)));
	        
	        ((SummaryTile) findViewById(R.id.datasummary_hialt)).setValue(String.format("%,dm", t.getHighAltitude()));
	        ((SummaryTile) findViewById(R.id.datasummary_lowalt)).setValue(String.format("%,dm", t.getLowAltitude()));
	        
	        ((SummaryTile) findViewById(R.id.datasummary_maxskispeed)).setValue(String.format("%.2fkph", s.getMaxSpeed()));
	        ((SummaryTile) findViewById(R.id.datasummary_aveskispeed)).setValue(String.format("%.2fkph", s.getAverageSpeed()));
	        
	        ((SummaryTile) findViewById(R.id.datasummary_totaltime)).setValue(String.format("%tk:%tM:%tS", tTime, tTime, tTime));
	        ((SummaryTile) findViewById(R.id.datasummary_totalskitime)).setValue(String.format("%tk:%tM:%tS", sTime, sTime, sTime));
	        
	        ((SummaryTile) findViewById(R.id.datasummary_totalskidesc)).setValue(String.format("%,dm", s.getDeltaAltitude()));
        } else {
        	Log.w(getLocalClassName(), "Data is null, summary values not set");
        }
        
        Log.d(getLocalClassName(), "DataSummaryActivity created");
    }
    
    /**
     * Callback method called when an altitude element is clicked.
     * @param v the Android View that originated this event.
     */
    public void clickAlt(View v) {
    	AppData.getAppData().track = data.getAllElements();
    	// Create altitude plot
    	AppData.getAppData().plot = new AltitudePlot(AppData.getAppData().track);
    	
    	// Open Plotter
    	startActivity(new Intent(this, XYPlotActivity.class));
    }
    
    /**
     * Callback method called when a distance element is clicked.
     * @param v the Android View that originated this event.
     */
    public void clickDistance(View v) {
    	AppData.getAppData().track = data.getAllElements();
    	// Create track plot
    	AppData.getAppData().plot = new TrackPlot(AppData.getAppData().track);
    	
    	// Open Plotter
    	startActivity(new Intent(this, XYPlotActivity.class));
    }
    
    /**
     * Callback method called when an altitude element is clicked.
     * @param v the Android View that originated this event.
     */
    public void clickSpeed(View v) {
    	AppData.getAppData().track = data.getAllElements();
    	// Create speed plot
    	AppData.getAppData().plot = new SpeedPlot(AppData.getAppData().track);
    	
    	// Open Plotter
    	startActivity(new Intent(this, XYPlotActivity.class));
    }
}