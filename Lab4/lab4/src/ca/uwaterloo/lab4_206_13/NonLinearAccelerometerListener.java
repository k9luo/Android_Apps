package ca.uwaterloo.lab4_206_13;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class NonLinearAccelerometerListener implements SensorEventListener {

	//private static float[] record = new float[3];
	private float[] R = new float[9];
	private	float[] I = new float[9];
	private	static float[] orientation = new float[3];
	private static float relativeOrientation;
	TextView display;
	
	public NonLinearAccelerometerListener(TextView d){
		display = d;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		SensorManager.getRotationMatrix(R, I, event.values, MagneticEventListener.getMagneticRecord());
		SensorManager.getOrientation(R, orientation);
		
		//convert the orientation from radian to degree
		orientation[0] = orientation[0] / 3.14f * 180f;
			
		if (orientation[0] < 0){
			relativeOrientation = 360 + orientation[0];
		} else{
			relativeOrientation = orientation[0];
		}
		
		display.setText(String.format("Orientation on z-axis: %, 5.1f "
				+ "\nDisplacement on E & S: (%,5.1f, %,5.1f) unit steps", relativeOrientation, 
				DisplacementCounter.getEast(), DisplacementCounter.getSouth()) );
		
		PositionHandler.generateInstructions(relativeOrientation);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	public static float getOrientation(){
		return relativeOrientation;
	}
	
	

}