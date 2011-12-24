package uk.co.essarsoftware.ski.xyplot;

import java.util.Collection;
import java.util.LinkedList;

/**
 * <p>Class containing a collection of <tt>XYDatum</tt> points, for plotting within a XY chart or histogram.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 *
 */
public class XYDataSet extends LinkedList<XYDatum>
{
	/**
	 * Serializable identifier.
	 */
	private static final long serialVersionUID = -778885092230851527L;
	// Maximum and minimum values
	private float maxX, maxY, minX, minY;
	// Data series name
	private String seriesName;
	
	/**
	 * Create a new empty unnamed data set.
	 */
	public XYDataSet() {
		super();
	}

	public XYDataSet(String seriesName) {
		this();
		this.seriesName = seriesName;
	}
	
	/**
	 * Process the loaded point, to update the maximum and minimum values.
	 * @param d
	 */
	private void processPoint(XYDatum d) {
		if(size() == 1) {
			// First point
			minX = d.getX();
			maxX = d.getX();
			minY = d.getY();
			maxY = d.getY();
		}
		minX = Math.min(minX, d.getX());
		maxX = Math.max(maxX, d.getX());
		minY = Math.min(minY, d.getY());
		maxY = Math.max(maxY, d.getY());
	}
	
	/**
	 * Transpose the data set to turn x values into y values and visa versa.
	 */
	XYDataSet transpose() {
		XYDataSet ts = new XYDataSet(seriesName);
		
		for(XYDatum d : this) {
			ts.add(d.transpose());
		}
		
		return ts;
	}
	
	/* (non-Javadoc)
	 * @see java.util.LinkedList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int location, XYDatum object) {
		super.add(location, object);
		processPoint(object);
	}

	/* (non-Javadoc)
	 * @see java.util.LinkedList#add(java.lang.Object)
	 */
	@Override
	public boolean add(XYDatum object) {
		if(super.add(object)) {
			processPoint(object);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.LinkedList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends XYDatum> collection) {
		if(super.addAll(collection)) {
			for(XYDatum object : collection) {
				processPoint(object);
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.LinkedList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int location, Collection<? extends XYDatum> collection) {
		if(super.addAll(location, collection)) {
			for(XYDatum object : collection) {
				processPoint(object);
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.LinkedList#addFirst(java.lang.Object)
	 */
	@Override
	public void addFirst(XYDatum object) {
		super.addFirst(object);
		processPoint(object);
	}

	/* (non-Javadoc)
	 * @see java.util.LinkedList#addLast(java.lang.Object)
	 */
	@Override
	public void addLast(XYDatum object) {
		super.addLast(object);
		processPoint(object);
	}
	
	/**
	 * Get the maximum X-coordinate in the set.
	 * @return the maximum x-coordinate.
	 */
	float getMaxX() {
		return maxX;
	}
	
	/**
	 * Get the maximum Y-coordinate in the set.
	 * @return the maximum y-coordinate.
	 */
	float getMaxY() {
		return maxY;
	}

	/**
	 * Get the minimum X-coordinate in the set.
	 * @return the minimum x-coordinate.
	 */
	float getMinX() {
		return minX;
	}
	
	/**
	 * Get the minimum Y-coordinate in the set.
	 * @return the minimum y-coordinate.
	 */
	float getMinY() {
		return minY;
	}
	
	public String getSeriesName() {
		return seriesName;
	}
}
