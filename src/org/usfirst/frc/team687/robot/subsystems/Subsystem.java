package org.usfirst.frc.team687.robot.subsystems;

import org.usfirst.frc.team687.lib.Loopable;

/**
 * Subsystem abstract class
 * 
 * @author tedfoodlin
 *
 */

public abstract class Subsystem implements Loopable{
	
	/**
	 * Report important data to Smart Dashboard
	 */
    public abstract void reportState();

    /**
     * Stop when disabled
     */
    public abstract void stop();

    /**
     * Reset sensors
     */
    public abstract void zeroSensors();
    
}
