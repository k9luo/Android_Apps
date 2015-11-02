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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.PointF;

/**
 * A representation of a map. All values stored in meters.
 * @author Kirill
 *
 */
public class NavigationalMap 
{
    private List<List<PointF>> paths = new ArrayList<List<PointF>>();
    
    /**
     * Returns the set of walls in this map.
     */
    public List<List<PointF>> getPaths() {
        return Collections.unmodifiableList(paths);
    }
    
    /**
     * Add a wall to the map.
     */
    void addPath(List<PointF> p) {
        paths.add(p);
    }
    
    /**
     * Calculates where a given line segment intersects lines on the map.
     * 
     * @param start the start point of the line to calculate (in meters)
     * @param end the end point of the line to calculate (in meters)
     * @return A list of points where the given line intersects with the map lines. 
     * Sorted by distance from the start point.
     */
    public List<InterceptPoint> calculateIntersections(final PointF start, final PointF end)
    {
        List<InterceptPoint> ret = new ArrayList<InterceptPoint>();
        LineSegment query = new LineSegment(start, end);
        
        for(List<PointF> path : getPaths()){
            for(int i = 0; i < path.size() - 1; i++){
                LineSegment segm = new LineSegment(path.get(i), path.get(i+1));
                
                if(segm.theSame(query))
                    continue;
                
                PointF p = query.findIntercept(segm);
                if(p != null){
                    ret.add(new InterceptPoint(segm, p));
                }
                
            }
        }
        
        Collections.sort(ret, new Comparator<InterceptPoint>(){
            public int compare(InterceptPoint arg0, InterceptPoint arg1) {
                float distStart0 = VectorUtils.distance(start, arg0.getPoint());
                float distStart1 = VectorUtils.distance(start, arg1.getPoint());
                if(VectorUtils.isZero(distStart0 - distStart1))
                    return 0;
                else if(distStart0  < distStart1)
                    return -1;
                else
                    return 1;
            }
        });
        
        return ret;
    }

    /**
     * Returns a list of line segments from the map; all start at the given point.
     * 
     * @param point The point from which to calculate geometry
     * @return
     *  All the geometry that starts at or passes through this point as line segments.
     * The Start member of each line segment will satisfy: 
     * 
     * {@code
     * VectorUtils.areEqual(point, start);}
     * 
     */
    public List<LineSegment> getGeometryAtPoint(PointF point)
    {
        List<LineSegment> geo = getGeometry();
        List<LineSegment> ret = new ArrayList<LineSegment>();
        
        for (LineSegment seg : geo) {
            if(VectorUtils.areEqual(seg.start, point)) {
                ret.add(seg);
            } else if(VectorUtils.areEqual(seg.end, point)) {
                ret.add(new LineSegment(seg.end, seg.start));
            } else if(seg.containsPoint(point)) {
                ret.add(new LineSegment(point, seg.start));
                ret.add(new LineSegment(point, seg.end));
            }
            
        }
        return ret;
    }

    /**
     * Returns all of the map information in the form of LineSegments. 
     * If you want to do something clever with the geometry yourself, you can use this method.
     * @return
     * All the data in the loaded map as line segments
     */
    public List<LineSegment> getGeometry(){
        List<LineSegment> ret = new ArrayList<LineSegment>();
        
        for(List<PointF> path : paths){
            for(int i = 0; i < path.size() - 1; i++){
                LineSegment segm = new LineSegment(path.get(i), path.get(i+1));
                ret.add(segm);
            }
        }
        
        return ret;
    }
}
