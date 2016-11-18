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

    private static Elevator drive_instance = new Elevator();
    
	/**
	 * @return Drive subsystem instance
	 */
    public static Elevator getInstance() {
        return drive_instance;
    }
    
    private Joystick m_articJoy;
    private Encoder m_encoder;
    private CANTalon m_elevator;
	private ToggleBoolean m_toggleButton;
	private boolean m_gettingValue;
	
	private MotionProfileGenerator m_motionProfileGenerator;
	private double error;
	private double previousError;
    
    public Elevator() {
    	m_articJoy = new Joystick(Constants.JoystickPort);
    	m_encoder = new Encoder(Constants.EncoderPort1, Constants.EncoderPort2);
    	m_elevator = new CANTalon(Constants.ElevatorPort);
    	m_elevator.changeControlMode(TalonControlMode.PercentVbus);
    	
    	m_toggleButton.set(false);
    	m_gettingValue = false;
    }
    
    private void toggleButtons() {
		if (m_articJoy.getRawButton(Constants.button)){
			m_toggleButton.set(!m_gettingValue);
		}
		m_gettingValue = m_toggleButton.get();
    }
    
    @Override 
    public void update() {
    	toggleButtons();
    	double desiredPosition;
    	double m_startingTime = 0;
    	if (m_gettingValue) {
    		desiredPosition = SmartDashboard.getNumber("Desired Position");
    		m_startingTime = Timer.getFPGATimestamp();
    		m_motionProfileGenerator = new MotionProfileGenerator(desiredPosition - m_encoder.getRaw());
    		m_motionProfileGenerator.generateProfile();
    	} else {
    		if (error != 0) {
    			previousError = error;
    		} else {
    			previousError = 0;
    		}
    		double currentTime = Timer.getFPGATimestamp() - m_startingTime;
    		double goalVelocity = m_motionProfileGenerator.readVelocity(currentTime);
    		double goalAcceleration = m_motionProfileGenerator.readAcceleration(currentTime);
    		double currentVelocity = m_encoder.getRate() / 100;
    		error = goalVelocity - currentVelocity;
    		double pow = Constants.kP * error 
    				+ Constants.kD * ((error - previousError) / currentTime - goalVelocity) 
    				+ Constants.kV * goalVelocity + Constants.kA * goalAcceleration;
    		m_elevator.set(pow);
    	}
    }
    
    @Override
    public void reportState() {
    	SmartDashboard.putNumber("Current Position", m_encoder.getRaw());
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
