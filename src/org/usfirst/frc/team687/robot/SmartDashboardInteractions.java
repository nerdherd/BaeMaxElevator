package org.usfirst.frc.team687.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartDashboardInteractions {
	
	public void init() {
    	SmartDashboard.putNumber("P: ", Constants.kP);
    	SmartDashboard.putNumber("I: ", Constants.kI);
    	SmartDashboard.putNumber("D: ", Constants.kD);
    	SmartDashboard.putNumber("kA: ", Constants.kA);
    	SmartDashboard.putNumber("kV: ", Constants.kV);
    	SmartDashboard.putNumber("Desired Position", 5627.0);
	}
	
	public void update() {
    	Constants.kP = SmartDashboard.getNumber("P: ");
    	Constants.kI = SmartDashboard.getNumber("I: ");
    	Constants.kD = SmartDashboard.getNumber("D: ");
    	Constants.kA = SmartDashboard.getNumber("kA: ");
    	Constants.kV = SmartDashboard.getNumber("kV: ");
    	Constants.desiredPosition = SmartDashboard.getNumber("Desired Position");
	}

}
