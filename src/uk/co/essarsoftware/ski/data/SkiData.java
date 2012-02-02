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
	private TrackBlockSet blocks;
	// Map of tracks
	private TrackBlock tracks;
	
	// Current track
	private transient Track cTrack;
	// Current block
	private transient TrackBlock cBlock;
	
	/**
	 * Initialise a new SkiData element.
	 */
	SkiData() {
		tracks = new TrackBlock();
		blocks = new TrackBlockSet();
		
		cTrack = new Track();
		cBlock = new TrackBlock();
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
			if(cBlock.size() > 0 && cTrack.getFirst().getMode() == Mode.LIFT) {
				// Close current block
				cBlock.close();
				// Add current block to set of blocks
				blocks.add(cBlock);
				// Reset current block
				cBlock = new TrackBlock();
			}
			// Add track to current block
			cBlock.add(cTrack);
			
			// Reset current track
			cTrack = new Track();
		}
	}
	
	/**
	 * Add an element to the data set.
	 * @param elem the element to add.
	 */
	void addElement(TrackElement elem) {
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
		
		// Calculate aggregate values on tracks
		tracks.close();
	}
	
	/**
	 * Get all elements in the data set.
	 * @return a Track containing all elements.
	 */
	public Track getAllElements() {
		return tracks.getElements();
	}
	
	/**
	 * Get all elements in the given mode.
	 * @param mode the mode to search for.
	 * @return a Track containing all elements in a given mode.
	 */
	public Track getAllElements(Mode mode) {
		return tracks.getElements(mode);
	}
	
	/**
	 * Get the tracks contained within a block identified by the specified key.
	 * @param key the <tt>Track</tt> that identifies the block.
	 * @return a <tt>TrackBlock</tt>, or null if <tt>key</tt> is not a valid key.
	 */
	public TrackBlock getBlock(Track key) {
		return blocks.get(key);
	}
	
	/**
	 * Get the elements contained within a block flattened into a single track, identified by the specified key.
	 * @param key the <tt>Track</tt> that identifies the block.
	 * @return a <tt>Track</tt>, or null if <tt>key</tt> is not a valid key.
	 */
	public Track getBlockElements(Track key) {
		if(blocks.containsKey(key)) {
			return blocks.get(key).getElements();
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
		return cTrack.size() + tracks.getElements().size();
	}
	
	private class TrackBlockSet extends TreeMap<Track, TrackBlock>
	{
		/**
		 * Unique serializable identifier.
		 */
		private static final long serialVersionUID = 7098078063747495972L;

		public TrackBlockSet() {
			super(new Comparator<Track>() {
				public int compare(Track t1, Track t2) {
					return (int) ((t1 == null ? 0 : t1.getStartTime()) - (t2 == null ? 0 : t2.getStartTime()));
				}
			});
		}
		
		public void add(TrackBlock block) {
			put(block.get(block.firstKey()), block);
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
