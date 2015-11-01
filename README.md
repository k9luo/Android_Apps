ECE 155 | Engineering Design with Embedded Systems | Winter 2015

All labs are Android applications and were tested on Nexus 5 and Nexus 7 devices. 

Lab 1 - Sensor Reader
Displays informaton from an android phone sensors. 
Sensors include:
  Light sensor: Displays light intensity
  Accelerometer: Displays X,Y,Z acceleration and displays it on a graph with respect to time
  Magnetic Field Sensor: Displays X,Y,Z field values
  Rotation Sensor: Displays X,Y,Z rotation values

Lab 2 - Step Counter
Detects and counts user steps (device held in both hands with screen facing upwards) 
Uses accelerometer data to detect a step in the user's motion. Step counter is then incremented and displayed on screen. Step counter can be reset by pressing the reset button.>

Lab 3 - Displacement Tracker
Measures user displacement in NS/EW coordinates 
Uses existing code from Lab 2 and adds the user's direction to track their displacement. North has to be calibrated by facing North and hitting the calibrate button.

Lab 4 - Map Guide
Gives directions to the user to move from one point of a room to another. 
Uses existing code from Lab 3 to track the user's displacement. A map in an SVG format must be added to the app's files directory. The user can set their initial location and direction by pressing and holding on the map.
