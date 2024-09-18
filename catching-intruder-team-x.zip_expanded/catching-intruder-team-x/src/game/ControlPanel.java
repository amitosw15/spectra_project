package game;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.games.controller.jits.BasicJitController;

/*
 * Manages the simulation - GUI, controller input/output, board (visualization)
 */

public class ControlPanel {
	// board dimensions
	int x;
	int y;
	// board constants
	final int dim = 130;
	static final int y_offset = 30;

	int num_robots;
	int num_targets;

	Point[] robots;
	Point[] fixedObstacles;

//	Point elevator;
	int state = 0;

	int stays = 0;
	
	int stuckTurns = 0;

	// holds the robots previous position (for use when animating transitions)
	Point[] robots_prev = new Point[num_robots];

	// Board and GUI elements
	JFrame frame;
	Board board;
	JButton advance_button;
	JButton autorun_button;

	// holds states for the animation
	boolean ready_for_next;
	boolean autorun;
	boolean shouldJump;

	// The controller and its inputs
	ControllerExecutor executor;
	Map<String, String> inputs = new HashMap<String, String>();

	// The path to the controller files
	String path;
	
	// random generator for guard location.
	Random rand = new Random();
	Point intruder;

	public ControlPanel(int x, int y, int num_robots, String path) {
		this.x = x;
		this.y = y;
		this.num_robots = num_robots;
		this.robots = new Point[num_robots];
		this.robots_prev = new Point[num_robots];
		this.fixedObstacles = new Point[4];
		this.path = path;
		this.shouldJump = false;
	}

	public void init() throws Exception {
		autorun = false;

		for (int i = 0; i < num_robots; i++) {
			robots[i] = new Point();
			robots_prev[i] = new Point();
		}

		// init controller
		executor = new ControllerExecutor(new BasicJitController(), this.path, "CatchingIntruder");

		// TODO: initial input values using inputs.put(...)

		// set initial robot locations
		// TODO: you may initial other things in a similar way

		this.fixedObstacles[0] = new Point(2, 1);
		this.fixedObstacles[1] = new Point(3, 1);
		this.fixedObstacles[2] = new Point(4, 1);
		this.fixedObstacles[3] = new Point(5, 1);
		
		this.setInitialIntruderLocation();

		executor.initState(inputs);

		Map<String, String> sysValues = executor.getCurrOutputs();

		for (int i = 0; i < num_robots; i++) {
			robots_prev[i].setX(Integer.parseInt(sysValues.get("robotX" + Integer.toString(i))));
			robots_prev[i].setY(Integer.parseInt(sysValues.get("robotY" + Integer.toString(i))));
			robots[i].setX(Integer.parseInt(sysValues.get("robotX" + Integer.toString(i))));
			robots[i].setY(Integer.parseInt(sysValues.get("robotY" + Integer.toString(i))));
		}

		setUpUI();

		System.out.println("1");
	}
	
	void setInitialIntruderLocation() {
		do {
			int intruderX = this.rand.nextInt(this.x - 1);
			this.intruder = new Point(intruderX, this.y - 1);
		} while(this.isElementOnRobots(this.intruder.getX(), this.intruder.getY()));
		inputs.put("intruderX", Integer.toString(this.intruder.getX()));
		inputs.put("intruderY", Integer.toString(this.intruder.getY()));
	}

	boolean isElementOnObstacle(int x, int y) {
		return y == 1 && x >= 1 && x <= 5;
	}
	boolean isElementOnRobots(int x, int y) {
		for (int i = 0; i < this.num_robots; i++) {
			int robotX = this.robots[i].getX();
			int robotY = this.robots[i].getY();
			if (robotX == x && robotY == y) {
				return true;
			}
		}
		return false;
	}
	boolean inBounds(int x, int y) {
		return x >=0 && x < this.x && y >= 0 && y < this.y;
	}
	
	
	void setNextIntruderLocation() {
		if (this.stuckTurns == 4) {
			this.setInitialIntruderLocation();
			this.stuckTurns = 0;
			return;
		}
		int x = this.intruder.getX();
		int y = this.intruder.getY();
		Point[] validPoints = new Point[5];
		int[][] moves = {{0, 0}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
		int count = 0;
		boolean isAdjacentToRobots = false;
		for (int i = 0; i < moves.length; i++) {
			int[] move = moves[i];
			int nextX = x + move[0];
			int nextY = y + move[1];
			isAdjacentToRobots = isAdjacentToRobots || this.isElementOnRobots(nextX, nextY);
			if (this.inBounds(nextX, nextY) && !this.isElementOnRobots(nextX, nextY) && !this.isElementOnObstacle(nextX, nextY)) {
				validPoints[count++] = new Point(nextX, nextY);
			}
		}

		if (isAdjacentToRobots) {
			if (count == 1) {
				this.stuckTurns++;
			}
			else {
				this.stuckTurns=0;
			}
			this.intruder =  validPoints[count == 1 ? 0 : this.rand.nextInt(count - 1) + 1];
		}
		else {
			this.intruder =  validPoints[this.rand.nextInt(count)];
			this.stuckTurns=0;
		}
		this.inputs.put("intruderX", Integer.toString(this.intruder.getX()));
		this.inputs.put("intruderY", Integer.toString(this.intruder.getY()));
	}
	void printInputs() {
		for (Map.Entry<String, String> entry : this.inputs.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
	}

	// handle next turn
	void next() throws Exception {
		ready_for_next = false;
		state += 1;
		advance_button.setText("...");
		for (int i = 0; i < num_robots; i++) {
			robots_prev[i].setX(robots[i].getX());
			robots_prev[i].setY(robots[i].getY());
		}

		// TODO: put here your ramdom env inputs
		this.setNextIntruderLocation();
//		this.printInputs();
		executor.updateState(inputs);

		// Receive updated values from the controller
		Map<String, String> sysValues = executor.getCurrOutputs();

//		System.out.println(sysValues);

		// Update robot locations
		for (int i = 0; i < num_robots; i++) {
			robots[i].setX(Integer.parseInt(sysValues.get("robotX" + Integer.toString(i))));
			robots[i].setY(Integer.parseInt(sysValues.get("robotY" + Integer.toString(i))));
		}

		// Animate transition
		board.animate();
	}

	void setUpUI() throws Exception {
		advance_button = new JButton();
		autorun_button = new JButton();
		frame = new JFrame();
		frame.add(advance_button);
		frame.add(autorun_button);
		board = new Board(this);
		board.init();
		frame.setTitle("Robots");
		frame.setSize(x * dim + 8 + 150, y * dim + y_offset + 8);
		board.setSize(x * dim, y * dim);
		frame.setLayout(null);
		frame.add(board, BorderLayout.CENTER);

		// Handle presses of the "next step" button
		advance_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (ready_for_next && !autorun)
						next();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		});
		advance_button.setBounds(x * dim + 8, 0, 130, 50);
		advance_button.setText("Start");

		// Handle presses of the "autorun/stop autorun" button
		autorun_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (autorun) {
						autorun = false;
						autorun_button.setText("Auto run");
					} else if (ready_for_next) {
						autorun = true;
						autorun_button.setText("Stop Auto run");
						next();
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		});

		autorun_button.setBounds(x * dim + 8, 50, 130, 50);
		autorun_button.setText("Auto run");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		board.setVisible(true);
		advance_button.setVisible(true);
		autorun_button.setVisible(true);
		ready_for_next = true;
	}
}
