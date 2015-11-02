package ca.uwaterloo.lab3_206_13;

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
	TextView display;
	
	public NonLinearAccelerometerListener(TextView d){
		display = d;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		//record[0] = event.values[0];
		//record[1] = event.values[1];
		//record[2] = event.values[2]; 
				
		SensorManager.getRotationMatrix(R, I, event.values, MagneticEventListener.getMagneticRecord());
		SensorManager.getOrientation(R, orientation);
		
		orientation[0] = orientation[0] / 3.14f * 180f;
			
		if (orientation[0] < 0){
			orientation[0] += 360;
		}
		
		DisplacementCounter.calculateOrientation();
		
		display.setText(String.format("Orientation on z-axis: %, 5.1f "
				+ "\nDisplacement on E & N: (%,5.1f, %,5.1f) unit steps", orientation[0], 
				DisplacementCounter.getEast(), DisplacementCounter.getNorth()) );
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	public static float getOrientation(){
		return orientation[0];
	}

}
