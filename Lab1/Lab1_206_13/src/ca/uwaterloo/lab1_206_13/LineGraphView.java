/*
 * Copyright Kirill Morozov 2012
 * 
 * 
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package ca.uwaterloo.lab1_206_13;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Pair;
import android.view.View;

/**
 * A simple implementation of a line graph widget.
 * The x axis is not user configurable, but it assumes each sample is 
 * happening at a constant frequency.
 * 
 * @author Kirill 
 *
 */
public class LineGraphView extends View
{
	private List<float[]> points = new ArrayList<float[]>();
	
	private List<Paint> linePaints = new ArrayList<Paint>();
	
	public final int[] defalutColors ={0xffff0000,
										0xff00ff00,
										0xff0000ff,
										0xff000000,
										0xffffff00,
										0xffff00ff,
										0xff00ffff,
										};
	
	private Paint graphPaint = new Paint();
	
	private final int WIDTH = 400;
	private final int HEIGHT = 400;
	private final int AXIS_WIDTH = 100;
	
	private float xScale, yScale;
	private final int maxDataWidth;
	private final List<String> labels;
	

	/**
	 * 
	 * @param context The application context. You can get your application context by calling getApplicationContext() from your Activity
	 * @param dataWidth How many data points to display before the graph starts scrolling
	 * @param labels A list of labels for the data points.
	 */
	public LineGraphView(Context context, int dataWidth, List<String> labels) {
		super(context);
		setBackgroundColor(0xffeeeeee);
		
		for(int i =0; i < labels.size(); i++)
			linePaints.add(new Paint());
		
		maxDataWidth = dataWidth;
		this.labels = labels;
		
		setColors(defalutColors);
	}
	
	/**
	 * Sets the colors for the y-values of the graph. Order should match the order of the labels.
	 * 
	 * Colors are represented by an integer that looks like this:
	 * 
	 * 0xAARRGGBB
	 * 
	 * where AA = alpha;
	 *       RR = red;
	 *       GG = green;
	 *       BB = blue;
	 *       
	 * You can initialize an array of colors like this:
	 * 
	 * private int[] colors = {0xffff0000, 
	 * 						0xff00ff00,
	 * 						0xff0000ff}
	 * 
	 * @param colors
	 */
	public void setColors(int[] colors)
	{
		for(int i = 0; i < Math.min(labels.size(), colors.length); i++)
			linePaints.get(i).setColor(colors[i]);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(WIDTH + AXIS_WIDTH, HEIGHT);
	}
	
	/**
	 * Draws the graph itself 
	 * @param canvas
	 */
	private void prepForData(Canvas canvas)
	{
		canvas.drawLine(0, HEIGHT / 2, WIDTH + AXIS_WIDTH, HEIGHT / 2, graphPaint);
		canvas.drawLine(5 + AXIS_WIDTH, 0, 5 + AXIS_WIDTH, HEIGHT, graphPaint);
		
		float maxY = 0;
		
		for(float[] pointArray : points){
			for(float point : pointArray){
				if(Math.abs(point) > maxY)
					maxY = Math.abs(point);
			}
		}
		
		xScale = WIDTH / (points.size()+1);
		yScale = (HEIGHT / 2) / maxY;
		
		canvas.drawText(Float.toString(maxY) + " m/s^2", 0, 10, graphPaint);
		canvas.drawText("-" + Float.toString(maxY) + " m/s^2", 0, HEIGHT, graphPaint);
		
		for(int i = 0; i < labels.size(); i++){
			canvas.drawText(labels.get(i) + ":", 0, 30 + i*20, graphPaint);
			canvas.drawLine(0, 35 + i*20, AXIS_WIDTH - 20, 35 + i *20, linePaints.get(i));
		}
	}
	
	private void drawLine(Canvas canvas, int count, Float rawSrc, Float rawDest, Paint paint)
	{
		float graphSrcX, graphSrcY, graphDestX, graphDestY;
		
		graphSrcX = (count - 1) * xScale + AXIS_WIDTH;
		graphSrcY = HEIGHT - (rawSrc * yScale + (HEIGHT / 2));
		graphDestX = (count) * xScale + AXIS_WIDTH;
		graphDestY = HEIGHT - (rawDest * yScale + (HEIGHT / 2));
		
		canvas.drawLine(graphSrcX, graphSrcY, graphDestX, graphDestY, paint);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		prepForData(canvas);
		
		for(int i = 1; i < points.size(); i++){
			for(int j = 0; j < points.get(i).length; j++){
				drawLine(canvas, i, points.get(i-1)[j], points.get(i)[j], linePaints.get(j));
			}
		}

	}
	
	/**
	 * Adds a set of datapoints for the next x value. The data points should be in the same 
	 * order as the array of labels this object was initialized with.
	 * @param y The array of datapoints.
	 */
	public void addPoint(float[] y)
	{
		points.add(y.clone());
		if(points.size() > maxDataWidth)
			points.remove(0);
		
		invalidate();
	}
	
	/**
	 * Adds a set of datapoints for the next x value. The data points should be in the same 
	 * order as the array of labels this object was initialized with.
	 * @param y The List of datapoints.
	 */
	public void addPoint(List<Float> y)
	{
		float[] floats = new float[y.size()];
		
		for(int i = 0; i < y.size(); i++){
			floats[i] = y.get(i);
		}
		addPoint(y);
	}

	/**
	 * Clears all the data from the graph.
	 */
	public void purge(){
		points.clear();
		invalidate();
	}
}
