package org.usfirst.frc.team687.robot;

/**
 * Constants
 * 
 * @author tedfoodlin
 *
 */
public class Constants {
	
	public static double kMaxVelocity = convertVelocity(72.0);
	public static double kMaxAccel = convertAccel(120.0);
	public static double kP = 1.0;
	public static double kI = 1.5;
	public static double kD = 0.0;
	public static double kA = 6.0E-4;
	public static double kV = 0.02;
	public static double desiredPosition = 5627.0;
	
	public static int EncoderPort1 = 8;
	public static int EncoderPort2 = 9;
	
	public static int JoystickPort = 1;
	public static int ElevatorPort = 1;
	
	public static int button = 1;
	
	private static double convertVelocity(double inchesPerSecond) {
		return 2 * inchesPerSecond * 100;
	}
	
	private static double convertAccel(double inchesPerSecond2) {
		return 2 * inchesPerSecond2 * Math.pow(100, 2);
	}
}
