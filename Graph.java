import java.io.*;
import java.util.*;





public class Graph {
	//nodeId -> Node
	
	HashMap<Integer, Node> nodeMap;
	HashMap<Integer, List<Integer>> groupMap;
	ArrayList<Integer> groupIds;
	Set<Integer> seedSet;
	double groupAlpha = 1;
	double maxFinalGoodNodeNumber = 0.0;
	int iterationTime = 10;
	int B = 10;

	
	
	private void outputWeightInfo() {
		try {
			File fout = new File("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/edge-weight.csv");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			for(Integer nodeId : nodeMap.keySet()) {
				Node node = nodeMap.get(nodeId);
				for(Integer inId : node.inEdges.keySet()) {
					bw.write(nodeId + "," + inId + "," + node.inEdges.get(inId));
					bw.newLine();
				}
			}
			bw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//for each node n, calculate weights from neighbors to n. the sum of weights is 1
	private void assignEdgeWeight() {
		for(Integer nodeId : nodeMap.keySet()) {
			Random r = new Random();
			Node node = nodeMap.get(nodeId);
			Set<Integer> inSet = new HashSet<Integer>(node.inEdges.keySet());
			int size = inSet.size();
			ArrayList<Double> weights = new ArrayList<Double>();
			double sum = 0.0;
			while(size-- > 0) {
				double d = r.nextDouble();
				weights.add(d);
				sum += d;
			}
			int index = 0;
			for(Integer i : inSet) {
				node.inEdges.put(i, weights.get(index++) / sum);
			}
		}
	}
	
	private void readData() {
		try {
			FileInputStream fstream_g_edges = new FileInputStream("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/group-edges.csv");
			BufferedReader br_g_edges = new BufferedReader(new InputStreamReader(fstream_g_edges));
			
			FileInputStream fstream_edges = new FileInputStream("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/edges.csv");
			BufferedReader br_edges = new BufferedReader(new InputStreamReader(fstream_edges));
			
			FileInputStream fstream_group = new FileInputStream("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/groups.csv");
			BufferedReader br_groups = new BufferedReader(new InputStreamReader(fstream_group));
			
			FileInputStream fstream_nodes = new FileInputStream("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/nodes.csv");
			BufferedReader br_nodes = new BufferedReader(new InputStreamReader(fstream_nodes));

			String strLine;

			//construct nodes
			while ((strLine = br_nodes.readLine()) != null)   {
				int nodeId = Integer.parseInt(strLine);
				Node n = new Node(nodeId);
				nodeMap.put(nodeId, n);
			}
			
			//construct edges
			while ((strLine = br_edges.readLine()) != null)   {
				String[] values = strLine.split(",");
				int node1 = Integer.parseInt(values[0]);
				int node2 = Integer.parseInt(values[1]);
				nodeMap.get(node1).inEdges.put(node2, 0.0);
//				nodeMap.get(node1).outEdges.put(node2, 0.0);
				nodeMap.get(node2).inEdges.put(node1, 0.0);
//				nodeMap.get(node2).outEdges.put(node1, 0.0);
			}
			
			//read group info
			while ((strLine = br_groups.readLine()) != null)   {
				int groupId = Integer.parseInt(strLine);
				groupMap.put(groupId, new LinkedList<Integer>());
				groupIds.add(groupId);
			}
			
			//read group edge information
			while ((strLine = br_g_edges.readLine()) != null)   {
				String[] values = strLine.split(",");
				int nodeId = Integer.parseInt(values[0]);
				int groupId = Integer.parseInt(values[1]);
				groupMap.get(groupId).add(nodeId);
			}
			
			System.out.println("graph topo structure has been read");			
			
			br_nodes.close();
			br_edges.close();
			br_g_edges.close();
			br_groups.close();
			
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}
	
	public void setEdgeWeight() {
		assignEdgeWeight();
		System.out.println("edge weights has been randomly set");
	}
	
	public void chooseSeedSet() {
		int size = nodeMap.size() / 100;
		Random r = new Random();
		while(seedSet.size() < size) {
			int rInt = r.nextInt(nodeMap.size()) + 1;
			if( !seedSet.contains(rInt) && nodeMap.containsKey(rInt))
				seedSet.add(rInt);
		}
		System.out.println("seedset " + seedSet.size());
		for(Integer i : seedSet){
			System.out.println(i);
		}
	}
	
	
	public Graph(double groupAlpha, int iterationTime, int B) {
		this.groupAlpha = groupAlpha;
		this.iterationTime = iterationTime;
		this.B = B;
		init();
		readData();
		setEdgeWeight();
		chooseSeedSet();
	}
	
	private void init() {
		nodeMap = new HashMap<Integer, Node>();
		seedSet = new HashSet<Integer>();
		groupMap = new HashMap<Integer, List<Integer>>();
		groupIds = new ArrayList<Integer>();
	}
	
	public void run() {
//		Set<Integer> individualTarget = new HashSet<Integer>();
//		Set<Integer> groupTarget = new HashSet<Integer>();
		int nodeNum = nodeMap.size();
		int groupNum = groupMap.size();
//		int nodeNum = 100;
//		int groupNum = 100;
		// X[1-nodeNum] represents nodes. X[nodeNum + 1, nodeNum + groupNum] represents groups
		int[] X = new int[nodeNum + groupNum + 1];
		int[] optimizedX = new int[nodeNum + groupNum + 1];
//		helper(X, B, 1, nodeNum, groupNum, optimizedX);
		helper(X, optimizedX, B, nodeNum, groupNum);
		System.out.println("the maxFinalGoodNodeNumber is " + maxFinalGoodNodeNumber);
		for(int i = 1; i <= nodeNum; ++i) {
			System.out.print(optimizedX[i] + " ");
		}
		System.out.println();
		System.out.println("group Info");

		for(int i = nodeNum+1; i < optimizedX.length; ++i) {
			System.out.print(optimizedX[i] + " ");
		}
	}

	private void helper(int[] X, int[] optimizedX, int B, int nodeNum, int groupNum) {
		System.out.println("total budget is " + B);
		double maxGoodNode = 0.0;
		while(B > 0) {
			maxGoodNode = 0;
			int optimalPos = 0;
			for(int i = 1; i < X.length; ++i) {
				//if node[i] is already a bad node, don't bother transferring it to good node.
				if( seedSet.contains(i)) continue;
				// if this node is already a good node, ignore it.
				if( X[i] > 0 && i <= nodeNum) continue;
				
				X[i]++;
				double finalGoodNode = estimate(X, nodeNum, groupNum);
				System.out.println("estimated node " + i );
				if( finalGoodNode > maxGoodNode) {
					maxGoodNode = finalGoodNode;
					optimalPos = i;
				}
				X[i]--;
			}
			B--;
			X[optimalPos]++;
			System.out.println("when B is " + B + ", maxFinalGoodNode is " + maxGoodNode);
		}
		for(int i = 0; i < X.length; ++i) {
			optimizedX[i] = X[i];
		}
		maxFinalGoodNodeNumber = maxGoodNode;
	}
	
//	private void helper(int[] X, int B, int startPos, int nodeNum, int groupNum, int[] oX) {
//		// if point is already bad, dont transfer it to good node
//		
//		if (startPos >= X.length)
//			return;
//		
//		if (seedSet.contains(startPos) ) {
//			helper(X, B, startPos + 1, nodeNum, groupNum, oX);
//		}
//		if (B == 0) {
//			double avg = estimate(X, nodeNum, groupNum);
//			if( avg > maxFinalGoodNodeNumber ) {
//				maxFinalGoodNodeNumber = Math.max(maxFinalGoodNodeNumber, avg);
//				for(int i = 1; i < X.length; ++i)
//					oX[i] = X[i];
//			}
//			return;
//		}
//		if (startPos <= nodeNum) {
//			helper(X, B, startPos + 1, nodeNum, groupNum, oX);
//			X[startPos]++;
//			helper(X, B - 1, startPos + 1, nodeNum, groupNum, oX);
//			X[startPos]--;
//		} else {
//			helper(X, B, startPos + 1, nodeNum, groupNum, oX);
//			X[startPos]++;
//			helper(X, B - 1, startPos, nodeNum, groupNum, oX);
//			X[startPos]--;
//		}
//	}
	
	private void addGroupEffect(int[] X, int startPos) {
		int groupIndex = 0;
		for(int i = startPos; i < X.length; ++i) {
			int budget = X[i];
			if(budget == 0) continue;
			int groupId = groupIds.get(groupIndex++);
			List<Integer> group = groupMap.get(groupId);
			double effect = groupAlpha * budget / groupMap.size();
			for(Integer nodeId : group) {
				nodeMap.get(nodeId).groupEffect = effect;
			}
		}
	}
	
	private void randomlizeNodeThresHold() {
		Random r= new Random();
		for(Node node : nodeMap.values()) {
			node.t = r.nextDouble();
		}
	}
	
	private int iterate(HashSet<Integer> badNodes, HashSet<Integer> goodNodes) {
		randomlizeNodeThresHold();
		
		while(true) {
			int goodSize = goodNodes.size();
			for(Integer nodeId : nodeMap.keySet()) {
				if( badNodes.contains(nodeId) || goodNodes.contains(nodeId))
					continue;
				Node node = nodeMap.get(nodeId);
				HashMap<Integer, Double> inEdges = node.inEdges;
				double badEffect = 0.0;
				double goodEffect = 0.0;
				for(Integer neighbor : inEdges.keySet()) {
					if( badNodes.contains(neighbor)) {
						badEffect += inEdges.get(neighbor);
					} else if(goodNodes.contains(neighbor)) {
						goodEffect += inEdges.get(neighbor);
					}
				}
				goodEffect += node.groupEffect;
				if(goodEffect >= badEffect) {
					if(goodEffect > node.t)
						goodNodes.add(nodeId);
				} else {
					if(badEffect > node.t)
						badNodes.add(nodeId);
				}
			}
			if(goodNodes.size() == goodSize) {
				return goodSize;
			}
		}
	}
	
	private void clearGroupEffect() {
		for(Node n : nodeMap.values())
			n.groupEffect = 0.0;
	}
	
	private double estimate(int[] X, int nodeNum, int groupNum){
		addGroupEffect(X, nodeNum + 1);
		HashSet<Integer> badNodes = new HashSet<Integer>(seedSet);
		HashSet<Integer> goodNodes = new HashSet<Integer>();
		for(int i = 1; i < nodeMap.size(); ++i) {
			if(X[i] == 1) {
				goodNodes.add(i);
			}
		}
		
		int[] result = new int[iterationTime];
		for(int i = 0; i < iterationTime; ++i)
			result[i] = iterate(new HashSet<Integer>(badNodes), new HashSet<Integer>(goodNodes));
		
		clearGroupEffect();
		return getAverage(result);
	}
	
	private double getAverage(int[] result) {
		int sum = 0;
		for(int i = 0; i < result.length; ++i) {
			sum += result[i];
		}
		return (double)sum / result.length;
	}
}

