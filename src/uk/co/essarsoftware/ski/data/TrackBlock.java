package uk.co.essarsoftware.ski.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import uk.co.essarsoftware.ski.data.TrackElement.Mode;

public class TrackBlock extends TreeMap<TrackElement, Track>
{
	/**
	 * Unique serializable ID
	 */
	private static final long serialVersionUID = -2911964945228662589L;
	
	private Track elems;
	private TrackModeMap modes;
	
	public TrackBlock() {
		super(new Comparator<TrackElement>() {
			public int compare(TrackElement te1, TrackElement te2) {
				return (int) ((te1 == null ? 0 : te1.getTime()) - (te2 == null ? 0 : te2.getTime()));
			}
		});
		elems = new Track();
		modes = new TrackModeMap();
	}
	
	void close() {
		// Calculate aggregates on elems
		elems.calcAggregates();
		
		// Calculate aggregates on modes
		for(Mode m : modes.keySet()) {
			modes.get(m).calcAggregates();
		}
	}
	
	/**
	 * Add a track to the set.
	 * @param track the track to add.
	 */
	public void add(Track track) {
		// Add the track, using the first element as the key
		put(track.getFirst(), track);
		// Add each element to the track and to the mode map
		for(TrackElement elem : track) {
			elems.add(elem);
			modes.add(elem);
		}
	}
	
	public Track getElements() {
		return elems;
	}
	
	public Track getElements(Mode mode) {
		return modes.get(mode);
	}
	
	public long getEndTime() {
		return (size() == 0 ? 0 : get(lastKey()).getEndTime());
	}
	
	public float getRatio(Mode mode) {
		return (float) modes.sizeFor(mode) / (float) size();
	}
	
	public long getStartTime() {
		return (size() == 0 ? 0 : get(firstKey()).getStartTime());
	}
	
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
		
		public int sizeFor(Mode mode) {
			return (containsKey(mode) ? get(mode).size() : 0);
		}
	}
}
