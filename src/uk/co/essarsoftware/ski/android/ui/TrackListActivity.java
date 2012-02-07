package uk.co.essarsoftware.ski.android.ui;

import java.util.ArrayList;
import java.util.Date;

import uk.co.essarsoftware.ski.data.SkiData;
import uk.co.essarsoftware.ski.data.Track;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * <p>Android Activity that displays a list of all tracks within the data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (01 Feb 2012)
 *
 */
public class TrackListActivity extends SkiDataActivity
{
	private SkiData data;
	
    /* (non-Javadoc)
     * @see uk.co.essarsoftware.ski.ui.SkiDataActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Retrieve loaded data
        data = AppData.getAppData().data;
        
        if(data != null) {
        	/*// Build track info
        	ArrayList<String> tracks = new ArrayList<String>();
        	for(TrackElement key : data.getTrackKeys()) {
        		Track t = data.getTrackFrom(key);
        		Date d = key.getTimeAsDate();
        		switch(key.getMode()) {
        			case STOP:
        				tracks.add(String.format("        STOP (%d mins, %d secs)", t.size() / 60, t.size() % 60));
        				break;
        			case LIFT:
        				tracks.add(String.format("[%k:%M] LIFT (+%dm; %d mins, %d secs)", d, d, t.getDeltaAltitude(), t.size() / 60, t.size() % 60));
        				break;
        			case SKI:
        				tracks.add(String.format("        SKI (%,dm; %.1f kph; %d mins, %d secs)", Math.round(t.getDistance()), t.getAverageSpeed(), t.size() / 60, t.size() % 60));
        				break;
        		}
        	}*/
        	
        	ExpandableListView elv = new ExpandableListView(this);
        	elv.setAdapter(new TrackListAdapter());
        	elv.setClickable(false);
        	
        	setContentView(elv);
        	
        } else {
        	Log.w(getLocalClassName(), "Data is null, tracks not loaded");
        }
        
        Log.d(getLocalClassName(), "TrackListActivity created");
    }
    
    class TrackListAdapter extends BaseExpandableListAdapter
    {
    	private ArrayList<Track> groups;
    	
    	public TrackListAdapter() {
    		groups = data.getBlockKeys();
    	}
    	
    	private TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);

            TextView textView = new TextView(TrackListActivity.this);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            return textView;
        }
    	
    	public Object getChild(int groupPosition, int childPosition) {
    		// Get group element
    		Track t = (Track) getGroup(groupPosition);
    		if(t != null) {
    			// Build list of tracks in block
    			ArrayList<Track> children = new ArrayList<Track>(data.getBlock(t).values());
    			// Return child, ignoring first element
    			return (childPosition < children.size() ? children.get(childPosition + 1) : null);
    		}
    		return null;
    	}

    	public long getChildId(int groupPosition, int childPosition) {
    		// Get child element
    		Track t = (Track) getChild(groupPosition, childPosition);
    		// Return start time as ID
    		return (t == null ? 0 : t.getStartTime());
    	}

    	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    		TextView tv = getGenericView();
    		Track t = (Track) getChild(groupPosition, childPosition);
    		if(t != null) {
    			switch(t.getFirst().getMode()) {
    				case SKI:
    					tv.setPadding(75, 5, 5, 5);
    					tv.setTextColor(0xFFFF0000);
    					tv.setText(String.format("SKI (%,dm; %.1f kph; %d mins, %d secs)", Math.round(t.getDistance()), t.getAverageSpeed(), t.size() / 60, t.size() % 60));
    					break;
    				case STOP:
    					tv.setPadding(60, 5, 5, 5);
    					tv.setTextColor(0xFFFFFFFF);
    					tv.setText(String.format("STOP (%d mins, %d secs)", t.size() / 60, t.size() % 60));
    			}
    		}
    		return tv;
    	}

    	public int getChildrenCount(int groupPosition) {
    		// Get group element
    		Track t = (Track) getGroup(groupPosition);
    		// Get block size (minus 1 as don't display the first element)
    		return (t == null ? 0 : data.getBlock(t).size() - 1);    		
    	}

    	public Object getGroup(int groupPosition) {
    		return (groupPosition < groups.size() ? groups.get(groupPosition) : null);
    	}

    	public int getGroupCount() {
    		return groups.size();
    	}

    	public long getGroupId(int groupPosition) {
    		// Get group element
    		Track t = (Track) getGroup(groupPosition);
    		// Return start time as ID
    		return (t == null ? 0 : t.getStartTime());
    	}

    	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    		TextView tv = getGenericView();
    		Track t = (Track) getGroup(groupPosition);
    		if(t != null) {
    			Date d = t.getFirst().getTimeAsDate();
    			tv.setPadding(50, 5, 5, 5);
    			switch(t.getFirst().getMode()) {
    				case LIFT:
    					tv.setTextColor(0xFF0000FF);
    					tv.setText(String.format("[%tk:%tM] LIFT (%+dm; %d mins, %d secs)", d, d, t.getDeltaAltitude(), t.size() / 60, t.size() % 60));
    					break;
    				case SKI:
    					tv.setTextColor(0xFFFF0000);
    					tv.setText(String.format("[%tk:%tM] SKI (%,dm; %.1f kph; %d mins, %d secs)", d, d, Math.round(t.getDistance()), t.getAverageSpeed(), t.size() / 60, t.size() % 60));
    					break;
    				case STOP:
    					tv.setTextColor(0xFFFFFFFF);
    					tv.setText(String.format("[%tk:%tM] STOP (%d mins, %d secs)", d, d, t.size() / 60, t.size() % 60));
    					break;
    			}
    		}
    		return tv;
    	}

    	public boolean hasStableIds() {
    		return true;
    	}

    	public boolean isChildSelectable(int groupPosition, int childPosition) {
    		return false;
    	}
    }
}