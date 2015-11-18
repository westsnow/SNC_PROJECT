import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;


public class GetSmallerDataSet {
	static final int  nodeNum = 100;
	public static void getSmallerEdgeSet(){
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("/Users/westsnow/Downloads/Flickr-dataset/data/edges.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			File fout = new File("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/edges.csv");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			File fout_node = new File("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/nodes.csv");
			FileOutputStream fos_node = new FileOutputStream(fout_node);
			BufferedWriter bw_node = new BufferedWriter(new OutputStreamWriter(fos_node)); 
			
			String strLine;

			while ((strLine = br.readLine()) != null)   {
				String[] values = strLine.split(",");
				int node1 = Integer.parseInt(values[0]);
				int node2 = Integer.parseInt(values[1]);
				if( node1 <= nodeNum && node2<nodeNum){
					String out = node1 + "," + node2;
					bw.write(out);
					bw.newLine();
				}
				
			}
			
			for(int i = 1; i <= nodeNum; ++i){
				bw_node.write(""+i);
				bw_node.newLine();
			}
			
			bw_node.close();
			br.close();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getSmallerGroupSet(){
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("/Users/westsnow/Downloads/Flickr-dataset/data/group-edges.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			File fout = new File("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/group-edges.csv");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			File fout_g = new File("/Users/westsnow/Downloads/Flickr-dataset/data_smaller/groups.csv");
			FileOutputStream fos_g = new FileOutputStream(fout_g);
			BufferedWriter bw_g = new BufferedWriter(new OutputStreamWriter(fos_g));
		 
			
			String strLine;
			HashSet<Integer> groups = new HashSet<Integer>();

			while ((strLine = br.readLine()) != null)   {
				String[] values = strLine.split(",");
				int node1 = Integer.parseInt(values[0]);
				int node2 = Integer.parseInt(values[1]);

				if( node1 <= nodeNum ){
					groups.add(node2);
					bw.write(strLine);
					bw.newLine();
				}
				
			}
			for(Integer g : groups){
				bw_g.write(g.toString());
				bw_g.newLine();
			}

			bw_g.close();
			br.close();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		getSmallerEdgeSet();
		getSmallerGroupSet();
		
		
	}
}
