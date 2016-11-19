package org.usfirst.frc.team687.robot.subsystems;

import org.usfirst.frc.team687.robot.Constants;
import org.usfirst.frc.team687.robot.ToggleBoolean;
import org.usfirst.frc.team687.robot.subsystems.controllers.MotionProfileGenerator;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Elevator subsystem
 * 
 * @author tedfoodlin
 *
 */
public class Elevator extends Subsystem{

    private static Elevator elevator_instance = new Elevator();
    
	/**
	 * @return Drive subsystem instance
	 */
    public static synchronized Elevator getInstance() {
    	if (elevator_instance == null) {
    		elevator_instance = new Elevator();
    	}
        return elevator_instance;
    }
    
    private Joystick m_articJoy;
    private Encoder m_encoder;
    private CANTalon m_elevator;
	private ToggleBoolean m_toggleButton;
	private boolean m_gettingValue;
	
	private MotionProfileGenerator m_motionProfileGenerator;
	private double error;
	private double previousError;
	
	private double currentVelocity;
	private double previousVelocity;
	private double deltaVelocity;
	private double previousAccel;
	private double previousDecel;
	
	private double maxVelocity = 0;
	private double maxAccel = 0;
	private double maxDecel = 0;
    
    private Elevator() {
    	m_articJoy = new Joystick(Constants.JoystickPort);
    	m_encoder = new Encoder(Constants.EncoderPort1, Constants.EncoderPort2);
    	m_elevator = new CANTalon(Constants.ElevatorPort);
    	m_elevator.changeControlMode(TalonControlMode.PercentVbus);
    	
    	m_toggleButton.set(false);
    	m_gettingValue = false;
    	
    	currentVelocity = 0;
    	previousVelocity = 0;
    	deltaVelocity = 0;
    	previousAccel = 0;
    	previousDecel = 0;
    }
    
    private void toggleButtons() {
		if (m_articJoy.getRawButton(Constants.button)){
			m_toggleButton.set(!m_gettingValue);
		}
		m_gettingValue = m_toggleButton.get();
    }
    
    /**
     * Limits the given input to the given magnitude.
     */
    private double limit(double v, double limit) {
        return (Math.abs(v) < limit) ? v : limit * (v < 0 ? -1 : 1);
    }
    
    @Override 
    public void update() {
//    	toggleButtons();
//    	double desiredPosition;
//    	double m_startingTime = 0;
//    	if (m_gettingValue) {
//    		desiredPosition = SmartDashboard.getNumber("Desired Position");
//    		m_startingTime = Timer.getFPGATimestamp();
//    		m_motionProfileGenerator = new MotionProfileGenerator(desiredPosition - m_encoder.getRaw());
//    		m_motionProfileGenerator.generateProfile();
//    	} else {
//    		if (error != 0) {
//    			previousError = error;
//    		} else {
//    			previousError = 0;
//    		}
//    		double currentTime = Timer.getFPGATimestamp() - m_startingTime;
//    		double goalVelocity = m_motionProfileGenerator.readVelocity(currentTime);
//    		double goalAcceleration = m_motionProfileGenerator.readAcceleration(currentTime);
//    		double currentVelocity = m_encoder.getRate() / 100;
//    		error = goalVelocity - currentVelocity;
//    		double pow = Constants.kP * error 
//    				+ Constants.kD * ((error - previousError) / currentTime - goalVelocity) 
//    				+ Constants.kV * goalVelocity + Constants.kA * goalAcceleration;
//    		m_elevator.set(pow);
//    	}
//		
//		TODO: MEASURE THE MAX VELOCITY AND MAX ACCELERATION OF ELEVATOR
    	m_elevator.set(limit(m_articJoy.getY(), 1.0));
    }
    
    @Override
    public void reportState() {
    	SmartDashboard.putNumber("Current Position", m_encoder.getRaw());
    	
    	// for measuring max velocity and acceleration in ticks per second
    	previousVelocity = currentVelocity;
    	previousAccel = deltaVelocity;
    	previousDecel = deltaVelocity;
    	currentVelocity = m_encoder.getRate();
		deltaVelocity = currentVelocity - previousVelocity;
		
    	if (currentVelocity > previousVelocity) {
    		if (deltaVelocity > previousAccel) {
    			maxAccel = deltaVelocity;
    		}
    		maxVelocity = currentVelocity;
    	} else if (currentVelocity < previousVelocity) {
    		if (deltaVelocity < previousDecel) {
    			maxDecel = deltaVelocity;
    		}
    	}
    	SmartDashboard.putNumber("Current Velocity", currentVelocity);
    	SmartDashboard.putNumber("Current Acceleration", deltaVelocity);
    	
    	SmartDashboard.putNumber("Max Velocity", maxVelocity);
    	SmartDashboard.putNumber("Max Acceleration", maxAccel);
    	SmartDashboard.putNumber("Max Deceleration", maxDecel);
    }
    
    @Override 
    public void stop() {
    	m_elevator.set(0);
    	zeroSensors();
    }
    
    @Override
    public void zeroSensors() {
    	
    }
}
