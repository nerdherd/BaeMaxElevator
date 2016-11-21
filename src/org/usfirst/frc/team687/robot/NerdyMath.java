package org.usfirst.frc.team687.robot;

/**
 * Useful math methods
 * 
 * @author tedfoodlin
 *
 */
public class NerdyMath {

    /**
     * Limit the given input to the given magnitude
     */
    public static double limit(double pow, double limit) {
        return (Math.abs(pow) < limit) ? pow : limit * (pow < 0 ? -1 : 1);
    }
    
    /**
     * Scales the speed in distance per seconds to distance per 10 ms
     * 
     * @param speed (raw ticks per second)
     * @return scaled speed (raw ticks per 10 ms)
     */
	public static double scaleVelocity(double velocity) {
		return velocity * Constants.kLoopFrequency;
	}
	
	/**
	 * Scales the acceleration in distance per seconds^2 to distance per (10 ms)^2
	 * 
	 * @param accel
	 * @return scaled accel
	 */
	public static double scaleAccel(double accel) {
		return accel * Math.pow(Constants.kLoopFrequency, 2);
	}
}
