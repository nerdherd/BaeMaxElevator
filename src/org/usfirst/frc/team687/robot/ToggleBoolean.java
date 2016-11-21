package org.usfirst.frc.team687.robot;

import edu.wpi.first.wpilibj.Timer;

/** 
 * Toggle booleans
 * 
 * @author goat
 *
 */

public class ToggleBoolean {

	private boolean toggle = false;
	private boolean waited = true;
	
	Timer timer = new Timer();
	
	public ToggleBoolean(){ /* totes ma goats */ }
	
	public void set(boolean state){
		// max time for holding down button is 1.0 seconds
		if(timer.get() > 1.0){
			waited = true;
			timer.stop();
		}
		
		if(state && waited){
			toggle = !toggle;
			waited = false;
			timer.start();
		}
	}
	
	public boolean get(){
		return toggle;
	}
}
