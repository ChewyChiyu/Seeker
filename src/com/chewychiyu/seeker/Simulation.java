package com.chewychiyu.seeker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.chewychiyu.random.Random;

/*
 * Simulation will use a genetic algorithm to evolve and
 * mutate "walkers" which aim for a set target. Will use JPanel and
 * Runnable for cycling and redraw. 2D arrays are used to position and increment the movement
 * of these "walkers". Using Random.java as well for implementation of randomness.
 */
@SuppressWarnings("serial")
public class Simulation extends JPanel{
	
	/* global vars */
	
	/* these are dimensions of the Jpanel (UI)*/
	Dimension d_;
	
	/* thread for engine , runnable for cycle, sleep time for delay*/
	Thread engine_;
	Runnable cycle_;
	final int SLEEP_TIME_ = 10;
	boolean run_cycle_;
	
	/* the terrain for the simulation will be represented by a 2d array ( int ) */
	int[][] grid_;
	
	/* our max population , and fitness scale */
	final int MAX_POPULATION_ = 200;
	final double FITNESS_SCALE_ = 4;
	final double MUTATION_RATE_ = 0.3;
	
	/* initial positon */
	int x_initial_, y_initial_;
	
	/* target position */
	final int TARGET_C_, TARGET_R_;
	
	/* our population of seekers */
	Seeker[] seekers_;
	
	/* current generation */
	float gen_;
	
	/* constructor passes through dimension of panel and grid_*/
	public Simulation(Dimension _d, int _rows, int _cols){
		d_ = _d;
		gen_ = 0;
		grid_ = new int[_rows][_cols];
		x_initial_ = _cols/2;
		y_initial_ = (int)(_rows*.9);
		TARGET_C_ = (int)(_cols*.2);
		TARGET_R_ = (int)(_rows*.1);
		seekers_ = new Seeker[MAX_POPULATION_];
		cycle_ = () -> _cycle();
		run_cycle_ = true;
		engine_ = new Thread(cycle_);
		_panel(_d);
		_spawn_seekers(seekers_);
		_zero_grid(grid_);
	}
	
	/* method for spawning seekers, randomly */
	public void _spawn_seekers(Seeker[] _seekers){
		for(int _i = 0; _i < _seekers.length; _i++){
			_seekers[_i] = new Seeker(y_initial_,x_initial_);
		}
	}
	
	/* fill the grid_ initial, zeroing it out */
	public void _zero_grid(int[][] _array){
		for(int _r = 0; _r < _array.length; _r++){
			for(int _c = 0; _c < _array[0].length; _c++){
				_array[_r][_c] = 0;
			}
		}
		_array[TARGET_R_][TARGET_C_] = 2;
	}
	
	/* this is where the genetic algorthim will take place,
	 * also moving and managing the walkers
	 */
	public void _cycle(){
		while(run_cycle_){
			_move_seekers(seekers_);
			_check_for_next(seekers_);
			repaint();
			try{ Thread.sleep(SLEEP_TIME_); } catch(Exception e) {}
		}
	}
	
	/* method for checking if the generation is over 
	 * continuing with getting highest fitness if yes. 
	 */
	public void _check_for_next(Seeker[] _seekers){
		double[] _weights = new double[_seekers.length];
		int[] _indexs = new int[_seekers.length];
		for(int _i = 0; _i < _seekers.length; _i++){
			if(_seekers[_i].current_index_!=-1){ return; }
			_indexs[_i] = _i;
			_weights[_i] = _seekers[_i].fitness_ / _get_total_fitness(_seekers);
		}
		Seeker _crop_a = _seekers[Random._pick_weighted(_indexs, _weights)];
		Seeker _crop_b = _seekers[Random._pick_weighted(_indexs, _weights)];
		_crop_a._verbose();
		_crop_b._verbose();
		_repopulate(_crop_a,_crop_b);
		_verbose();
	}
	
	/* repopulate the seeker array with a choosen seeker, has chance if mutation */
	public void _repopulate(Seeker _a, Seeker _b){
		for(int _i = 0; _i < seekers_.length; _i++){
			seekers_[_i] = _merge(_a,_b);
			seekers_[_i]._mutate(MUTATION_RATE_);
		}
	}
	
	/* merging two sets of data at random, return a new gen object */
	public Seeker _merge(Seeker _a, Seeker _b){
		int _merge_index = Random._integer_inclusive(0, _a.movement_list_.size()-1);
		ArrayList<Integer> _merge_move = new ArrayList<Integer>();
		for(int _i = 0; _i < _a.movement_list_.size(); _i++){
			_merge_move.add( (_merge_index < _i) ? _a.movement_list_.get(_i) : _b.movement_list_.get(_i) );
		}
		return new Seeker(_merge_move, y_initial_,x_initial_ );
	}
	
	/* get total fitness of generation */
	public double _get_total_fitness(Seeker[] _seekers){
		double _total_fitness = 0;
		for(int _i = 0; _i < _seekers.length; _i++){
			_total_fitness+=_seekers[_i].fitness_;
		}
		return _total_fitness;
	}
	
	/* move each element of seeker in seekers
	 * update the grid_ as well, remove seeker if movement is not possible
	 */
	public void _move_seekers(Seeker[] _seekers){
		for(int _i = 0; _i < _seekers.length; _i++){
			Seeker _s = _seekers[_i];
			if(_s.current_index_ == -1) { continue; }
			grid_[_s.r_][_s.c_] = 0;
			if(_s._move_in(grid_)){
				if(_s.r_ == TARGET_R_ && _s.c_ == TARGET_C_){
					 _s._calculate_fitness(TARGET_R_,TARGET_C_,FITNESS_SCALE_);
				}else{
				grid_[_s.r_][_s.c_] = 1;
				}
			}else{
				 _s._calculate_fitness(TARGET_R_,TARGET_C_,FITNESS_SCALE_);
			}
		}
	}
	
	/* get the element with the highest fitness */
	public Seeker _highest_fitness(Seeker[] _seekers){
		Seeker _best_seeker = null;
		for(int _i = 0; _i < _seekers.length; _i++){
			if(_best_seeker == null || _seekers[_i].fitness_ > _best_seeker.fitness_){
				_best_seeker = _seekers[_i];
			}
		}
		return _best_seeker;
	}
	
	/* Construction of the UI */
	public void _panel(Dimension _d){
		JFrame _frame = new JFrame("walker algorthim");
		_frame.add(this);
		_frame.setPreferredSize(_d);
		_frame.pack();
		_frame.setVisible(true);
		_frame.setResizable(false);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/* redraw method, will be used to draw the "walkers on the 2d array" */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		_draw_grid(g);
	}
	
	/*drawing grid */
	public void _draw_grid(Graphics g){
		int _x_buffer = 0;
		int _y_buffer = 0;
		final int _SPACER = d_.width / grid_.length;
		for(int _r = 0; _r < grid_.length; _r++){
			for(int _c = 0; _c < grid_[0].length; _c++){
				g.setColor(Color.BLACK);
				g.drawRect(_x_buffer, _y_buffer, _SPACER, _SPACER);
				if(grid_[_r][_c]!=0){
					g.setColor((grid_[_r][_c]==2)?Color.RED:Color.BLACK);
					g.fillRect(_x_buffer, _y_buffer, _SPACER, _SPACER);
				}
				_x_buffer += _SPACER;
			}
			_x_buffer = 0;
			_y_buffer += _SPACER;
		}
	}
	
	/* basic verbose, printing current status of simulation */
	public void _verbose(){
		for(int _i = 0; _i < seekers_.length; _i++){
			//seekers_[_i]._verbose();
		}
		System.out.print("gen: " + gen_++ + " ");
		_highest_fitness(seekers_)._verbose();
		System.out.println();
	}
	
}
