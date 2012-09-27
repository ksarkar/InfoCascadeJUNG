package edu.asu.ame.infocascade.jung;

import java.util.Collection;
import java.util.HashMap;

import edu.asu.ame.infocascade.jung.model.lt.LTEdgeValue;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Graph<Integer, Integer> g = new DirectedSparseGraph<Integer, Integer>();
		
		g.addEdge(1, new Integer(1), new Integer(2));
		g.addEdge(2, 2, 3);
		g.addEdge(3, 3, 4);
		g.addEdge(4, 3, 5);
		g.addEdge(5, 4, 5);
		
		g.addEdge(6, new Integer(2), new Integer(1));
		g.addEdge(7, 3, 2);
		g.addEdge(8, 4, 3);
		g.addEdge(9, 5, 3);
		g.addEdge(10, 5, 4);
		
		System.out.println("The graph g: \n" + g.toString());
		System.out.println(g.getVertexCount());
		Collection<Integer> nei = g.getSuccessors(1);
		System.out.println("Succesors of " + 1 + " are: " + (nei == null? "none" : nei.toString()));
		
		HashMap<Long, LTEdgeValue> map = new HashMap<Long, LTEdgeValue>();
		map.put(new Long(1), new LTEdgeValue(0.5f));
		map.put(new Long(2), new LTEdgeValue(0.5f));
		System.out.println("the map: " + map.toString());
		LTEdgeValue val = map.get(new Long(1));
		val.weight = 0.6f;
		System.out.println("the map: " + map.toString());

	}

}
