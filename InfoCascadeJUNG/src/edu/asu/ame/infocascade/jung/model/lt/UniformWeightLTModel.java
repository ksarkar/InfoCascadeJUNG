package edu.asu.ame.infocascade.jung.model.lt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.simulation.Simulation;
import edu.uci.ics.jung.graph.Graph;

/**
 * Model class for Linear Threshold model with equal (uniform) edge 
 * weights on each outgoing edge
 * 
 * @author ksarkar1
 *
 */

public class UniformWeightLTModel extends AbstractLTModel {
	/**
	 * Random number generator
	 */
	protected Random random = new Random(System.currentTimeMillis());
	
	/**
	 * Map structures for storing model related parameters
	 */
	protected HashMap<String, LTVertexValue> vertexMap;
	private HashMap<Long, LTEdgeValue> edgeMap;
	
	/**
	 * Data structure for storing the set of seeds during a simulation.
	 * Each cascade makes a cloned copy of this for their own use.
	 */
	//private HashSet<String> seedSet = null;
	
	public UniformWeightLTModel() {
		super();
	}
	
	@Override
	protected void createNewVertexMap() {
		this.vertexMap = new HashMap<String, LTVertexValue>();		
	}

	@Override
	protected void createNewEdgeMap() {
		this.edgeMap = new HashMap<Long, LTEdgeValue>();
	}

	@Override
	protected void createVertexValue(String v) {
		this.vertexMap.put(v, new LTVertexValue());
	}

	@Override
	protected void createEdgeValues(Iterator<Long> inEdgesItr, int numPred) {
		float weight = (float) 1 / numPred;
		while (inEdgesItr.hasNext()) {
			this.edgeMap.put(inEdgesItr.next(), new LTEdgeValue(weight));
		}
	}

	/**
	 * @return the set of vertices of the graph stored by this model.
	 */
	public Collection<String> getVertices() {
		return this.g.getVertices();
	}
	
	@Override
	protected void updateMetric(HashSet<String> activeSet) {
	}
	
	@Override
	protected void initModelVariables(){
	}
	
	@Override 
	protected void initCascade(HashSet<String> seedSet) {
		this.initCascadeVariables();
		
		Iterator<String> vItr = this.g.getVertices().iterator();
		String v;
		while(vItr.hasNext()) {
			v = vItr.next();
			LTVertexValue val = this.vertexMap.get(v);
			val.threshold = this.random.nextDouble();
			if (seedSet.contains(v)) {
				val.isActive = true;
			}
			else {
				val.isActive = false;
			}
		}
	}
	
	protected void initCascadeVariables(){
	}
	
	@Override
	protected boolean isThresholdChanged(HashSet<String> activeSet) {
		return false;
	}

	@Override
	protected double getThreshold(String vertexId) {
		return this.vertexMap.get(vertexId).threshold;
	}

	@Override
	protected boolean isActive(String vertexId) {
		return this.vertexMap.get(vertexId).isActive;
	}

	@Override
	protected double newThreshold(double threshold) {
		return threshold;
	}

	@Override
	protected void setActive(String vertexId) {
		this.vertexMap.get(vertexId).isActive = true;
	}
	
	protected boolean isActivated(String vertex, double threshold) {
		Iterator<Long> inEdgeItr = this.g.getInEdges(vertex).iterator(); 
		
		double weightSum = 0;
		Long inEdge;
		String inNeighbor;
		while(inEdgeItr.hasNext()) {
			inEdge = inEdgeItr.next();
			inNeighbor = this.g.getSource(inEdge);
			if (this.vertexMap.get(inNeighbor).isActive) {
				weightSum = weightSum + this.edgeMap.get(inEdge).weight;
			}
		}
		
		return ((weightSum >= threshold) ? true : false);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/in/test-graph.txt");
		
		HashSet<String> seeds = new HashSet<String>();
		
		seeds.add("1");
		seeds.add("3");
		
		/*
		seeds.add("1086");
		seeds.add("21718");
		seeds.add("35290");
		seeds.add("53213");
		seeds.add("111161");
		*/
		long startTime = System.currentTimeMillis();
		UniformWeightLTModel model = new UniformWeightLTModel();
		model.createModel(g);
		long[] result1 = model.runCascade(seeds);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		/*
		startTime = System.currentTimeMillis();
		long[] result2 = model.runCascade(seeds);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		*/
		
		System.out.println("Spread = " + result1[0] + ",Timestep = " + result1[1]);
		//System.out.println("Spread = " + result2[0] + ",Timestep = " + result2[1]);
		
		Simulation sim = new Simulation(model);
		
		startTime = System.currentTimeMillis();
		double mean = sim.run(seeds, 1000);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		System.out.println("Spread = " + mean);
	}

}
