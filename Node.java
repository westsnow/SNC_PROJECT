import java.util.HashMap;



class Node {
	int nodeId;
	double t; //threshold
	double groupEffect;
//	HashMap<Integer, Double> outEdges;
	HashMap<Integer, Double> inEdges;

	
	
	public Node(int id) {
		nodeId = id;
//		outEdges = new HashMap<Integer, Double>();
		inEdges = new HashMap<Integer, Double>();
	}
	
	
}

