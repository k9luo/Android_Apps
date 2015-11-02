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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.graphics.PointF;

/**
 * Loads a map from SVG files and returns a corresponding NavigationalMap object.
 * @author Kirill Morozov
 *
 */
public class MapLoader 
{   
    // prevent construction; static methods only.
    private MapLoader() {}  

    private final static float DEFAULT_SCALE = 0.05f;
    private static DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder docBuilder = null;
    
    /**
     * Create a Pedometer map out of the provided SVG file.
     * @param dir pass the return from the method getExternalFilesDir(null) to this parameter
     * @param filename The filename of the map to load
     * @return a PedometerMap representing the map file that was loaded
     */
    public static NavigationalMap loadMap(File dir, final String filename)
    {
        NavigationalMap pedMap = new NavigationalMap();
        
        if (dir == null)
            throw new NullPointerException("getExternalFilesDir() returned null: did you add WRITE_PERMISSION?");
        
        if(docBuilder == null) {
            try {
                docBuilder = docBuildFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
                
        File[] maps = dir.listFiles(new FilenameFilter(){
            public boolean accept(File dir, String name) {
                return name.equals(filename);
            }
        });
        if (maps.length == 0)
            throw new RuntimeException("no maps in map directory");
        File map = maps[0];
        
        Document doc = null;
        
        try {
            doc = docBuilder.parse(map);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Process metadata
        Element svg = (Element) doc.getElementsByTagName("svg").item(0);

        PointF fileMaxCoord = new PointF(Float.parseFloat(svg.getAttribute("width")),
                                         Float.parseFloat(svg.getAttribute("height")));
    
        float scaleX = 0.0f;
        float scaleY = 0.0f;
        try {
            scaleX = Float.parseFloat(svg.getAttribute("xScale"));
            scaleY = Float.parseFloat(svg.getAttribute("yScale"));
        } catch (NumberFormatException e) {}
        
        PointF fileScale;
        if (scaleX == 0 || scaleY == 0){
            fileScale = new PointF(DEFAULT_SCALE, DEFAULT_SCALE);
        }else{
            fileScale = new PointF(scaleX, scaleY);
        }
        
        // process paths
        NodeList filePaths = doc.getElementsByTagName("path");
        
        for(int i = 0; i < filePaths.getLength(); i++) {
            pedMap.addPath(parseConvertPath(filePaths.item(i), fileMaxCoord, fileScale));
        }
        return pedMap;
    }
    
    
    private static PointF makePoint(String s1, String s2)
    {
        return new PointF(
                Float.parseFloat(s1),
                Float.parseFloat(s2)
                ) ;
    }
    
    private static PointF makePointRelative(PointF p, String s1, String s2)
    {
        return new PointF(
                p.x + Float.parseFloat(s1),
                p.y + Float.parseFloat(s2)
                ) ;
    }
    
    private static ArrayList<PointF> parseConvertPath(Node node, PointF fileMaxCoord, PointF fileScale)
    {
        Element elem = (Element) node;
        ArrayList<PointF> ret = new ArrayList<PointF>();
        
        String d = elem.getAttribute("d");
        
        String[] pathString = d.split("[ ,]");
        PointF refPoint = new PointF();
        char defaultCommand = 'l';
        
        try{
            for(int i = 0; i < pathString.length; i++){
            PointF newPoint;
            
            if("cCsSqQtTaAmMlLzZ-1234567890".indexOf(pathString[i].charAt(0)) == -1){
                throw new InvalidParameterException("A character that was to be interpreted as a command character " +
                        "is not known by the Map loader. Check your path Data. The unknown character was: <" + pathString[i].charAt(0) +">"  +
                        "in the path {" + d + "}");
            }
                
            // I don't handle bezier curves, so skip all the extra components of the bezier commands
            switch(pathString[i].charAt(0)){
            case 'c':
                i += 4;
                pathString[i] = "l";
                break;
            case 'C':
                pathString[i] = "L";
                i+= 4;
                break;
            case 's':
                i += 2;
                pathString[i] = "l";
                break;
            case 'S':
                i += 2;
                pathString[i] = "L";
                break;
            case 'q':
                i += 2;
                pathString[i] = "l";
                break;
            case 'Q':
                i += 2;
                pathString[i] = "L";
                break;
            case 't':
                pathString[i] = "l";
                break;
            case 'T':
                pathString[i] = "L";
                break;
            case 'a':
                i += 5;
                pathString[i] = "l";
                break;
            case 'A':
                i += 5;
                pathString[i] = "L";
                break;
            }
                        
            // read control character
            switch(pathString[i].charAt(0)){
            case 'M':
                newPoint = makePoint(pathString[i+1], pathString[i+2]);
                i += 2;
                defaultCommand = 'L';
                break;
            case 'm':
            case 'l':
                newPoint = makePointRelative(refPoint, pathString[i+1], pathString[i+2]);
                i += 2;
                defaultCommand = 'l';
                break;
            case 'L':
                newPoint = makePoint(pathString[i+1], pathString[i+2]);
                i += 2;
                defaultCommand = 'L';
                break;
            case 'z':
            case 'Z':
                newPoint = new PointF();
                newPoint.set(ret.get(0));
                break;
            //If there is no control character, we use the last one.
            default:
                switch(defaultCommand){
                case 'l':
                default:
                    newPoint = makePointRelative(refPoint, pathString[i], pathString[i+1]);
                    i += 1;
                    break;
                case 'L':
                    newPoint = makePoint(pathString[i], pathString[i+1]);
                    i += 1;
                    break;
                }
            }
            
            ret.add(newPoint);
            refPoint.set(newPoint);                                 
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("There were not enough elements to process all the commands. "+
                    "Either the path contains an unknown command or one of the commands has too few parameters. " +
                    "The path being processed was {" + d +"}");
        }catch(NumberFormatException e) {
            throw new NumberFormatException("The map loader encountered a problem parsing path data. This likely means that you have an " +
                    "unknown control character. Check your paths. " +
                    "The path being processed was {" + d +"}");
        }
        for(PointF p : ret)
            convertCoord(p, fileScale);
        
        return ret;
    }

    /**
     * Converts the coordinate to real-world space
     * @param coord
     */
    private static void convertCoord(PointF coord, PointF fileScale) {
        coord.set(coord.x * fileScale.x, coord.y * fileScale.y);
    }
}
