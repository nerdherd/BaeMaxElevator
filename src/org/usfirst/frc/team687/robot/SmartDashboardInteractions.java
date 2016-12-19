package org.usfirst.frc.team687.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Interactions with SmartDashboard
 * 
 * @author tedfoodlin
 *
 */

public class SmartDashboardInteractions {
	
	/**
	 * Init with defaults
	 */
	public void init() {
    	SmartDashboard.putNumber("P: ", Constants.kP);
    	SmartDashboard.putNumber("I: ", Constants.kI);
    	SmartDashboard.putNumber("D: ", Constants.kD);
    	SmartDashboard.putNumber("kA: ", Constants.kA);
    	SmartDashboard.putNumber("kV: ", Constants.kV);
    	SmartDashboard.putNumber("Desired Position", Constants.kSecondTapeMarkerPosition);
	}
	
	/**
	 * Update editable values
	 */
	public void update() {
    	Constants.desiredPosition = SmartDashboard.getNumber("Desired Position");
	}

}
