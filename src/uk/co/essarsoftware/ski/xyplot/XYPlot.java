package uk.co.essarsoftware.ski.xyplot;
/*
 * Essar Software Ski Data
 * http://github.com/essar/skidata
 * 
 * -----------+----------+-----------------------------------------------------
 *  Date      | Version  | Comments
 * -----------+----------+-----------------------------------------------------
 *  17-Dec-11 | 1.0      | Initial version
 * -----------+----------+-----------------------------------------------------
 * 
 */


/**
 * <p>Class representing an XY plot, or histogram, of data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 *
 */
public class XYPlot
{
	private boolean proportional, showScale;
	private XYDataSet[] data;
	private XYAxis xAxis, yAxis;
	
	/**
	 * Create a new plot with a single data series and normal X and Y axis.
	 * @param data the data series to plot.
	 */
	public XYPlot(XYDataSet data) {
		this(new XYDataSet[] {data}, XYAxis.AXIS_TYPE_NORMAL, XYAxis.AXIS_TYPE_NORMAL);
	}
	
	XYPlot(XYDataSet[] data) {
		this(data[0]);
	}
	
	/**
	 * Create a new plot with the specified axis types. Axis can be linear (normal) or logarithmic, and can be inverted.
	 * @param data the data series to plot.
	 * @param xMode the X-axis type.
	 * @param yMode the Y-axis type.
	 * @see XYAxis#AXIS_TYPE_NORMAL
	 * @see XYAxis#AXIS_TYPE_INVERTED
	 * @see XYAxis#AXIS_TYPE_LOGARITHMIC
	 * @see XYAxis#AXIS_TYPE_INVERTED_LOG
	 */
	public XYPlot(XYDataSet[] data, int xMode, int yMode) {
		this.data = data;
		
		xAxis = createXAxis(data[0].getMinX(), data[0].getMaxX(), xMode);
		yAxis = createYAxis(data[0].getMinY(), data[0].getMaxY(), yMode);
	}
	
	protected XYAxis createXAxis(float min, float max, int mode) {
		return new XYAxis(null, min, max, mode);
	}
	
	protected XYAxis createYAxis(float min, float max, int mode) {
		return new XYAxis(null, min, max, mode);
	}
	
	/**
	 * Get the data series plotted on this axis.
	 * @return the data series to plot.
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
		return proportional;
	}
	
	public boolean isScaleShown() {
		return showScale;
	}
	
	/**
	 * Set the proportional flag for this plot.
	 * @param proportional true if this plot can only be scaled in a proportional manner, false otherwise.
	 */
	public void setProportional(boolean proportional) {
		this.proportional = proportional;
	}
	
	public void showScale(boolean showScale) {
		this.showScale = showScale;
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
		// Create transposed data set
		XYDataSet tds = data[0].transpose();
		data[0] = tds;

		// Transpose axis
		XYAxis temp = xAxis;
		xAxis = yAxis;
		yAxis = temp;
	}
}
