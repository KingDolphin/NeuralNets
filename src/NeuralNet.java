import java.io.*;

public class NeuralNet {
	
	public int inputs, outputs, hiddenLayers;
	public int[] counts;
	public Perceptron[][] layers;
	
	public NeuralNet(int inputs, int outputs, int hiddenLayers, int... counts) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.hiddenLayers = hiddenLayers;
		this.counts = counts;
		this.layers = new Perceptron[hiddenLayers+1][];
		
		for (int l = 0; l < hiddenLayers; l++) {
			layers[l] = new Perceptron[counts[l]];
			for (int j = 0; j < layers[l].length; j++)
				layers[l][j] = new Perceptron((l == 0 ? inputs : counts[l-1]));
		}
		layers[layers.length-1] = new Perceptron[outputs];
		for (int j = 0; j < outputs; j++)
			layers[layers.length-1][j] = new Perceptron(counts[counts.length-1]);
	}
	
	public void save(String filename) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filename, "UTF-8");
		for (int l = 0; l < layers.length; l++) {
			for (int p = 0; p < layers[l].length; p++)
				layers[l][p].save(writer);
		}
		writer.close();
	}
	
	public void load(String filename) throws IOException {
		BufferedReader stream = new BufferedReader(new FileReader(filename));
		for (int l = 0; l < layers.length; l++) {
			for (int p = 0; p < layers[l].length; p++)
				layers[l][p].load(stream);
		}
		stream.close();
	}
	
	public double[] guess(double[] inputs) {
		for (int l = 0; l < layers.length; l++) {
			double[] outputs = new double[layers[l].length];
			for (int p = 0; p < layers[l].length; p++)
				outputs[p] = layers[l][p].guess(inputs);
			inputs = outputs;
		}
		return inputs;
	}
	
}
