

public abstract class Player {
	
	public Connect4 c4;
	public int color;
	
	public Player(Connect4 c4, int color) {
		this.c4 = c4;
		this.color = color;
	}
	
	public abstract boolean play() throws InterruptedException;
	
}
