package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.ui.XYTrackElement;
import uk.co.essarsoftware.ski.xyplot.XYDatum;
import uk.co.essarsoftware.ski.xyplot.XYPlot;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * <p>Android View containing an <tt>XYPlot</tt> histogram.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 * 
 * Input - set of points to plot, either as single points or as lines. Renderer to determine colours (lines/points?)
 *
 * Should automatically apply transformations to allow raw data values to be used throughout.
 * Set a zoom factor and central point.
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
	private boolean drawBadges;
	private int plotAreaBg = 0x33808080; // Mid-gray with 33% transparency

	private float padding = 2.0f;
	private float cX, cY, zFactor;
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
		
		this.zFactor = 1.0f;
	}
	
	public XYPlotView(Context context, XYPlot plot, Renderer vr, float cX, float cY, float zFactor) {
		this(context, plot, vr);
		
	}
	
	/**
	 * Set the renderer used to draw this plot.
	 * @param vr the new <tt>Renderer</tt> implementation.
	 * @param redraw whether an immediate redraw should be triggered.
	 */
	void setRenderer(Renderer vr, boolean redraw) {
		this.vr = vr;
		if(redraw) {
			invalidate();
		}
	}
	
	void showBadges(boolean visible) {
		this.drawBadges = visible;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas) {
		// Set up paint object
		Paint p = new Paint();
		
		//
		float axisPaddingX = (plot.isScaleShown() ? 50.0f : 0.0f);
		float axisPaddingY = (plot.isScaleShown() ? 50.0f : 0.0f);

		/* * * * * * * * * *
		 * SCALING
		 */
		
		// Calculate draw area
		float dwWidth = (float) getWidth() - (2.0f * padding) - axisPaddingX;
		float dwHeight = (float) getHeight() - (2.0f * padding) - axisPaddingY;
		
		// Get raw plot size
		float plX = plot.getXAxis().getMinValue();
		float plY = plot.getYAxis().getMinValue();
		float plWidth = plot.getXAxis().getLength();
		float plHeight = plot.getYAxis().getLength();
		
		// Adjust scaling to fit plot to draw area
		float scaleX = dwWidth / plWidth;
		float scaleY = dwHeight / plHeight;

		// Adjust scaling if proportional flag is set
		if(plot.isProportional()) {
			scaleX = Math.min(scaleX, scaleY);
			scaleY = Math.min(scaleX, scaleY);
		}
		Log.d("XYPlot", String.format("Plot area set to %.2fx%.2f", plWidth, plHeight));
		Log.d("XYPlot", String.format("Drawing area set to %.2fx%.2f", dwWidth, dwHeight));
		Log.d("XYPlot", String.format("Scaling plot by %.2fx%.2f", scaleX, scaleY));

		// Move plot to origin
		//canvas.translate(-plX, -plY);
		
		// Use negative Y-scale factor to invert y-axis
		canvas.scale(scaleX, -scaleY);
		
		// Move plot area - reset origin to bottom left of view area
		float transX = -1.0f * (plX - ((padding + axisPaddingX) / scaleX));
		//float transX = ((padding + axisPaddingX) / scaleX);
		float transY = -1.0f * (plY + plHeight + ((2 * padding) / scaleY));
		//float transY = -1.0f * ((dwHeight + padding) / scaleY); 
		Log.d("XYPlot", String.format("Translating plot to (%.2f,%.2f)", transX, transY));
		canvas.translate(transX, transY);

		
		/* * * * * * * * * *
		 * DRAWING
		 */
		
		// Draw plot area background
		p.setColor(plotAreaBg);
		//RectF plotArea = new RectF(plot.getXAxis().getMinValue(), plot.getYAxis().getMinValue(), plot.getXAxis().getMaxValue(), plot.getYAxis().getMaxValue());
		RectF plotArea = new RectF(plX, plY, plX + plWidth, plY + plHeight);
		canvas.drawRect(plotArea, p);
		
		if(plot.isScaleShown()) {
			// White! = A:11111111 R:11111111 G:11111111 B:11111111
			p.setColor(0xFFFFFFFF);
			p.setTextSize(10.0f);
			
			// Draw x axis
			float xAxisY = plot.getYAxis().getMinValue() - (1.0f / scaleY);
			
			canvas.drawLine(plot.getXAxis().getMinValue(), xAxisY, plot.getXAxis().getMaxValue(), xAxisY, p);
			for(float f : plot.getXAxis().getTickValues()) {
				canvas.drawLine(f, xAxisY, f, xAxisY - (10.0f / scaleY), p);
			}
			
			// Draw y axis
			float yAxisX = plot.getYAxis().getMinValue() - (1.0f / scaleY);
			
			canvas.drawLine(yAxisX, plot.getYAxis().getMinValue(), yAxisX, plot.getYAxis().getMaxValue(), p);
			for(float f : plot.getYAxis().getTickValues()) {
				canvas.drawLine(yAxisX, f, yAxisX - (10.0f / scaleX), f, p);
			}
			
			
			/* * * * * * * * * *
			 * Axis Text
			 */

			// Remove scaling to draw text - must be done manually
			canvas.scale(1.0f / scaleX, -1.0f / scaleY);
			// Set anti-aliasing on
			p.setAntiAlias(true);
				
			// X-axis values
			for(int i = 0; i < plot.getXAxis().getTickValues().length; i ++) {
				float f = plot.getXAxis().getTickValues()[i];
				float x = f * scaleX;
				float y = (xAxisY * -scaleY) + 20.0f;
				if(i == 0) {
					p.setTextAlign(Align.LEFT);
				} else if(i == plot.getXAxis().getTickValues().length - 1) {
					p.setTextAlign(Align.RIGHT);
				} else {
					p.setTextAlign(Align.CENTER);
				}
				canvas.drawText(plot.getXAxis().getLabel(f), x, y, p);
			}
			// X-axis name
			{
				float x = plot.getXAxis().getMaxValue() * scaleX;
				float y = (xAxisY * -scaleY) + 30.0f;
				p.setTextAlign(Align.RIGHT);
				canvas.drawText(plot.getXAxis().getAxisName(), x, y, p);
			}
				
			// Y-axis values
			for(int i = 0; i < plot.getYAxis().getTickValues().length; i ++) {
				p.setTextAlign(Align.RIGHT);
				float f = plot.getYAxis().getTickValues()[i];
				float x = (yAxisX * scaleX) - 12.0f;
				float y = f * -scaleY;
				if(i > 0) {
					y -= p.getFontMetrics().top;
				}
				canvas.drawText(plot.getYAxis().getLabel(f), x, y, p);
			}
			// Y-axis name
			{
				float x = (yAxisX * scaleX) - 25.0f;
				float y = (plot.getYAxis().getMinValue() + (plot.getYAxis().getLength() / 2.0f)) * -scaleY;
				p.setTextAlign(Align.CENTER);
				canvas.rotate(90.0f, x, y);
				canvas.drawText(plot.getYAxis().getAxisName(), x, y, p);
				canvas.rotate(-90.0f, x, y);
			}
				
			// Reset scaling
			canvas.scale(scaleX, -scaleY);
			// Remove anti-aliasing
			p.setAntiAlias(false);
		}
		
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
			
		// Draw each datum within the data set as a point on the canvas
		/*for(XYDatum d : plot.getData()) {
			// Set pixel colour
			//p.set(vr.paintValue(d.getV(), p));
			p.setColor(0xFF00FF00);
			//p.setStrokeWidth(3.0f);
			// Translate point
			XYDatum td = plot.translatePoint(d);
			if(! plotArea.contains(td.getX(), td.getY())) {
				Log.w("XYPlot", String.format("Drawing point (%.2f,%.2f) outside of plot area", td.getX(), td.getY()));
			}
			canvas.drawPoint(td.getX(), td.getY(), p);
		}*/
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Default : fill parent?
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void zoomTo(float cX, float cY, float zFactor, boolean redraw) {
		this.cX = cX;
		this.cY = cY;
		this.zFactor = zFactor;
		
		if(redraw) {
			invalidate();
		}
	}
}
