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
import java.io.Serializable;
import java.util.HashMap;

import uk.co.essarsoftware.ski.data.TrackElement.Mode;

/**
 * <p>Data structure for holding ski <tt>TrackElement</tt> objects.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public class SkiData implements Serializable
{
	/**
	 * Unique serializable identifier. 
	 */
	private static final long serialVersionUID = 2372621609920844359L;
	
	// Map of tracks
	private TrackSet tracks;
	// Map of elements by mode
	private TrackModeMap modeElems;
	// Track of all elements
	private Track allElems;
	
	// Current track
	private transient Track cTrack;
	
	/**
	 * Initialise a new SkiData element.
	 */
	SkiData() {
		allElems = new Track();
		modeElems = new TrackModeMap();
		tracks = new TrackSet();
		
		cTrack = new Track();
	}
	
	/**
	 * Add an element to the data set.
	 * @param elem the element to add.
	 */
	void addElement(TrackElement elem) {
		// If this element is not the same mode as the current track, close the track
		if(cTrack.size() > 0  && elem.getMode() != cTrack.getFirst().getMode()) {
			closeTrack();
		}
		
		// Add the element to the current track
		cTrack.add(elem);
		// Add element to complete track
		allElems.add(elem);
		// Add element to mode list
		modeElems.add(elem);
	}
	
	/**
	 * Close all tracks and calculate aggregate values.
	 */
	void closeAll() {
		// Close any open set
		if(cTrack.size() > 0) {
			closeTrack();
		}
		
		// Calculate aggregate values for all element track
		allElems.calcAggregates();
		
		// Calculate aggregate values for each mode track
		for(Track me : modeElems.values()) {
			me.calcAggregates();
		}
	}
	
	/**
	 * Close the current track and calculate aggregate values.
	 */
	void closeTrack() {
		// Check the set is open
		if(cTrack.size() > 0) {
			// Calculate aggregate values
			cTrack.calcAggregates();
			
			// Add current track to set of tracks
			tracks.add(cTrack);
			
			// Debug output
			//System.out.printf("Loaded %d points into a %s set.\n", currentSet.size(), currentSet.getFirst().getMode());
			//System.out.printf("Alt:%d-%d (%d), MaxSpeed:%2.2f, AveSpeed:%2.2f, Dist:%2.2f\n", currentSet.getFirst().getAltitude(), currentSet.getLast().getAltitude(), currentSet.getDeltaAltitude(), currentSet.getMaxSpeed(), currentSet.getAverageSpeed(), currentSet.getDistance());
			
			// Reset current track
			cTrack = new Track();
		}
	}
	
	/**
	 * Gets a <tt>Track</tt> of points in a chunk starting with the given element.
	 */
	public Track getPointsFrom(TrackElement elem) {
		return tracks.get(elem);
	}
	
	/**
	 * Get all elements in the given mode.
	 * @param mode the mode to search for.
	 * @return a Track containing all elements in a given mode.
	 */
	public Track getAllInMode(Mode mode) {
		return modeElems.get(mode);
	}
	
	/**
	 * Get all elements in the data set.
	 * @return a Track containing all elements.
	 */
	public Track getAllElements() {
		return allElems;
	}
	
	/**
	 * Get the number of elements in the data set.
	 * @return the size of the set.
	 */
	public int size() {
		return allElems.size();
	}
	
	/**
	 * <p>Private inner class containing a set of Tracks, broken down by stages and indexed by the
	 * first point.</p>
	 *
	 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
	 * @version 1.0 (30 Nov 2011)
	 *
	 */
	private class TrackSet extends HashMap<TrackElement, Track>
	{
		/**
		 * Unique serializable identifier. 
		 */
		private static final long serialVersionUID = -3368391294058227473L;

		/**
		 * Add a track to the set.
		 * @param track the track to add.
		 */
		public void add(Track track) {
			// Add the track, using the first element as the key
			put(track.getFirst(), track);
		}
	}
	
	/**
	 * <p>Private inner class mapping tracks by mode.</p>
	 *
	 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
	 * @version 1.0 (30 Nov 2011)
	 */
	private class TrackModeMap extends HashMap<Mode, Track>
	{
		/**
		 * Unique serializable identifier.
		 */
		private static final long serialVersionUID = 6161858796973305838L;

		/**
		 * Add a track element to the map.
		 * @param elem the <tt>TrackElement</tt> to add.
		 */
		public void add(TrackElement elem) {
			if(! containsKey(elem.getMode())) {
				// Key not found, create new track
				put(elem.getMode(), new Track());
			}
			// Add to the end of existing track
			get(elem.getMode()).addLast(elem);
		}
	}
}
