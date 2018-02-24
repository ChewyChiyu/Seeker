package com.chewychiyu.seeker;

import java.awt.Dimension;

/*
 * This is where the main() is stored, starts new JVM thread
 *  and launches the simulation program. 
 */

public class SimulationLauncher {
	
	/* rows and cols of grid_ in simulation */
	final int ROWS_ = 100;
	final int COLS_ = 100;
	
	/* New JVM thread */
	public static void main(String[] args){
		new SimulationLauncher();
	}
	
	/* default constructor, also launching engine_ in simulation */
	SimulationLauncher(){
		new Simulation(new Dimension(800,800), ROWS_, COLS_).engine_.start();
	}
	
}
