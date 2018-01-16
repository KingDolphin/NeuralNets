

public class GeneticAlgo {

	public static NeuralNet[] breed(NeuralNet mom, NeuralNet dad, int numChildren) {
		final double CROSSOVER_RATE = 0.5;
		final double MUTATION_AMT = 0.05;
		
		final double WEIGHT = 1.0;//2. / Math.sqrt((mom.fitness + dad.fitness) / 2. + 0.1);
		
		NeuralNet[] children = new NeuralNet[numChildren];
		
		for (int c = 0; c < numChildren; c++) {
			NeuralNet child = crossover(mom, dad, CROSSOVER_RATE);
			mutate(child, MUTATION_AMT * WEIGHT);
			children[c] = child;
		}
		
		return children;
	}
	
	public static NeuralNet crossover(NeuralNet a, NeuralNet b, double crossover_rate) {
		NeuralNet child = new NeuralNet(a.inputs, a.outputs, a.hiddenLayers, a.counts);
		for (int l = 0; l < child.layers.length; l++) {
			for (int p = 0; p < child.layers[l].length; p++) {
				Perceptron cp = child.layers[l][p];
				for (int w = 0; w < cp.weights.length; w++)
					cp.weights[w] = (Math.random() < crossover_rate ? a.layers[l][p].weights[w] : b.layers[l][p].weights[w]);
			}
		}
		return child;
	}
	
	public static void mutate(NeuralNet nn, double mutation_amt) {
		for (int l = 0; l < nn.layers.length; l++) {
			for (int p = 0; p < nn.layers[l].length; p++) {
				Perceptron cp = nn.layers[l][p];
				for (int w = 0; w < cp.weights.length; w++)
					cp.weights[w] += (Math.random() * 2 - 1) * mutation_amt;
			}
		}
	}
	
}
