package ca.uwaterloo.lab4_206_13;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.widget.TextView;
import android.widget.Toast;
import mapper.InterceptPoint;
import mapper.LabeledPoint;
import mapper.MapView;
import mapper.NavigationalMap;
import mapper.PositionListener;
import mapper.VectorUtils;

public class PositionHandler implements PositionListener{
	
	private List<InterceptPoint> intercept;
	private static List<PointF> designedPath = new ArrayList<PointF>();
	private static NavigationalMap nm;
	public static final float STEPLENGTH = 0.6f;
	private final int NORTH = 0;
	private final int SOUTH = 1;
	private final int EAST = 2;
	private final int WEST = 3;
	private final float TOLERANCE = 0.5f;
	private static PointF userPoint;
	private static PointF orig;
	private static PointF dest;
	private static Instruction instantInstruction;
	private static TextView in;
	private static TextView orig_dest;
	private static Toast ts;
	private static boolean origIsAvai = false;
	private static boolean destIsAvai = false;
		
	@Override
	public void originChanged(MapView source, PointF loc) {
		orig = loc;
		userPoint = loc;
		dest = source.getDestinationPoint();
		origIsAvai = true;
		DisplacementCounter.setEast(userPoint.x);
		DisplacementCounter.setSouth(userPoint.y);
		
		nm = MainActivity.getNavigationalMap();
		
		source.setUserPoint(loc);	
		
		if(destIsAvai){
			preSetUpPath(source);
		}	
		
	}

	@Override
	public void destinationChanged(MapView source, PointF dest) {
		userPoint = source.getOriginPoint();
		PositionHandler.dest = dest;
		destIsAvai = true;
		
		nm = MainActivity.getNavigationalMap();
		
		preSetUpPath(source);
	}
	
	@Override
	public void userPointChanged(float east, float south){
				
		if(origIsAvai && destIsAvai){
			userPoint = new PointF(east, south);
			MapView mv = MainActivity.getMapView();
			mv.setUserPoint(east, south);
			
			preSetUpPath(mv);
		}
	}
	
	private void preSetUpPath(MapView source){
		orig_dest.setText(String.format("Origin: (%,3.1f, %,3.1f) \nDestination: (%,3.1f, %,3.1f)", userPoint.x, userPoint.y, dest.x, dest.y));
		
		//clear the old path to avoid invalid new line between the same reference points
		source.clearUserPath();
		source.removeAllLabeledPoints();
		designedPath.clear();
				
		if (!hasDrawnStraightWay(source)){
			designIndirectPath(source);
		}
		
	}
	
	private boolean hasDrawnStraightWay(MapView source){
		intercept = nm.calculateIntersections(userPoint, dest);
		if(intercept.isEmpty()){
			designedPath.add(userPoint);
			designedPath.add(dest);
			source.setUserPath(designedPath);
			return true;
		} 
		return false;
	}
	
	private void designIndirectPath(MapView source){
				
		designedPath.add(userPoint);
		
		boolean finalIsFound = false;
		
		do{
			PointF inter = nm.calculateIntersections(designedPath.get(designedPath.size() - 1), dest).get(0).getPoint();
			designedPath.add(wallBuffer(designedPath.get(designedPath.size() - 1), inter));
			PointF validPoint = designedPath.get(designedPath.size() - 1);
			
			for (int i = 0; i < 4; i++){
				LabeledPoint testingPoint= decisionMaking(i, designedPath.get(designedPath.size() - 1));
							
				if (testingPoint.getLabel().equals("finalValid")){
					designedPath.add(testingPoint.getPoint());
					finalIsFound = true;
					break;
				} 
				
				if (testingPoint.getLabel().equals("valid")){
					if (interceptToDest(testingPoint.getPoint()) < interceptToDest(validPoint)){
						validPoint = testingPoint.getPoint();
					}
				}
			}
			
			if(!finalIsFound){			 
				designedPath.add(validPoint);	
			}
		} while (!finalIsFound);
			
		designedPath.add(dest);
		smoothPath();
		
		for(int i = 0; i + 1 < designedPath.size() - 1; i++ ){
			if(!nm.calculateIntersections(designedPath.get(i), designedPath.get(i + 1)).isEmpty()){
				return;
			}
		}
			
		for(PointF turningPoint: designedPath){
			source.addLabeledPoint(turningPoint, "");
		}
		
		source.setUserPath(designedPath);
		generateInstructions(NonLinearAccelerometerListener.getOrientation());
		
	}
	
	private LabeledPoint decisionMaking(int movingDir, PointF firstIntercept){
		
		PointF movingPoint;
		String label = "";
		int steps = 0;
		boolean crossingWall = false;
		boolean changingIntercept = false;
		boolean freeOfWall = false;

		do{
			steps++;
			if (movingDir == NORTH){
				movingPoint = new PointF(firstIntercept.x, firstIntercept.y - steps * STEPLENGTH);
			} else if (movingDir == SOUTH){
				movingPoint = new PointF(firstIntercept.x, firstIntercept.y + steps * STEPLENGTH);
			} else if (movingDir == EAST){
				movingPoint = new PointF(firstIntercept.x + steps * STEPLENGTH, firstIntercept.y);
			} else{
				//movingDir = WEST
				movingPoint = new PointF(firstIntercept.x - steps * STEPLENGTH, firstIntercept.y);
			}
			
			crossingWall = !nm.calculateIntersections(firstIntercept, movingPoint).isEmpty();
			freeOfWall = nm.calculateIntersections(movingPoint, dest).isEmpty();
			
			if (freeOfWall || crossingWall){
				break;
			}
			
			changingIntercept = (nm.calculateIntersections(movingPoint, dest).get(0).getPoint().x - firstIntercept.x) > TOLERANCE;
								
		} while(!crossingWall && !freeOfWall && !changingIntercept);
		
		if (crossingWall){
			label = "invalid";
		} 
		if (freeOfWall){
			label = "finalValid";
		}
		if (changingIntercept){
			label = "valid";
		}
		
		return new LabeledPoint(movingPoint, label);
	}
	
	private PointF wallBuffer(PointF start, PointF intercept){
		
		float deltaX = intercept.x - start.x;
		float deltaY = intercept.y - start.y;
		
		if (deltaX > 0){
			deltaX -= STEPLENGTH;
		} else{
			deltaX += STEPLENGTH; 
		}
		
		if (deltaY > 0){
			deltaY -= STEPLENGTH;
		} else{
			deltaY += STEPLENGTH;
		}
			
		PointF buffer = new PointF(start.x + deltaX, start.y + deltaY);
		return buffer;
	}
	
	private float interceptToDest(PointF p1){
		PointF tmpIntercept = nm.calculateIntersections(p1, dest).get(0).getPoint();
		float distance = (float)Math.abs(tmpIntercept.x - dest.x);
		return distance;
	}
	
	private void smoothPath(){
		for(int i = 0; i + 2 < designedPath.size(); i++){
			boolean removedAll;
			do{
				removedAll = true;
				intercept = nm.calculateIntersections(designedPath.get(i), designedPath.get(i + 2));
				if (intercept.isEmpty()){
					designedPath.remove(designedPath.get(i + 1));
					removedAll = false;
				}
			} while (!removedAll && i + 2 < designedPath.size());
		}
	}
	
	public static void generateInstructions(float currentHeading){
		if (!designedPath.isEmpty()){
			instantInstruction = new Instruction(designedPath.get(0), designedPath.get(1), currentHeading);
			in.setText(instantInstruction.toString());
		} else{
			in.setText("");
		}
	}
	
	public static void setDisplay(TextView in, TextView od, Toast ts){
		PositionHandler.in = in;
		orig_dest = od;
		PositionHandler.ts = ts;
	}
	
	//check to see if the user point crosses the wall or has reached destination
	public static boolean checkUserPoint(float pX, float pY, float cX, float cY){
		PointF previous = new PointF(pX, pY);
		PointF current = new PointF(cX, cY);
		
		if(!origIsAvai || !destIsAvai){
			return true;
		}
		
		if(nm.calculateIntersections(previous, current).isEmpty()){
			if(VectorUtils.distance(current, dest) < STEPLENGTH){
				ts.show();
			}
			return true;
		}
		
		return false;
	}
	
}