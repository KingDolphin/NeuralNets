import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RealPlayer extends Player {
	
	private Thread thread;

	public RealPlayer(Connect4 c4, int color) {
		super(c4, color);
		
		c4.frame.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) { click(e); }
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
		});
	}
	
	public void click(MouseEvent e) {
		if (c4.player != color || c4.hasWon())
			return;
		
		int x = (e.getX() * c4.w / c4.frame.getWidth());
		c4.place(x, color);
		
		c4.frame.repaint();

		synchronized (thread) {
			thread.notify();
		}
	}
	
	@Override
	public boolean play() throws InterruptedException {
		thread = Thread.currentThread();
		return true;
	}
	
}
