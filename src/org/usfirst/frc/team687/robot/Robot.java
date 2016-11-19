package org.usfirst.frc.team687.robot;

import org.usfirst.frc.team687.lib.MultiLooper;
import org.usfirst.frc.team687.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Same
 * 
 * @author tedfoodlin
 *
 */

public class Robot extends IterativeRobot {
	
	Elevator elevator = Elevator.getInstance();
    MultiLooper controllers = new MultiLooper("Controllers", 1/100.0);
	
    public void robotInit() {
    	controllers.addLoopable(elevator);
    	elevator.stop();
    	SmartDashboard.putNumber("P: ", Constants.kP);
    	SmartDashboard.putNumber("I: ", Constants.kI);
    	SmartDashboard.putNumber("D: ", Constants.kD);
    	SmartDashboard.putNumber("kA: ", Constants.kA);
    	SmartDashboard.putNumber("kV: ", Constants.kV);
    	SmartDashboard.putNumber("Desired Position", 5627.0);
    }
    
    public void teleopInit() {
    	controllers.stop();
    }

    public void teleopPeriodic() {
    	Constants.kP = SmartDashboard.getNumber("P: ");
    	Constants.kI = SmartDashboard.getNumber("I: ");
    	Constants.kD = SmartDashboard.getNumber("D: ");
    	Constants.kA = SmartDashboard.getNumber("kA: ");
    	Constants.kV = SmartDashboard.getNumber("kV: ");
    	Constants.desiredPosition = SmartDashboard.getNumber("Desired Position");
    	controllers.start();
    	elevator.reportState();
    }
    
    public void disabledPeriodic() {
    	controllers.stop();
    }
}
