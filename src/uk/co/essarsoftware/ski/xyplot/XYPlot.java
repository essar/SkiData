package uk.co.essarsoftware.ski.xyplot;



/**
 * <p>Class representing an XY plot, or histogram, of data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 *
 */
public class XYPlot
{
	private boolean rotatable;
	private int scaleMode;
	private XYDataSet[] data;
	private XYAxis xAxis, yAxis;

	
	public static final int SCALE_MODE_NONE = 0x00;
	public static final int SCALE_MODE_PROPORTIONAL = 0x01;
	public static final int SCALE_MODE_STRETCH = 0x02;
	
	/**
	 * Create a new plot with normal X and Y axis.
	 * @param data the data set to plot.
	 */
	public XYPlot(XYDataSet data) {
		this(new XYDataSet[] {data}, XYAxis.AXIS_TYPE_NORMAL, XYAxis.AXIS_TYPE_NORMAL);
	}
	
	XYPlot(XYDataSet[] data) {
		this(data[0]);
	}
	
	/**
	 * Create a new plot with the specified axis types.
	 * @param data the data set to plot.
	 * @param xMode the X-axis type.
	 * @param yMode the Y-axis type.
	 */
	public XYPlot(XYDataSet[] data, int xMode, int yMode) {
		this.data = data;
		
		xAxis = new XYAxis(data[0].getMinX(), data[0].getMaxX(), xMode);
		yAxis = new XYAxis(data[0].getMinY(), data[0].getMaxY(), yMode);
	}
	
	/**
	 * Get the data set plotted on this axis.
	 * @return the data set to plot.
	 */
	public XYDataSet getData() {
		return data[0];
	}

	/**
	 * Get the X-axis for this plot.
	 * @return the X-axis (horizontal).
	 */
	public XYAxis getXAxis() {
		return xAxis;
	}
	
	/**
	 * Get the Y-axis for this plot.
	 * @return the Y-axis (vertical).
	 */
	public XYAxis getYAxis() {
		return yAxis;
	}
	
	/**
	 * Get the proportional flag for this plot.
	 * @return true if this plot can only be scaled in a proportional manner, false otherwise.
	 */
	public boolean isProportional() {
		return scaleMode == SCALE_MODE_PROPORTIONAL;
	}
	
	/**
	 * Get the rotatable flag for this plot.
	 * @return true if this plot can be rotated, false otherwise.
	 */
	public boolean isRotatable() {
		return rotatable;
	}
	
	/**
	 * Get the scalable flag for this plot.
	 * @return true if the plot can be scaled, false otherwise.
	 */
	public boolean isScalable() {
		return scaleMode == SCALE_MODE_STRETCH;
	}
	
	/**
	 * Set the proportional flag for this plot.
	 * @param proportional true if this plot can only be scaled in a proportional manner, false otherwise.
	 */
	public void setProportional(boolean proportional) {
		scaleMode = (proportional ? SCALE_MODE_PROPORTIONAL : SCALE_MODE_STRETCH);
	}
	
	/**
	 * Set the rotatable flag for this plot.
	 * @param rotatable true if this plot can be rotated, false otherwise.
	 */
	public void setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
	}
	
	/**
	 * Set the scalable flag for this plot.
	 * @param scalable true if this plot can be scaled, false otherwise.
	 */
	public void setScalable(boolean scalable) {
		scaleMode = (scalable ? SCALE_MODE_STRETCH : SCALE_MODE_NONE);
	}
	
	/**
	 * Translate the given point against the plot axis.
	 * @param in the original datum plot to translate. 
	 * @return the translated datum plot.
	 */
	public XYDatum translatePoint(XYDatum in) {
		return new XYDatum(xAxis.translateValue(in.getX()), yAxis.translateValue(in.getY()), in.getV());
	}
	
	/**
	 * Transpose the plot to make the x-axis the y-axis, and visa versa.
	 */
	public void transposePlot() {
		XYAxis temp = xAxis;
		xAxis = yAxis;
		yAxis = temp;
	}
}
