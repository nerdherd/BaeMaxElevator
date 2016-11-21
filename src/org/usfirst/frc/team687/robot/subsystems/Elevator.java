package org.usfirst.frc.team687.robot.subsystems;

import org.usfirst.frc.team687.robot.Constants;
import org.usfirst.frc.team687.robot.NerdyMath;
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
	 * @return Elevator subsystem instance
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
	
	private MotionProfileGenerator m_motionProfileGenerator;
	private double m_error;
	private double m_lastError;
	
	private double m_desiredPosition;
	private double m_startingTime;
	
	private double currentVelocity;
	private double previousVelocity;
	private double previousAccel;
	private double previousDecel;
	private double acceleration;
	
	private double maxVelocity;
	private double maxAccel;
	private double maxDecel;
    
    private Elevator() {
    	m_articJoy = new Joystick(Constants.JoystickPort);
    	m_encoder = new Encoder(Constants.EncoderPort1, Constants.EncoderPort2);
    	m_elevator = new CANTalon(Constants.ElevatorPort);
    	m_elevator.changeControlMode(TalonControlMode.PercentVbus);
    	
    	m_desiredPosition = Constants.kSecondTapeMarkerPosition;
    	
    	m_motionProfileGenerator = MotionProfileGenerator.getInstance();
		m_motionProfileGenerator.generateProfile(m_desiredPosition - getCurrentPosition());
    	
		m_startingTime = 0;
		
    	currentVelocity = 0;
    	previousVelocity = 0;
    	previousAccel = 0;
    	previousDecel = 0;
    	acceleration = 0;
    	
    	maxVelocity = 0;
    	maxAccel = 0;
    	maxDecel = 0;
    }
    
    /**
     * @return raw unscaled encoder value
     */
    private double getCurrentPosition() {
    	return m_encoder.getRaw();
    }
    
    /**
     * @return velocity in ticks / 10 ms (clock speed)
     */
    private double getCurrentVelocity() {
    	return NerdyMath.scaleVelocity(m_encoder.getRate());
    }
    
    /**
     * Record the max acceleration, deceleration, and velocity during a single motion
     */
    private void recordSystemConstants() {
    	// update each iteration
    	previousVelocity = currentVelocity;
    	previousAccel = acceleration;
    	previousDecel = acceleration;
    	currentVelocity = getCurrentVelocity();
		acceleration = (currentVelocity - previousVelocity) / Constants.kLoopFrequency;
		
		// record peak values for acceleration, velocity, and deceleration
    	if (currentVelocity > previousVelocity) {
    		if (acceleration > previousAccel) {
    			maxAccel = acceleration;
    		}
    		maxVelocity = currentVelocity;
    	} else if (currentVelocity < previousVelocity) {
    		if (acceleration < previousDecel) {
    			maxDecel = acceleration;
    		}
    	}
    	
    	SmartDashboard.putNumber("Current Velocity", currentVelocity);
    	SmartDashboard.putNumber("Current Acceleration", acceleration);
    	
    	SmartDashboard.putNumber("Max Velocity", maxVelocity);
    	SmartDashboard.putNumber("Max Acceleration", maxAccel);
    	SmartDashboard.putNumber("Max Deceleration", maxDecel);
    	
    	SmartDashboard.putNumber("Calculated kV", 1.0 / maxVelocity);
    }
    
    
    @Override 
    public void update() {
    	if (m_articJoy.getRawButton(Constants.updatePositionButton)) {
    		m_desiredPosition = SmartDashboard.getNumber("Desired Position");
    		m_startingTime = Timer.getFPGATimestamp();
    		m_motionProfileGenerator.generateProfile(m_desiredPosition - getCurrentPosition());
    	} else {
    		if (m_error != 0) {
    			m_lastError = m_error;
    		} else {
    			m_lastError = 0;
    		}
    		double currentTime = Timer.getFPGATimestamp() - m_startingTime;
    		double goalVelocity = m_motionProfileGenerator.readVelocity(currentTime);
    		double goalAcceleration = m_motionProfileGenerator.readAcceleration(currentTime);
    		double setpointPos = m_motionProfileGenerator.readDistance(currentTime);
    		double actualPos = getCurrentPosition();
    		m_error = setpointPos - actualPos;
    		double pow = Constants.kP * m_error 
    				+ Constants.kD * ((m_error - m_lastError) / currentTime - goalVelocity)
    				+ Constants.kV * goalVelocity 
    				+ Constants.kA * goalAcceleration;

    		m_elevator.set(pow);
    	}
		
//		TODO: MEASURE THE MAX VELOCITY AND MAX ACCELERATION OF ELEVATOR
    	if (getCurrentPosition() < 5000) {
    		m_elevator.set(1.0);
    	}
    }
    
    @Override
    public void reportState() {
    	SmartDashboard.putNumber("Current Position", getCurrentPosition());
    	
    	recordSystemConstants();
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
