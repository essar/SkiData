package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.xyplot.XYDatum;
import uk.co.essarsoftware.ski.xyplot.XYPlot;
import uk.co.essarsoftware.ski.xyplot.XYTrackElement;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * <p>Android View containing an <tt>XYPlot</tt> histogram.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 * 
 * Next things to do:
 * Draw axis, with values
 * Draw grid lines
 * Colours
 * Colour legend
 */
public class XYPlotView extends View
{
	// Rendering settings
	private boolean drawAxis;
	private int plotAreaBg = 0x33808080; // Mid-gray with 33% transparency
	
	private float padding = 2.0f;
	private Renderer vr;
	private XYPlot plot;
	
	/**
	 * Create the View from plot data, within the specified context.
	 * @param context an Android <tt>Context</tt> containing this view.
	 * @param plot the <tt>XYPlot</tt> data to draw.
	 */
	public XYPlotView(Context context, XYPlot plot, Renderer vr) {
		super(context);
		this.plot = plot;
		this.vr = vr;
		this.drawAxis = false;
	}
	
	void setRenderer(Renderer vr, boolean redraw) {
		this.vr = vr;
		if(redraw) {
			invalidate();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas) {
		// Set up paint object
		Paint p = new Paint();

		/* * * * * * * * * *
		 * SCALING
		 */
		
		// Calculate draw area
		float dwX = padding;
		float dwY = padding;
		float dwWidth = (float) getWidth() - (2.0f * padding);
		float dwHeight = (float) getHeight() - (2.0f * padding);
		
		float plWidth = plot.getXAxis().getLength();
		float plHeight = plot.getYAxis().getLength();
		
		Log.d("XYPlot", String.format("Plot area set to %.2fx%.2f", plWidth, plHeight));
		Log.d("XYPlot", String.format("Drawing area set to %.2fx%.2f", dwWidth, dwHeight));
		
		if(plot.isRotatable() && (dwWidth / dwHeight > 0 && dwHeight / dwWidth < 0) || (dwWidth / dwHeight < 0 && dwHeight / dwWidth > 0)) {
			// Rotate (switch X & Y axis)
		}
		
		// Adjust scaling to fit data set - use negative Y-scale factor to put origin at bottom
		float scaleX = dwWidth / plWidth;
		float scaleY = dwHeight / plHeight;

		// Adjust scaling if proportional flag is set
		if(plot.isProportional()) {
			scaleX = Math.min(scaleX, scaleY);
			scaleY = Math.min(scaleX, scaleY);
		}
		Log.d("XYPlot", String.format("Scaling plot by %.2fx%.2f", scaleX, scaleY));
		canvas.scale(scaleX, -scaleY);
		
		// Move plot area - reset origin to bottom left of view area
		float transX = -1.0f * (plot.getXAxis().getMinValue() + dwX);
		float transY = -1.0f * (plot.getYAxis().getMaxValue() + dwY);
		Log.d("XYPlot", String.format("Translating plot to (%.2f,%.2f)", transX, transY));
		canvas.translate(transX, transY);

		
		/* * * * * * * * * *
		 * DRAWING
		 */
		
		// Draw plot area background
		p.setColor(plotAreaBg);
		RectF plotArea = new RectF(plot.getXAxis().getMinValue(), plot.getYAxis().getMinValue(), plot.getXAxis().getMaxValue(), plot.getYAxis().getMaxValue());
		canvas.drawRect(plotArea, p);
		
		// White! = A:11111111 R:11111111 G:11111111 B:11111111
		p.setColor(0xFFFFFFFF);
		
		if(drawAxis) {
			// Draw x axis
			canvas.drawLine(plot.getXAxis().getMinValue(), plot.getYAxis().getMinValue(), plot.getXAxis().getMaxValue(), plot.getYAxis().getMinValue(), p);
			// Draw y axis
			canvas.drawLine(plot.getXAxis().getMinValue(), plot.getYAxis().getMinValue(), plot.getXAxis().getMinValue(), plot.getYAxis().getMaxValue(), p);
		}
		
		
		int drawMode = 1;
		if(drawMode == 0) {
			// Draw each datum within the data set as a point on the canvas
			for(XYDatum d : plot.getData()) {
				// Set pixel colour
				p.set(vr.paintValue(d.getV(), p));
				// Translate point
				XYDatum td = plot.translatePoint(d);
				if(! plotArea.contains(td.getX(), td.getY())) {
					Log.w("XYPlot", String.format("Drawing point (%.2f,%.2f) outside of plot area", td.getX(), td.getY()));
				}
				canvas.drawPoint(td.getX(), td.getY(), p);
			}
		} else if(drawMode == 1) {
			XYDatum ld = null;
		
			// Draw each datum within the data set as a line on the canvas
			for(XYDatum d : plot.getData()) {
				if(ld == null) {
					ld = d;
				} else {
					// Render point if renderer is set
					if(vr != null) {
						// Cast datum
						XYTrackElement xyte = (XYTrackElement) d;
						// Set line colour
						float v = vr.getValue(xyte.getE());
						p.set(vr.paintValue(v, p));
					}
					// Translate point
					XYDatum td = plot.translatePoint(d);
					if(! plotArea.contains(td.getX(), td.getY())) {
						Log.w("XYPlot", String.format("Drawing point (%.2f,%.2f) outside of plot area", td.getX(), td.getY()));
					}
					canvas.drawLine(ld.getX(), ld.getY(), td.getX(), td.getY(), p);
				
					ld = td;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Default : fill parent?
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
