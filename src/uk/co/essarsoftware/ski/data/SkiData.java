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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

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
	
	// Map of blocks
	private BlockSet blocks;
	// Map of tracks
	private TrackSet tracks;
	// Map of elements by mode
	private TrackModeMap modes;
	// Track of all elements
	private Track allElements;
	
	// Current track
	private transient Track cTrack;
	// Current block
	private transient TrackSet cTrackSet;
	
	/**
	 * Initialise a new SkiData element.
	 */
	SkiData() {
		allElements = new Track();
		modes = new TrackModeMap();
		tracks = new TrackSet();
		blocks = new BlockSet();
		
		cTrack = new Track();
		cTrackSet = new TrackSet();
	}
	
	/**
	 * Close the current track and calculate aggregate values.
	 */
	private void closeTrack() {
		// Check the set is open
		if(cTrack.size() > 0) {
			// Calculate aggregate values
			cTrack.calcAggregates();
			
			// Add current track to set of tracks
			tracks.add(cTrack);
			
			// Add current track to blocks
			if(cTrackSet.size() > 0 && cTrack.getFirst().getMode() == Mode.LIFT) {
				// Add current block to set of blocks
				blocks.add(cTrackSet);
				// Reset current block
				cTrackSet = new TrackSet();
			}
			// Add track to current block
			cTrackSet.add(cTrack);
			
			// Reset current track
			cTrack = new Track();
		}
	}
	
	/**
	 * Add an element to the data set.
	 * @param elem the element to add.
	 */
	void addElement(TrackElement elem) {
		// Add element to complete track
		allElements.add(elem);
		
		// Add element to modes list
		modes.add(elem);
		
		// If this element is not the same mode as the current track, close the track
		if(cTrack.size() > 0 && elem.getMode() != cTrack.getFirst().getMode()) {
			closeTrack();
		}
		
		// Add element to current track
		cTrack.add(elem);
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
		allElements.calcAggregates();
		
		// Calculate aggregate values for each mode track
		for(Track me : modes.values()) {
			me.calcAggregates();
		}
	}
	
	/**
	 * Get the number of blocks in the data set.
	 * @return the number of blocks.
	 */
	public int blockSize() {
		return blocks.size();
	}
	
	/**
	 * Get all elements in the data set.
	 * @return a Track containing all elements.
	 */
	public Track getAllElements() {
		return allElements;
	}
	
	/**
	 * Get all elements in the given mode.
	 * @param mode the mode to search for.
	 * @return a Track containing all elements in a given mode.
	 */
	public Track getAllElements(Mode mode) {
		return modes.get(mode);
	}
	
	/**
	 * Get the tracks contained within a block identified by the specified key.
	 * @param key the <tt>Track</tt> that identifies the block.
	 * @return an <tt>ArrayList</tt> of <tt>Track</tt>s, or null if <tt>key</tt> is not a valid key.
	 */
	public ArrayList<Track> getBlock(Track key) {
		if(blocks.containsKey(key)) {
			return new ArrayList<Track>(blocks.get(key).values());
		}
		return null;
	}
	
	/**
	 * Get the elements contained within a block flattened into a single track, identified by the specified key.
	 * @param key the <tt>Track</tt> that identifies the block.
	 * @return a <tt>Track</tt>, or null if <tt>key</tt> is not a valid key.
	 */
	public Track getBlockElements(Track key) {
		if(blocks.containsKey(key)) {
			return blocks.get(key).flattenTracks();
		}
		return null;
	}
	
	/**
	 * Get a list of all tracks that represent block keys.
	 * @return an <tt>ArrayList</tt> of <tt>Track</tt>s.
	 */
	public ArrayList<Track> getBlockKeys() {
		return new ArrayList<Track>(blocks.keySet());
	}
	
	/**
	 * Gets a <tt>Track</tt> of points in a track starting with the given element.
	 */
	public Track getTrackFrom(TrackElement elem) {
		return tracks.get(elem);
	}
	
	/**
	 * Get the total number of elements in the data set.
	 * @return the size of the set.
	 */
	public int size() {
		return allElements.size();
	}
	
	/**
	 * Get the number of tracks in the data set.
	 * @return the number of tracks.
	 */
	public int trackSize() {
		return tracks.size();
	}
	
	private class BlockSet extends TreeMap<Track, TrackSet>
	{
		/**
		 * Unique serializable identifier.
		 */
		private static final long serialVersionUID = 7098078063747495972L;

		public BlockSet() {
			super(new Comparator<Track>() {
				public int compare(Track t1, Track t2) {
					return (int) ((t1 == null ? 0 : t1.getStartTime()) - (t2 == null ? 0 : t2.getStartTime()));
				}
			});
		}
		
		public void add(TrackSet block) {
			put(block.get(block.firstKey()), block);
		}
	}
	
	/**
	 * <p>Private inner class containing a set of Tracks, broken down by stages and indexed by the
	 * first point.</p>
	 *
	 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
	 * @version 1.0 (30 Nov 2011)
	 *
	 */
	private class TrackSet extends TreeMap<TrackElement, Track>
	{
		/**
		 * Unique serializable identifier. 
		 */
		private static final long serialVersionUID = -4285550362381375944L;
		
		public TrackSet() {
			super(new Comparator<TrackElement>() {
				public int compare(TrackElement te1, TrackElement te2) {
					return (int) ((te1 == null ? 0 : te1.getTime()) - (te2 == null ? 0 : te2.getTime()));
				}
			});
		}
		
		/**
		 * Add a track to the set.
		 * @param track the track to add.
		 */
		public void add(Track track) {
			// Add the track, using the first element as the key
			put(track.getFirst(), track);
		}
		
		public Track flattenTracks() {
			Track o = new Track();
			for(Track t : values()) {
				o.addAll(t);
			}
			return o;
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
