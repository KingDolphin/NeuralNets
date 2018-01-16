import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Perceptron {
	
	double[] weights;
	double learningRate = 0.005;
	
	public Perceptron(int num) {
		weights = new double[num];
		for (int i = 0; i < num; i++)
			weights[i] = (Math.random() - 0.5) * 2;
	}
	
	static double sign(double x) {
		return (x >= 0 ? +1 : -1);
	}
	
	static double tanh(double x) {
		return Math.tanh(x);
	}
	
	static double dtanh(double x) {
		double t = tanh(x);
		return 1 - t * t;
	}
	
	double guess(double[] inputs) {
		double sum = 0;
		for (int i = 0; i < inputs.length; i++)
			sum += weights[i] * inputs[i];
		return tanh(sum);
	}
	
	void train(double[] inputs, double target) {
		double guess = guess(inputs);
		double err = target - guess;
		if (err != 0) {
			for (int i = 0; i < inputs.length; i++)
				weights[i] += err * learningRate * inputs[i];
		}
	}
	
	public void save(PrintWriter pw) {
		for (int i = 0; i < weights.length; i++)
			pw.print(weights[i] + " ");
		pw.println();
	}
	
	public void load(BufferedReader stream) throws IOException {
		String line = stream.readLine();
		String[] terms = line.split(" ");
		for (int i = 0; i < weights.length; i++)
			weights[i] = Double.parseDouble(terms[i]);
	}
	
}
