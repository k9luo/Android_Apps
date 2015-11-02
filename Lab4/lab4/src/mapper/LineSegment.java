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

import android.graphics.PointF;

/**
 * A class that represents a line segment.
 * @author Kirill Morozov
 *
 */
public class LineSegment 
{
    public final PointF start, end;
    public final float m, b; // as in, y = mx + b.
    
    /** Creates a line segment with given start and end points. */
    public LineSegment(PointF start, PointF end) {
        this.start = new PointF(start.x, start.y);
        this.end = new PointF(end.x, end.y);
        
        m = (end.y - start.y) / (end.x - start.x);
        b = start.y - (start.x * m);
    }
    
    private boolean isPointInSegment(float x, float y) {
        return ( (x >= Math.min(start.x, end.x) && x <= Math.max(start.x, end.x)) || VectorUtils.isZero(x - start.x) || VectorUtils.isZero(x - end.x)) && 
                ( (y >= Math.min(start.y, end.y) && y <= Math.max(start.y, end.y)) || VectorUtils.isZero(y - start.y) || VectorUtils.isZero(y - end.y));
    }
    
    /**
     * Return the point where this LineSegment intersects another line segment.
     * @param other the other line segment.
     * @return the point where the two segments intersect, or null if they do not.
     */
    public PointF findIntercept(LineSegment other) {
        // Special case for vertical lines
        if(Float.isInfinite(m) && Float.isInfinite(other.m)) {
            if(start.x == other.start.x)
                return new PointF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
            return null;
        }
        // special case for horizontal lines
        if(m == 0 && other.m == 0) {
            if(start.y == other.start.y)
                return new PointF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
            return null;
        }
        
        float x, y;
        
        if(Float.isInfinite(other.m)) {
            x = other.start.x;
        } else if(Float.isInfinite(m)) {
            return other.findIntercept(this);
        } else {
            // first find intercept. 
            // solve for X
            // m1x + b1 = m2x + b2
            // b1 - b2 = (m2 - m1)x
            
            x = (b - other.b) / (other.m - m);
        }
        
        // Now solve for y
        y = m*x + b;
        
        // now see if this point is on both line segments.
        if (isPointInSegment(x, y) && other.isPointInSegment(x, y)) {
            return new PointF(x,y);
        }
        
        return null;
    }
    
    /**
     * Returns true if point is contained in this line segment.
     * @param point
     * @return true if point is contained in this line segment
     */
    public boolean containsPoint(PointF point) {
        if(!VectorUtils.areEqual(point.x * m + b, point.y))
            return false;
        // we know the point is on the line; now, if it is in the square denoted by the start and end point, then it is in the segment
        return point.x < Math.max(start.x, end.x) && point.x > Math.min(start.x, end.x) &&
                point.y < Math.max(start.y, end.y) && point.y > Math.min(start.y, end.y) 
                    || ((VectorUtils.areEqual(point.x, start.x) || VectorUtils.areEqual(point.x, end.x)) &&
                        (VectorUtils.areEqual(point.y, start.y) || VectorUtils.areEqual(point.y, end.y)));
    }
    
    /**
     * Calculates a unit vector parallel to this line segment, with the same direction.
     * @return unit vector parallel to this line segment
     */
    public float[] findUnitVector()
    {
        float[] ret = new float[2];
        ret[0] = end.x - start.x;
        ret[1] = end.y - start.y;
        VectorUtils.convertToUnitVector(ret, ret);
        
        return ret;
    }
    
    /**
     * Returns whether the other line segment is the same as this one, modulo direction. 
     * It would be inappropriate to override <code>.equals()</code> because we ignore direction.
     * @param other the other line segment.
     * @return true iff this and other are equal, modulo direction
     */
    public boolean theSame(LineSegment other)
    {
        if(!(other instanceof LineSegment))
            return false;
        
        if(!(
                VectorUtils.areEqual(start, other.start) && VectorUtils.areEqual(end, other.end) ||
                VectorUtils.areEqual(start, other.end) && VectorUtils.areEqual(end, other.start)
            )) {
            return false;
        }
        
        if(!VectorUtils.areEqual(m, other.m) || !VectorUtils.areEqual(b, other.b)) {
            return false;
        }
            
        return true;
    }
    
    /**
     * Returns true iff the line segments overlap; weaker than <code>theSame</code>.
     * @param other
     * @return true iff this and other overlap
     */
    public boolean isOverlapping(LineSegment other){
        if(!VectorUtils.areEqual(m, other.m) && !VectorUtils.areEqual(b, other.b))
            return false;
        
        // same line, now check for overlap---
        // the start point of one should be inside the other
        return containsPoint(other.start) || other.containsPoint(start);
    }
    
    /**
     * Calculates the length of this line segment.
     * @return the length of this line segment, in meters.
     */
    public float length()
    {
        return VectorUtils.distance(start, end);
    }
    
}
