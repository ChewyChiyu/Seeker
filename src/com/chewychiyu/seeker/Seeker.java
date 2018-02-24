package com.chewychiyu.seeker;

import java.util.ArrayList;

import com.chewychiyu.random.Random;

/* this is what we will mutate for the purpose of a higher chance of fitness
 * data will be stored in a form of array list and fitness will be stored as
 * a double 
 */
public class Seeker {
	
	/* list for movement
	 * 0 = NORTH, 1 = SOUTH, 2 = EAST, 3 = WEST
	 */
	ArrayList<Integer> movement_list_;
	
	/* fitness of Seeker ( double )*/
	double fitness_;
	
	/* current position to grid */
	int r_, c_;
	int initial_r_, initial_c_;
	/*current index in the move map */
	public int current_index_;
	
	/* maximum move map length */
	final int MAX_MOVE_LENGTH_ = 200;
	
	/* constructor, generate random movement list */
	public Seeker(int _r, int _c){
		movement_list_ = new ArrayList<Integer>();
		fitness_ = 0;
		current_index_ = 0;
		r_ = _r;
		c_ = _c;
		initial_c_ = c_;
		initial_r_ = r_;
		_fill_rand_movement(movement_list_, MAX_MOVE_LENGTH_);
	}
	
	/* constructor passing in a preset movement array */
	public Seeker(ArrayList<Integer> _preset_move, int _r, int _c){
		movement_list_ = _preset_move;
		fitness_ = 0;
		current_index_ = 0;
		r_ = _r;
		c_ = _c;
		initial_c_ = c_;
		initial_r_ = r_;
	}
	
	/* generate random movement pattern and apply it to movement_list_,
	 * random movement size 0-100, random selection of 0-3
	 */
	public void _fill_rand_movement(ArrayList<Integer> _move_list, int _move_size){
		for(int _i = 0; _i < _move_size; _i++){
			movement_list_.add(Random._integer_inclusive(0, 3));
		}
	}
	
	/* mutating the array to simulate common evolution */
	public void _mutate(double _mutation_rate){
		if(Math.random() < _mutation_rate){
			movement_list_.set(Random._integer_inclusive(0, movement_list_.size()-1), Random._integer_inclusive(0, 3));
		}
	}
	
	/* changing the position of the seeker based on 
	 * 0 = NORTH, 1 = SOUTH, 2 = EAST, 3 = WEST
	 * returns true if movement if possible
	 * false if hit obstacle
	 */
	public boolean _move_in(int[][] _grid){
		if(current_index_++ < movement_list_.size()-1){
			switch(movement_list_.get(current_index_)){
			case 0: 
				if(r_ > 1){
				r_--;
				}else{
					return false;
				}
				break;
			case 1:
				if(r_ < _grid.length-2){
				r_++;
				}else{
					return false;
				}
				break;
			case 2:
				if(c_ < _grid[0].length-2){
				c_++;
				}else{
					return false;
				}
				break;
			case 3:
				if(c_ > 1){
				c_--;
				}else{
					return false;
				}
				break;
			}
		}else{
			return false;
		}
		return true;
	}
	
	/* die method, impacted wall or obstacle
	 *	denoted by the current index turning to -1 
	 *  calculate fitness on death (looking for min distance from target, and if same, shortest index time)
	 */
	public void _calculate_fitness(int _target_r, int _target_c, double _fitness_scale){
		int _delta_c = initial_c_ - Math.abs(c_ - _target_c);
		int _delta_r = initial_r_ - Math.abs(r_ - _target_r);
		fitness_ = Math.pow(_fitness_scale, (_delta_r + _delta_c)) ;
		current_index_ = -1;
	}
	
	/* basic verbose to string */
	public void _verbose(){
		System.out.print("fit: " + fitness_ + " move map: " );
		for(int _i = 0; _i < movement_list_.size(); _i++){
			System.out.print(movement_list_.get(_i) + " ");
		}
		System.out.println();
	}
}
