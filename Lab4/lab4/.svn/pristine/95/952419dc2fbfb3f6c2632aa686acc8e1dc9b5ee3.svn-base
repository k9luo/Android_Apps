package ca.uwaterloo.lab4_206_13;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MagneticEventListener implements SensorEventListener {
	
	private static float[] record = new float[3];
	
	public MagneticEventListener(){
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		record[0] = event.values[0];
		record[1] = event.values[1];
		record[2] = event.values[2];
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	public static float[] getMagneticRecord(){
		return record;
	}

}
