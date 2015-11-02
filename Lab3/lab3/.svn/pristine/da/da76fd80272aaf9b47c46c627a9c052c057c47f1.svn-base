/*
 * Copyright Kirill Morozov 2012
 * 
 * 
    This file is part of Mapper.

    Mapper is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Mapper is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Mapper.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package mapper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

/**
 * Mapper class for getting user input relative to an SVG map loaded by the MapLoader.
 * 
 * The Mapper's coordinate origin is in the top left hand corner.
 * @author Kirill Morozov
 *
 */
public class MapView extends View
{
    private float fieldWidth = 400;
    private float fieldHeight = 400;
    
    private GestureDetector gestureDetector;
    private Handler handler;
    
    private List<PositionListener> listeners = new ArrayList<PositionListener>();
    private List<LabeledPoint> labeledPoints = new ArrayList<LabeledPoint>();
    
    
    private PointF startPoint = new PointF(), 
            destPoint = new PointF(), 
            userPoint = new PointF(),
            selectPoint = new PointF();
    
    private List<PointF> userPath = new ArrayList<PointF>();
    
    private int SET_LOCATION_ID = 0;
    private int SET_DESTINATION_ID = 1;
    
    private List<Paint> linePaints = new ArrayList<Paint>();
    public final int[] defaultColors = {
            0xff000000, // lines
            0xffff0000, // user point
            0xffff0000, // user path
            0xff00ff00, // end
            0xffffff00, // start
            0xffff00ff, // labeled point
            };
    
    private static final int LINE_COLOR_INDEX = 0;
    private static final int USER_POINT_COLOR_INDEX = 1;
    private static final int USER_PATH_COLOR_INDEX = 2;
    private static final int END_POINT_COLOR_INDEX = 3;
    private static final int START_POINT_COLOR_INDEX = 4;
    private static final int LABELlED_POINT_COLOR_INDEX = 5;
    
    NavigationalMap map = new NavigationalMap();
    PointF scale;
    
    /**
     * Initializes a new mapper object.
     * @param context context The application context. You can get your application context by calling getApplicationContext() from your Activity
     * @param sizeX The width of the mapper
     * @param sizeY The Height of the mapper
     * @param xScale The number of pixles to use per meter in the X axis
     * @param yScale The number of pixles to use per meter in the Y axis
     */
    public MapView(Context context, float sizeX, float sizeY, float xScale, float yScale) {
        super(context);
        
        fieldWidth = sizeX;
        fieldHeight = sizeY;
        
        handler = new Handler();
        gestureDetector = new GestureDetector(context, new MapperGestureDetector(this), handler);
        
        for(int i = 0; i < defaultColors.length; i++)
            linePaints.add(new Paint());
        setColors(defaultColors);
        
        scale = new PointF(xScale, yScale);     
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
     *                      0xff00ff00,
     *                      0xff0000ff}
     * 
     * The array of colors should contain them in this order:
     * 
     * {Lines, User point, User path, End point, Start point, Labelled points}
     * 
     * @param colors
     */
    public void setColors(int[] colors)
    {
        for(int i = 0; i < Math.min(linePaints.size(), colors.length); i++)
            linePaints.get(i).setColor(colors[i]);
    }
    
    /**
     * Adds a Listener for responding to changes in the start and end point. 
     * @param listener
     */
    public void addListener(PositionListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Removes an existing listener.
     * @param listener
     */
    public void removeListener(PositionListener listener)
    {
        listeners.remove(listener);
    }
    
    /**
     * Set the "user point" of the mapper; the user point represents the current position of the user, and is drawn in a different color. 
     * @param point new value of the user point
     */
    public void setUserPoint(PointF point)
    {
        userPoint.set(point.x, point.y);
        invalidate();
    }
    
    
    /** 
     * Returns the current value of the user point.
     * @return current value of the user point
     */
    public PointF getUserPoint()
    {
        return userPoint;
    }
    
    /**
     * Set the "user point" of the mapper; the user point represents the current position of the user, and is drawn in a different color. 
     * The default color of the user point is red. 
     * @param x x-coordinate of the user point, in meters
     * @param y y-coordinate of the user point, in meters
     */
    public void setUserPoint(float x, float y)
    {
        userPoint.set(x, y);
        invalidate();
    }
    
    /**
     * Set the "user path". This is a series of points that are drawn as a line of a different color. (red by default)
     * @param points The points' location (in meters)
     */
    public void setUserPath(List<PointF> points)
    {
        userPath.clear();
        
        if(points != null)
            userPath.addAll(points);
        
        invalidate();
    }
    
    /**
     * Adds a labelled point. These are a series of points that are drawn with the indicated labels.
     * @param point The point's location (in meters). Values are copied.
     * @param label The associated label
     * @return
     */
    public LabeledPoint addLabeledPoint(PointF point, String label)
    {
        LabeledPoint ret = new LabeledPoint(point, label);
        labeledPoints.add(ret);
        invalidate();
        return ret;
    }
    
    public void removeLabeledPoint(PointF point)
    {
        labeledPoints.remove(point);
        invalidate();
    }
    
    public void removeAllLabeledPoints()
    {
        labeledPoints.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
         boolean ret = super.onTouchEvent(event);
         boolean ret2 = gestureDetector.onTouchEvent(event);
         
         return ret || ret2;
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int)fieldWidth, (int)fieldHeight);
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        
        for(List<PointF> path : map.getPaths()){
            for(int i = 0; i < path.size() - 1; i++){
                canvas.drawLine(path.get(i).x * scale.x, path.get(i).y * scale.y, 
                                path.get(i+1).x * scale.x, path.get(i+1).y * scale.y, 
                                linePaints.get(LINE_COLOR_INDEX));
            }
        }
        
        for(int i = 0; i < userPath.size() - 1; i++){
            canvas.drawLine(userPath.get(i).x * scale.x, userPath.get(i).y * scale.y, 
                            userPath.get(i+1).x * scale.x, userPath.get(i+1).y * scale.y, 
                            linePaints.get(USER_PATH_COLOR_INDEX));
        }
        
        for(LabeledPoint lp : labeledPoints){
            PointF p = lp.getPoint(); 
            canvas.drawCircle(p.x * scale.x, p.y * scale.y, 4, linePaints.get(LABELlED_POINT_COLOR_INDEX));
            canvas.drawText(lp.getLabel(), 2 + p.x * scale.x, p.y * scale.y, linePaints.get(LINE_COLOR_INDEX));
        }
        
        canvas.drawCircle(startPoint.x * scale.x, startPoint.y * scale.y, 10, linePaints.get(START_POINT_COLOR_INDEX));
        canvas.drawText("Start", 5 + startPoint.x * scale.x, startPoint.y * scale.y, linePaints.get(LINE_COLOR_INDEX));
        
        canvas.drawCircle(destPoint.x * scale.x, destPoint.y * scale.y, 10, linePaints.get(END_POINT_COLOR_INDEX));
        canvas.drawText("End", 5 + destPoint.x * scale.x, destPoint.y * scale.y, linePaints.get(LINE_COLOR_INDEX));
        
        canvas.drawCircle(userPoint.x * scale.x, userPoint.y * scale.y, 5, linePaints.get(USER_POINT_COLOR_INDEX));
        canvas.drawText("User", 2.5f + userPoint.x * scale.x, userPoint.y * scale.y, linePaints.get(LINE_COLOR_INDEX));     
        
    }


    /**
     * Sets the given PedometerMap as the map displayed by the mapper. The map file should be loaded by the MapLoader class.
     * @param newMap
     */
    public void setMap(NavigationalMap newMap)
    {
        map = newMap;
        invalidate();
    }
    
    /**
     * 
     * @return The point the user marked as the initial/origin location through the UI.
     */
    public PointF getOriginPoint() {
        return startPoint;      
    }
    
    /**
     * 
     * @return The point the user wishes to travel to.
     */
    public PointF getDestinationPoint() {
        return destPoint;
    }
    
    /** Explicitly sets the origin point.
     * May be useful in restoring the state in onCreate after a rotation.
     * 
     * @param origin Origin point for path.
     */
    public void setOriginPoint(PointF origin) {
        startPoint = origin;
    }

    /** Explicitly sets the destination point.
     * May be useful in restoring the state in onCreate after a rotation.
     * 
     * @param origin Destination point for path.
     */
    public void setDestinationPoint(PointF destination) {
        destPoint = destination;
    }

    /**
     * A helper method. Call this in your Activity's onCreateContextMenu() method. Pass it all the same parameters
     * @param menu
     * @param v
     * @param menuInfo
     */
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        menu.add(ContextMenu.NONE, SET_LOCATION_ID, ContextMenu.NONE, "Set as origin/current location");
        menu.add(ContextMenu.NONE, SET_DESTINATION_ID, ContextMenu.NONE, "Set as destination");
    }
    
    /**
     * A helper method. Call this in your Activity's onContextItemSelected() method. Pass it all the same parameters.
     * 
     * @param item
     * @return true if the selected item is understood by the MapperView. False otherwise.
     */
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean ret = true;
        
        if(id == SET_LOCATION_ID) {
            startPoint.set(selectPoint);
            
            for(PositionListener listen : listeners)
                listen.originChanged(this, startPoint);
        } else if(id == SET_DESTINATION_ID) {
            destPoint.set(selectPoint);
            
            for(PositionListener listen : listeners)
                listen.destinationChanged(this, destPoint);
                    
        } else {
            ret = false;
        }
        invalidate();
        
        
        return ret;
    }
    
    private class MapperGestureDetector extends SimpleOnGestureListener
    {
        MapView parent;
        public MapperGestureDetector(MapView parent) {
            this.parent = parent;
        }

        // I am not sure why this needs to return true. It looks like it might eat downs that are not used elsewhere?
        // Not having it, means that onLongPress is called for every click.
        public boolean onDown(MotionEvent e) {
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            
            parent.selectPoint.x = e.getX() / scale.x;
            parent.selectPoint.y = e.getY() / scale.y;
            parent.invalidate();
        }

    }

}
