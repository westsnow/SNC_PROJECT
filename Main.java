import java.util.Random;


public class Main {

	public static void main(String[] args) {
		double groupAlpha = Double.parseDouble(args[0]);
		int iterationTime = Integer.parseInt(args[1]);
		int B = Integer.parseInt(args[2]); 
		Graph g = new Graph(groupAlpha, iterationTime, B);
		g.run();
	}

}
