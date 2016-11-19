package org.usfirst.frc.team687.robot;

import org.usfirst.frc.team687.lib.MultiLooper;
import org.usfirst.frc.team687.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * Same
 * 
 * @author tedfoodlin
 *
 */

public class Robot extends IterativeRobot {
	
	Elevator elevator = Elevator.getInstance();
    MultiLooper controllers = new MultiLooper("Controllers", Constants.kClk);
    
	SmartDashboardInteractions SDI = new SmartDashboardInteractions();
	
    public void robotInit() {
    	controllers.addLoopable(elevator);
    	elevator.stop();
    	SDI.init();
    }
    
    public void teleopInit() {
    	controllers.stop();
    }

    public void teleopPeriodic() {
    	SDI.update();
    	controllers.start();
    	elevator.reportState();
    }
    
    public void disabledPeriodic() {
    	controllers.stop();
    }
}
