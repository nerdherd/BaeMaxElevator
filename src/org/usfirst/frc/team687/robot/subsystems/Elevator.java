package org.usfirst.frc.team687.robot.subsystems;

import org.usfirst.frc.team687.robot.Constants;
import org.usfirst.frc.team687.robot.NerdyMath;
import org.usfirst.frc.team687.robot.subsystems.controllers.MotionProfileGenerator;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

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
	
	private boolean m_finished;
    
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
    	
    	m_finished = false;
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
     * Set elevator motor power to a value (between -1.0 to 1.0 in PercentVBus TalonControlMode)
     * 
     * @param power
     */
    private void setElevatorPower(double pow) {
    	m_elevator.set(pow);
    }
    
    /**
     * Update the setpoint position and create a new motion profile for that setpoint
     */
    private void setNewPosition() {
    	m_motionProfileGenerator = null;
		m_desiredPosition = SmartDashboard.getNumber("Desired Position");
		m_startingTime = Timer.getFPGATimestamp();
		m_motionProfileGenerator.generateProfile(m_desiredPosition - getCurrentPosition());
    }
    
    /**
     * Calculate the input power for motor controller at a given timestamp
     * 
     * @return pow
     */
    private double calculatePow() {
		m_lastError = m_error;
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
		return NerdyMath.limit(pow, 0.5);
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
    	if (currentVelocity > previousVelocity && currentVelocity > maxVelocity) {
    		if (acceleration > previousAccel && acceleration > maxAccel) {
    			maxAccel = acceleration;
    		}
    		maxVelocity = currentVelocity;
    	} else if (currentVelocity < previousVelocity) {
    		if (acceleration < previousDecel && acceleration < maxDecel) {
    			maxDecel = acceleration;
    		}
    	}
    	
    	SmartDashboard.putNumber("Max Velocity", maxVelocity);
    	SmartDashboard.putNumber("Max Acceleration", maxAccel);
    	SmartDashboard.putNumber("Max Deceleration", maxDecel);
    	
    	SmartDashboard.putNumber("Calculated kV", 0.5 / maxVelocity);
    }
    
    
    @Override 
    public void update() {
//		TODO: TEST MOTION PROFILED CONTROL
//    	if (m_articJoy.getRawButton(Constants.updatePositionButton)) {
//    		setNewPosition();
//    	}
//    	else {
//    		setElevatorPower(calculatePow());
//    	}
//    	
//		TODO: MEASURE MAX ACCELERATION OF ELEVATOR
    	if (getCurrentPosition() <= Constants.kSecondTapeMarkerPosition && m_finished == false) {
    		setElevatorPower(-0.5);
    	} else if (getCurrentPosition() > Constants.kSecondTapeMarkerPosition) {
    		m_finished = true;
    		setElevatorPower(0);
    	}
    }
    
    @Override
    public void reportState() {
    	SmartDashboard.putNumber("Current Position", getCurrentPosition());
    	SmartDashboard.putNumber("Current Velocity", getCurrentVelocity());
    	SmartDashboard.putNumber("Current Acceleration", acceleration);
    	recordSystemConstants();
    }
    
    @Override 
    public void stop() {
    	m_elevator.set(0);
    	zeroSensors();
    }
    
    @Override
    public void zeroSensors() {
    	m_encoder.reset();
    }
    
}
