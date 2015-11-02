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
 * Implement this interface to listen to user-generated positional events sent by the map view.
 * 
 * You will need to call MapView.addListener() to register as an event listener.
 * 
 * @author Kirill Morozov
 *
 */
public interface PositionListener 
{
	/**
	 * Called when the user sets their origin/current location through the MapView.
	 * @param source The MapView that caused the change.
	 * @param loc The new coordinates of the location in meters.
	 */
	public void originChanged(MapView source, PointF loc);
	/**
	 * Called when the user sets their destination through the MapView.
	 * @param source The MapView that caused the change.
	 * @param dest The new coordinates of the destination in meters.
	 */
	public void destinationChanged(MapView source, PointF dest);
}
