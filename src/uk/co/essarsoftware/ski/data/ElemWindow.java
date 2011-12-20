package uk.co.essarsoftware.ski.data;
/*
 * Essar Software Ski Data
 * http://github.com/essar/skidata
 * 
 * -----------+----------+-----------------------------------------------------
 *  Date      | Version  | Comments
 * -----------+----------+-----------------------------------------------------
 *  30-Nov-11 | 1.0      | Initial version
 * -----------+----------+-----------------------------------------------------
 * 
 */
import java.util.LinkedList;


/**
 * <p>Moving window of elements, used to look forward and analyse data paths.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
class ElemWindow
{
	// Next datum point
	private Datum d;
	// Window of elements
	private LinkedList<TrackElement> data;
	
	/**
	 * Initialise a new <tt>ElemWindow</tt> with the specified capacity, starting with the given <tt>Datum</tt> point.
	 * Fills the window with datum elements.
	 * @param dIn the first datum point in the track.
	 * @param capacity the capacity or size of the window.
	 */
	ElemWindow(Datum dIn, int capacity) {
		this.d = dIn;
		data = new LinkedList<TrackElement>();
		while(data.size() < capacity && d != null) {
			data.addLast(new TrackElement(d));
			d = d.getNext();
		}
	}

	/**
	 * Retrieve the first <tt>TrackElement</tt> in the window and adds the new datum point, if one is available.
	 * @return the next available track element object.
	 */
	TrackElement next() {
		if(data.size() == 0) {
			// Nothing in the list to return
			return null;
		}
		if(d != null) {
			// Add element to end of list
			data.addLast(new TrackElement(d));
			d = d.getNext();
		}
		// Return and remove first element of list
		return data.removeFirst();
	}
	
	/**
	 * Count change in altitude across points in the window.
	 * @return the overall altitude change.
	 */
	int ascent() {
		int alt = 0;
		for(TrackElement elem : data) {
			// Get altitude delta
			alt += elem.getAltitudeChange();
		}
		return alt;
	}
	
	/**
	 * Count how many elements within the window are ascending.
	 * @return a value between 0 and 1 as a ratio of ascending points.
	 */
	float ascending() {
		float ct = 0;
		for(TrackElement elem : data) {
			// Increment count if altitude has increased
			ct += (elem.getAltitudeChange() > 0 ? 1 : 0);
		}
		// Return ratio of positives over total number of points
		return ct / (float) data.size();
	}
	
	/**
	 * Count how many elements within the window are descending.
	 * @return a value between 0 and 1 as a ratio of descending points.
	 */
	float descending() {
		float ct = 0;
		for(TrackElement elem : data) {
			// Increment count if altitude has decreased
			ct += (elem.getAltitudeChange() < 0 ? 1 : 0);
		}
		// Return ratio of positives over total number of points
		return ct / (float) data.size();
	}
	
	/**
	 * Count how many elements within the window do not have altitude change.
	 * @return a value between 0 and 1 as a ratio of flat points.
	 */
	float flat() {
		float ct = 0;
		for(TrackElement elem : data) {
			// Increment count if altitude has not changed
			ct += (elem.getAltitudeChange() == 0 ? 1 : 0);
		}
		// Return ratio of positives over total number of points
		return ct / (float) data.size();
	}
	
	/**
	 * Count how many elements within the window are moving.
	 * @return a value between 0 and 1 as a ratio of moving points.
	 */
	float moving() {
		float ct = 0;
		for(TrackElement elem : data) {
			// Increment if element has moved
			ct += (elem.getDistance() > 0 ? 1 : 0);
		}
		// Return ratio of positives over total number of points
		return ct / (float) data.size();
	}
	
	/**
	 * Count how many elements within the window are stopped.
	 * @return a value between 0 and 1 as a ratio of stationary points.
	 */
	float stopped() {
		float ct = 0;
		for(TrackElement elem : data) {
			// Increment if element has not moved
			ct += (elem.getDistance() == 0 && elem.getAltitudeChange() == 0 ? 1 : 0);
		}
		// Return ratio of positives over total number of points
		return ct / (float) data.size();
	}
}
