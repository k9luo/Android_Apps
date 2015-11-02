package ca.uwaterloo.lab3_206_13;

public class DisplacementCounter {
	private static int steps;
	private static float north = 0f;	//y-axis
	private static float east = 0f;	//x-axis
	private static float baseline = 0f;
	private static boolean resetBaseline = true;
	private static float addUpBaseline = 0f;
	private static int numOfSamples = 0;
	
	public static int steps(){
		return steps;
	}
	
	public static void stepIncrement(){
		steps++;
	}
	
	public static void reset(){
		steps = 0;
		north = 0f;
		east = 0f;
		resetBaseline = true;
		baseline = 0f;
		addUpBaseline = 0f;
		numOfSamples = 0;
		
	}	
	
	public static void calculateOrientation(){
		float currentOrientation = NonLinearAccelerometerListener.getOrientation();
		
		if (resetBaseline == true){
			baseline = currentOrientation;
			resetBaseline = false;
		}
		
		addUpBaseline += currentOrientation - baseline;
		numOfSamples ++;
	}
	
	public static void resetBaseline(){
		resetBaseline = true;
		baseline = 0f;
		addUpBaseline = 0f;
		numOfSamples = 0;
	}
	
	public static void getFinalDisplacement(){
		double finalOrient = baseline + addUpBaseline / numOfSamples;
		//Calculate the displacement of each step based on the previous position
		//the corresponding changes with respect to the previous coordinates would be 
		//cos(angle) and sin(angle) for North and East respectively
		north += (float) Math.cos((double)(finalOrient / 180 * 3.14));
		east += (float) Math.sin((double)(finalOrient / 180 * 3.14));
	}
	
	public static float getNorth(){
		return north;
	}
	
	public static float getEast(){
		return east;
	}
}
