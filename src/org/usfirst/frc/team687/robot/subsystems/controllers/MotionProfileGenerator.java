package org.usfirst.frc.team687.robot.subsystems.controllers;

import java.util.ArrayList;

import org.usfirst.frc.team687.robot.Constants;

/**
 * Trapezoidal motion profile generator for smoother distance control
 * 
 * @author tedfoodlin
 *
 */
public class MotionProfileGenerator {
	
	private static double distance;
	private static double vmax = Constants.kMaxVelocity;
	private static double a_max = Constants.kMaxAccel;
	private static double clk = 1/100; //the period that the multilooper runs at
	
	/**
	 * Data arrays
	 */
	public static ArrayList<Double> time_data = new ArrayList<Double>();
	public static ArrayList<Double> velocity_data = new ArrayList<Double>();
	public static ArrayList<Double> distance_data = new ArrayList<Double>();
	public static ArrayList<Double> acceleration_data = new ArrayList<Double>();
	
	public MotionProfileGenerator(double dist) {
		distance = dist;
	}

	/**
	 * 3 stages - accelerate, cruising, decelerate
	 * Loop through time and add data for each time interval to arrays
	 * Don't account for jerk
	 */
	public void generateProfile() {
		double time;
		double x;
		double v = 0;
		
		boolean isTriangular = isTriangular();
		for (time = 0; time < (vmax/a_max); time += clk){
			x = (0.5 * a_max * Math.pow(time, (double)2));
			v = a_max * time;
			addData(time, v, x, a_max);
		}
		if (isTriangular == false){
			for (time = vmax/a_max; time < (distance/vmax); time += clk){
				x = (0.5 * (Math.pow(vmax, 2) / a_max)) + (vmax * (time - (vmax/a_max)));
				v = (vmax);
				addData(time, v, x, 0);
			}
		}
		double end_of_second_stage = (vmax/a_max) + (distance/vmax);
		for (time = distance/vmax; time <= end_of_second_stage; time += clk){
			x = (double)(distance - 0.5 * a_max * Math.pow((time-((vmax/a_max)+(distance/vmax))), 2));
			v = a_max * ((vmax/a_max)+(distance/vmax)-time);
			addData(time, v, x, -a_max);
		}
	}
	
	/**
	 * Adds data to array lists
	 * 
	 * @param time
	 * @param velocity
	 * @param distance
	 * @param acceleration
	 */
	private void addData(double time, double v, double x, double acceleration) {
		time_data.add(time);
		velocity_data.add(v);
		distance_data.add(x);
		acceleration_data.add(acceleration);
	}
	
	/**
	 * Check if the maximum velocity can actually be reached
	 * If the maximum velocity can't be reached, adjust it to the final velocity that it can reach
	 * Maximum velocity can't be reached = triangular motion profile
	 */
	private boolean isTriangular() {
		double mid = distance/2;
		double vFinal = Math.pow(2 * a_max * mid, 0.5);
		if (vFinal < vmax){
			vmax = vFinal;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param time
	 * @return goal velocity
	 */
	public double readVelocity(double time) {
		return velocity_data.get((int)(time/clk));
	}
	
	/**
	 * @param time
	 * @return goal distance
	 */
	public double readDistance(double time) {
		return distance_data.get((int)(time/clk));
	}
	
	/**
	 * @param time
	 * @return goal acceleration
	 */
	public double readAcceleration(double time) {
		return acceleration_data.get((int)(time/clk));
	}
}

