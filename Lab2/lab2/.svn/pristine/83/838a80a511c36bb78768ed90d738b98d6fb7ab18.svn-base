package ca.uwaterloo.lab2_206_13;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class AccelerometerEventListener implements SensorEventListener {

	TextView output;
	LineGraphView graph;
	TextView counter;
    
	float[] record = new float[1];
	float delta;
	//float max;
	
	AccelerationState state = AccelerationState.WAITING;
	
	public AccelerometerEventListener (TextView outputView, LineGraphView graph, TextView counter){
		output = outputView;
		this.graph = graph;
		this.counter = counter;
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			//get z-axis value
			delta = event.values[2] - record[0];
			record[0] += delta / 16;
			
			//if (max < record[0]){
			//	max = record[0];
			//}
			
			//output.setText(String.format("Linear acceleration on z axis:\n%,5.2f in m/s^2 \n max on z is %,5.2f", 
			//		record[0], max) );
			
			output.setText(String.format("Linear acceleration on z axis:\n%,5.2f in m/s^2 \n ", 
						record[0]));
			graph.addPoint(record);
			
			state = state.process(delta, record[0]);
			
			counter.setText("Steps: " + StepCounter.steps() + "\nstate: " + state.toString() + "\nDuration: " + StepCounter.getDuration() +"\nSteps2: " + StepCounter.getSteps2());
			
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

    
}
