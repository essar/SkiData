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
	private float avSpeed, dist, maxSpeed;
	private int dAlt, hiAlt, loAlt;
	private long endTime, startTime;
	
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
			// Count
			ct ++;
			// Overall altitude change
			dAlt += elem.getAltitudeChange();
			// Highest altitude
			hiAlt = (hiAlt == 0 ? elem.getAltitude() : Math.max(hiAlt, elem.getAltitude()));
			// Lowest altitude
			loAlt = (loAlt == 0 ? elem.getAltitude() : Math.min(loAlt, elem.getAltitude()));
			// Average speed
			avSpeed = ((avSpeed * (ct - 1)) + elem.getSpeed()) / ct;
			// Maximum speed
			maxSpeed = (maxSpeed == 0.0f ? elem.getSpeed() : Math.max(maxSpeed, elem.getSpeed()));
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
		return avSpeed;
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
		return hiAlt;
	}
	
	/**
	 * Get the low altitude.
	 * @return the lowest altitude reached on the track.
	 */
	public int getLowAltitude() {
		return loAlt;
	}
	
	/**
	 * Get the maximum speed.
	 * @return the maximum speed reached on the track.
	 */
	public float getMaxSpeed() {
		return maxSpeed;
	}
	
	/**
	 * Get the end time.
	 * @return the earliest time of the track.
	 */
	public long getStartTime() {
		return startTime;
	}
}
