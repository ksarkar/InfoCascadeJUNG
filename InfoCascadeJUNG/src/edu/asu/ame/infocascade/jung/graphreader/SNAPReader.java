package edu.asu.ame.infocascade.jung.graphreader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphFile;


/**
 * Reads edge-list based SNAP data files into the a JUNG DirectedSparseGraph<String, Integer> 
 * structure.
 * 
 * @author ksarkar1
 *
 */

public class SNAPReader implements GraphFile<String, Long>{

	@Override
	public Graph<String, Long> load(String filename) {
		try {
			BufferedReader reader =
				new BufferedReader(new FileReader(filename));
			Graph<String, Long> graph = load(reader);
			reader.close();
			return graph;
		} catch (IOException ioe) {
			throw new RuntimeException("Error in loading file " + filename, ioe);
		}
	}

	private Graph<String, Long> load(BufferedReader reader) throws IOException {
		Graph<String, Long> g = new DirectedSparseGraph<String, Long>();
		String line = null;
		long i = 1;
		
		while((line = reader.readLine()) != null) {
			if (line.charAt(0) != '#') { // check if its a header line
				String[] nodes = line.split("\\s");
				g.addEdge(i, nodes[0], nodes[1]);
				i++;
			}
		}
		
		return g;						
	}

	@Override
	public void save(Graph<String, Long> arg0, String arg1) {
		// TODO Auto-generated method stub	
	}
	
	public static void main(String[] args) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/test-graph.txt");
		
		System.out.println("The graph g: \n" + g.toString());
		System.out.println(g.getVertexCount());
	}


}
