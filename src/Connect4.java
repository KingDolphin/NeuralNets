import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Connect4 {
	
	static int COUNT = 0;
	
	public JFrame frame;
	public final int w, h;
	public int[] board;
	public int target;
	// 4,9,14
	
	private int lastPlaceX = -1, lastPlaceY = -1;
	
	Player t_p0, t_p1;
	
	public int player;
	
	public Connect4(int w, int h, int target) {
		this.w = w;
		this.h = h;
		this.board = new int[w*h];
		this.target = target;
		this.player = +1;
	}
	
	public void setup() {
		frame = new JFrame("Neural net");
		frame.setSize(500, 500*h/w);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setUndecorated(false);
		frame.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) { draw(g); }
		});
		new Thread(() -> {
			int cnt = COUNT++;
			int save = JOptionPane.showConfirmDialog(frame, "Save " + cnt + "?", "Save?", JOptionPane.YES_NO_OPTION);
			if (save == 0 && t_p0 instanceof NeuralNetPlayer) {
				try {
					((NeuralNetPlayer)t_p0).nn.save("weights_" + cnt + ".txt");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		frame.setVisible(true);
	}
	
	public void draw(Graphics g) {
		if (frame == null)
			return;
		
		double ww = (double)frame.getWidth() / w;
		double hh = (double)frame.getHeight() / h;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int b = board[x + y*w];
				if (b == -1) {
					g.setColor(Color.BLUE);
				} else if (b == 0) {
					g.setColor(Color.WHITE);
				} else if (b == 1) {
					g.setColor(Color.RED);
				} else {
					System.out.println("wtf happened " + b);
				}
				g.fillRect((int)(x*ww), (int)(y*hh), (int)((x+1)*ww), (int)((y+1)*hh));
			}
		}
	}
	
	public int at(int x, int y) {
		return board[x + y * w];
	}
	
	public void set(int x, int y, int val) {
		board[x + y * w] = val;
	}
	
	public boolean isValid(int x, int y) {
		return (x < w && y < h && x >= 0 && y >= 0);
	}
	
	public void place(int row, int color) {
		if (row == -1) {
			System.out.println("TIE! " + hasTie());
			return;
		}
		int y = 0;
		while ((y+1) < h && at(row, y+1) == 0)
			y++;
		set(row, y, color);
		
		lastPlaceX = row;
		lastPlaceY = y;
		player *= -1;
	}
	
	public boolean isLegal(int row) {
		return (board[row] == 0);
	}
	
	public boolean hasTie() {
		for (int x = 0; x < w; x++) {
			if (isLegal(x))
				return false;
		}
		return true;
	}
	
	public boolean hasWon() {
		if (lastPlaceX == -1 || lastPlaceY == -1)
			return false;
		
		int x0 = lastPlaceX, y0 = lastPlaceY;
		int target = at(x0, y0);
		for (int a = 0; a < 4; a++) {
			int x = x0, y = y0;
			int dx = (int)(Math.round(Math.cos(a * 2 * Math.PI / 8)));
			int dy = (int)(Math.round(Math.sin(a * 2 * Math.PI / 8)));
			int count = 0;
			while (isValid(x, y) && at(x, y) == target && count < this.target) {
				count++;
				x += dx;
				y += dy;
			}
			dx *= -1;
			dy *= -1;
			x = x0 + dx;
			y = y0 + dy;
			while (isValid(x, y) && at(x, y) == target && count < this.target) {
				count++;
				x += dx;
				y += dy;
			}
			if (count >= this.target)
				return true;
		}
		return false;
	}
	
	static Thread thread = null;
	
	static Player play(Player p1, Player p2, boolean display) {
		Connect4 c4 = new Connect4(3, 3, 2);
		p1.color = +1;
		p2.color = -1;
		p1.c4 = c4;
		p2.c4 = c4;
		c4.t_p0 = p1;
		c4.t_p1 = p2;
		if (display)
			c4.setup();
		Player curPlayer = p1;
		while (!c4.hasWon() && !c4.hasTie()) {
			try {
				if (curPlayer.play())
					thread.wait();
				
				if (display)
					Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			curPlayer = (curPlayer == p1 ? p2 : p1);
		}
		return (curPlayer == p1 ? p2 : p1);
	}
	
	static NeuralNetPlayer[] players;
	
	public static void main(String[] args) throws IOException {
		Connect4 c4 = new Connect4(3, 3, 2);
		
		players = new NeuralNetPlayer[100];
		
		for (int i = 0; i < players.length; i++) {
			NeuralNetPlayer p = new NeuralNetPlayer(c4, 1);
			players[i] = p;
		}
		
		final int GAMES = 100;
		thread = new Thread(() -> {
			int count = 0;
			while (count < GAMES) {
				// Start tournaments
				List<NeuralNetPlayer> winners = new ArrayList<>();
				List<NeuralNetPlayer> finalists = new ArrayList<>();
				
				// First bracket
				for (int i = 0; i < players.length/2; i++) {
					NeuralNetPlayer p1 = players[i];
					NeuralNetPlayer p2 = players[i+players.length/2];
					NeuralNetPlayer winner = (NeuralNetPlayer)play(p1, p2, false);
					winners.add(winner);
				}
				
				// Second bracket
				for (int i = 0; i < winners.size()/2; i++) {
					NeuralNetPlayer p1 = winners.get(i);
					NeuralNetPlayer p2 = winners.get(i+winners.size()/2);
					NeuralNetPlayer winner = (NeuralNetPlayer)play(p1, p2, count == GAMES-1);
					finalists.add(winner);
				}
				
				
				// BREED!
				players = new NeuralNetPlayer[100];
				int p = 0;
				for (int i = 0; i < finalists.size(); i++) {
					NeuralNet[] children = GeneticAlgo.breed(finalists.get(i).nn, finalists.get((i+1)%finalists.size()).nn, 4);
					for (int j = 0; j < children.length; j++) {
						NeuralNetPlayer nnp = new NeuralNetPlayer(c4, 1);
						nnp.nn = children[j];
						players[p++] = nnp;
					}
					GeneticAlgo.mutate(players[p-1].nn, 0.5);
				}
				
				count++;
			}
			
		}, "name");
		thread.start();
		
//		Connect4 c4 = new Connect4(3, 3, 2);
//		c4.setup();
//		
//		Player p1 = new RealPlayer(c4, 1);
//		Player p2 = new NeuralNetPlayer(c4, -1);//NeuralNetPlayer(c4, -1, "weights_9.txt");
//		
//		thread = new Thread(() -> {
//			Player curPlayer = p1;
//			synchronized (thread) {
//				while (!c4.hasWon()) {
//					try {
//						if (curPlayer.play())
//							thread.wait();
//						
//						Thread.sleep(100);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//					curPlayer = (curPlayer == p1 ? p2 : p1);
//				}
//			}
//			
//		}, "name");
//		thread.start();
	}
	
}
