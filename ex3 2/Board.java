package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.games.controller.jits.BasicJitController;

@SuppressWarnings("serial")
public class Board extends JFrame {
	final int x = 8;
	final int y = 8;
	final int dim = 100;
	int[] monkey = new int[] { 0, 0 };
	int[] banana = new int[] { -1, -1 };
	int[] bed = new int[] { 0, 0 };
	int[] tv = new int[] { 2, 2 };
	int[] shower = new int[] { 2, 0 };
	BufferedImage m;

	ControllerExecutor executor;
	Map<String,String> inputs = new HashMap<String, String>();

	public void run() throws Exception {

		executor = new ControllerExecutor(new BasicJitController(), "out/jit", "MonkeyWithBananaEx3");
		m = ImageIO.read(new File("img/monkey.jpg"));
	    int[] randomNumbers = new int[] { 0,2,4,6};
	    Random r = new Random();
	    int nextRandomNumberIndex = r.nextInt(randomNumbers.length);
		banana[0] = randomNumbers[nextRandomNumberIndex];
		nextRandomNumberIndex = r.nextInt(randomNumbers.length);
		banana[1] = randomNumbers[nextRandomNumberIndex];
		inputs.put("banana[0]", Integer.toString(banana[0]));
		inputs.put("banana[1]", Integer.toString(banana[1]));
		executor.initState(inputs);
		
		Map<String, String> sysValues = executor.getCurrOutputs();
		
		monkey[0] = Integer.parseInt(sysValues.get("monkey[0]"));
		monkey[1] = Integer.parseInt(sysValues.get("monkey[1]"));
		
		paint(this.getGraphics());
		Thread.sleep(1000);
		
		while (true) {
			if (monkey[0] == banana[0] & monkey[1] == banana[1]) {
				nextRandomNumberIndex = r.nextInt(randomNumbers.length);
				banana[0] = randomNumbers[nextRandomNumberIndex];
				nextRandomNumberIndex = r.nextInt(randomNumbers.length);
				banana[1] = randomNumbers[nextRandomNumberIndex];
			}
			
			inputs.put("banana[0]", Integer.toString(banana[0]));
			inputs.put("banana[1]", Integer.toString(banana[1]));
			
			executor.updateState(inputs);
			
			sysValues = executor.getCurrOutputs();
			
			monkey[0] = Integer.parseInt(sysValues.get("monkey[0]"));
			monkey[1] = Integer.parseInt(sysValues.get("monkey[1]"));
			
			paint(this.getGraphics());
			Thread.sleep(1000);
		}
	}

	@Override
	public void paint(Graphics g) {
		int row;
		int col;

		for (row = 0; row < y; row++) {
			for (col = 0; col < x; col++) {
				if ((row % 2) == (col % 2))
					g.setColor(Color.WHITE);
				else
					g.setColor(Color.LIGHT_GRAY);

				g.fillRect(col * dim, row * dim, dim, dim);
			}
		}

		g.setColor(Color.BLACK);
		g.drawString("BED", bed[0] * dim + 40, bed[1] * dim + 50);
		g.drawString("SHOWER", shower[0] * dim + 20, shower[1] * dim + 50);
		g.drawString("TV", tv[0] * dim + 40, tv[1] * dim + 50);

		if (banana[0] != -1) {
			g.setColor(Color.YELLOW);
			g.fillRect(banana[0] * dim, banana[1] * dim, dim, dim);
			g.setColor(Color.BLACK);
			g.drawString("BANANA", banana[0] * dim + 30, banana[1] * dim + 50);
		}
		if (m != null) {
			g.drawImage(m, monkey[0] * dim, monkey[1] * dim, null);
		} else {
			g.setColor(Color.BLUE);
			g.fillRect(monkey[0] * dim, monkey[1] * dim, dim, dim);
			g.setColor(Color.WHITE);
			g.drawString("MONKEY", monkey[0] * dim + 20, monkey[1] * dim + 50);
		}
	}

	public static void main(String args[]) throws Exception {
		Board check = new Board();
		check.setTitle("monkey");
		check.setSize(check.x * check.dim, check.y * check.dim);
		check.setDefaultCloseOperation(EXIT_ON_CLOSE);
		check.setVisible(true);
		check.run();
	}
}