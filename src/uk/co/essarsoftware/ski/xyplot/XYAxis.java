package uk.co.essarsoftware.ski.xyplot;


public class XYAxis
{
	/** Constant indicating normal (left to right, top to bottom) axis. */
	public static final int AXIS_TYPE_NORMAL = 0x00;
	/** Constant indicating inverted (right to left, bottom to top) axis. */
	public static final int AXIS_TYPE_INVERTED = 0x01;
	/** Constant indicating logarithmic normal (left to right, top to bottom) axis. */
	public static final int AXIS_TYPE_LOGARITHMIC = 0x02;
	/** Constant indicating inverted logarithmic (right to left, bottom to top) axis. */
	public static final int AXIS_TYPE_INVERTED_LOG = 0x03;
	
	private float tickInterval;
	private float minValue, maxValue;
	private int axisType;
	private String axisName;
	
	/**
	 * Create a new normal axis.
	 * @param axisName the name of this axis.
	 * @param minValue the lowest value in the data series.
	 * @param maxValue the highest value in the data series.
	 */
	XYAxis(String axisName, float minValue, float maxValue) {
		this(axisName, minValue, maxValue, AXIS_TYPE_NORMAL);
	}
	
	/**
	 * Create a new axis of the specified type.
	 * @param axisName the name of this axis.
	 * @param minValue the lowest value in the data series.
	 * @param maxValue the highest value in the data series.
	 * @param axisType constant indicating axis type.
	 */
	protected XYAxis(String axisName, float minValue, float maxValue, int axisType) {
		this.axisName = axisName;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.axisType = axisType;
		
		// Set tick interval to 0 - off
		this.tickInterval = Float.MAX_VALUE;
	}
	
	/**
	 * Translate the input value against its axis.
	 * @param in the original datum plot to translate. 
	 * @return the translated datum plot.
	 */
	float translateValue(float in) {
		if(isInverted()) {
			// Translate inverted axis
			in = minValue + (maxValue - in);
		}
		if(isLogarithmic()) {
			// Translate log axis
			in = (float) Math.log(in);
		}
		return in;
	}
	
	public String getAxisName() {
		return axisName;
	}
	
	public String getLabel(float v) {
		return String.format("%,.2f", v);
	}
	
	/**
	 * Get the length of the axis.
	 * @return the length of this axis.
	 */
	public float getLength() {
		return Math.abs(maxValue - minValue) + 1.0f;
	}
	
	/**
	 * Get the maximum value in the data set.
	 * @return the highest value in the data series.
	 */
	public float getMaxValue() {
		return maxValue;
	}
	
	/**
	 * Get the minimum value in the data set.
	 * @return the lowest value in the data series.
	 */
	public float getMinValue() {
		return minValue;
	}
	
	/**
	 * Get the interval between value 'ticks'
	 * @return the interval between ticks, or zero if no ticks are displayed.
	 */
	public float getTickInterval() {
		return tickInterval;
	}
	
	public float[] getTickValues() {
		if(tickInterval == 0) {
			// Tick interval of zero turns off tick markings
			return new float[0];
		} else if(tickInterval > getLength()) {
			// Tick interval is greater than length of axis, show ticks at start and end of axis
			return new float[] { minValue, maxValue };
		} else {
			// Calculate ticks within axis
			float t = ((float) Math.ceil(minValue / tickInterval) + 1);
			int s = (int) Math.ceil(getLength() / tickInterval);
			
			float[] ticks = new float[s];
			for(int i = 0; i < ticks.length; i ++) {
				ticks[i] = (t + i) * tickInterval;
			}
			return ticks;
		}
	}
	
	/**
	 * Get the axis type. 
	 * @return constant indicating axis type.
	 */
	public int getType() {
		return axisType;
	}
	
	/**
	 * Check if the axis is inverted.
	 * @return true if the axis is inverted, false otherwise.
	 */
	public boolean isInverted() {
		return (axisType & AXIS_TYPE_INVERTED) > 0;
	}
	
	/**
	 * Check if the axis is logarithmic.
	 * @return true if the axis is logarithmic, false otherwise.
	 */
	public boolean isLogarithmic() {
		return (axisType & AXIS_TYPE_LOGARITHMIC) > 0;
	}
	
	public void setAxisName(String axisName) {
		this.axisName = axisName;
	}
	
	/**
	 * Set the interval between value 'ticks' on this axis.
	 * @param tickInterval the interval between tick markings, or zero to display no ticks.
	 */
	public void setTickInterval(float tickInterval) {
		this.tickInterval = tickInterval;
	}
}
