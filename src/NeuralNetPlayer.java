import java.io.IOException;

public class NeuralNetPlayer extends Player {

	public NeuralNet nn;
	
	public NeuralNetPlayer(Connect4 c4, int color) {
		super(c4, color);
		
		nn = new NeuralNet(c4.board.length*3, c4.w, 3, c4.board.length*3, c4.board.length*3, c4.board.length*3);
//		nn = new NeuralNet(c4.board.length, c4.w, 3, c4.board.length, c4.board.length, c4.board.length);
	}
	
	public NeuralNetPlayer(Connect4 c4, int color, String file) throws IOException {
		this(c4, color);
		
		nn.load(file);
	}

	@Override
	public boolean play() throws InterruptedException {
		double[] inputs = new double[c4.board.length*3];
		for (int i = 0; i < c4.board.length; i++) {
			int scl = c4.board[i] == 0 ? 0 : (c4.board[i] == color ? 1 : 2);
			inputs[3*i + scl] = 1;
//			inputs[i] = (c4.board[i] == color ? +1 : (c4.board[i] == 0 ? 0 : -1));
		}
		
		double[] outputs = nn.guess(inputs);
		int row = -1;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < outputs.length; i++) {
			if (outputs[i] > max && c4.isLegal(i)) {
				max = outputs[i];
				row = i;
			}
		}
		c4.place(row, color);
		
		if (c4.frame != null)
			c4.frame.repaint();
		
		return false;
	}
	
}
