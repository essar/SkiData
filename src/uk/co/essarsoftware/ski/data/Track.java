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
 * <p>Class representing a GPS track.</p>
 * <p>Linked list of <tt>TrackElement</tt> objects, ordered as a track or path.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public class Track extends LinkedList<TrackElement>
{
	/**
	 * Unique Serializable identifier.
	 */
	private static final long serialVersionUID = 8822380771220916932L;
	
	// Aggregate elements
	private float avgSpeed, dist;
	private int dAlt;
	private long endTime, startTime;
	private TrackElement hiAlt, loAlt, maxSpeed;
	
	/**
	 * Create a new, empty Track.
	 */
	public Track() {
		super();
	}
	
	/**
	 * Calculate aggregate values for elements in the set.
	 */
	void calcAggregates() {
		int ct = 0;
		// Loop through all elements in the set to calculate the aggregate values
		for(TrackElement elem : this) {
			// Overall altitude change
			dAlt += elem.getAltitudeChange();
			// Highest altitude element
			hiAlt = (hiAlt == null ? elem : (elem.getAltitude() > hiAlt.getAltitude() ? elem : hiAlt));
			// Lowest altitude element
			loAlt = (loAlt == null ? elem : (elem.getAltitude() < loAlt.getAltitude() ? elem : loAlt));
			// Average speed
			avgSpeed = ((avgSpeed * ct) + elem.getSpeed()) / (++ ct);
			// Maximum speed
			maxSpeed = (maxSpeed == null ? elem : (elem.getSpeed() > maxSpeed.getSpeed() ? elem : maxSpeed));
			// Total distance
			dist += elem.getDistance();
			// Earliest time
			startTime = (startTime == 0 ? elem.getTime() : Math.min(startTime, elem.getTime()));
			// Latest time
			endTime = (endTime == 0 ? elem.getTime() : Math.max(endTime, elem.getTime()));
		}
	}
	
	/**
	 * Get the average speed.
	 * @return the average speed on the track.
	 */
	public float getAverageSpeed() {
		return avgSpeed;
	}
	
	/**
	 * Get the overall altitude change.
	 * @return the total altitude change across the track.
	 */
	public int getDeltaAltitude() {
		return dAlt;
	}
	
	/**
	 * Get the distance of the track.
	 * @return the distance in kilometres.
	 */
	public float getDistance() {
		return dist;
	}
	
	/**
	 * Get the duration of the track.
	 * @return the duration in seconds.
	 */
	public long getDuration() {
		return endTime - startTime;
	}
	
	/**
	 * Get the end time.
	 * @return the latest time of the track.
	 */
	public long getEndTime() {
		return endTime;
	}
	
	/**
	 * Get the high altitude.
	 * @return the highest altitude reached on the track.
	 */
	public int getHighAltitude() {
		return hiAlt.getAltitude();
	}
	
	/**
	 * Get the low altitude.
	 * @return the lowest altitude reached on the track.
	 */
	public int getLowAltitude() {
		return loAlt.getAltitude();
	}
	
	/**
	 * Get the maximum speed.
	 * @return the maximum speed reached on the track.
	 */
	public float getMaxSpeed() {
		return maxSpeed.getSpeed();
	}
	
	/**
	 * Get the end time.
	 * @return the earliest time of the track.
	 */
	public long getStartTime() {
		return startTime;
	}
	
	public String toString() {
		switch(getFirst().getMode()) {
			case LIFT:
				return String.format("LIFT (+%dm; %d mins, %d secs)", dAlt, size() / 60, size() % 60);
			case SKI:
				return String.format("SKI (%,dm; %.1f kph; %d mins, %d secs)", Math.round(dist), avgSpeed, size() / 60, size() % 60);
			case STOP:
				return String.format("STOP (%d mins, %d secs)", size() / 60, size() % 60);
		}
		return super.toString();
	}
}
