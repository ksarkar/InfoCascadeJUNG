package edu.asu.ame.infocascade.jung.model.lt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.asu.ame.infocascade.jung.model.Model;
import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractLTModel implements Model {
	
	/**
	 *  Graph structure of the network 
	 */
	protected Graph<String, Long> g = null;
	
	/**
	 * Data structure for storing the set of active nodes during a simulation.
	 * Each cascade reinitializes the variable.
	 */
	//protected HashSet<String> activeSet = null;
	
	/**
	 * Number of rounds to run the cascade
	 */
	protected long numRounds = 1;
	
	/**
	 * Takes the input graph read by the input reader module and
	 * initializes the model parameters.
	 * 
	 * @param g The graph returned by the input reader module
	 */
	public void createModel(Graph<String, Long> g) {
		this.g = g;
		
		this.createNewVertexMap();
		this.createNewEdgeMap();
		
		Iterator<String> vItr = this.g.getVertices().iterator();
		while(vItr.hasNext()) {
			String v = vItr.next();
			this.createVertexValue(v);
			Iterator<Long> inEdgesItr = new ArrayList<Long>(this.g.getInEdges(v)).iterator();
			int numPred = this.g.getPredecessorCount(v);
			this.createEdgeValues(inEdgesItr, numPred);
		} 
		
		this.initModelVariables();
	}
	
	abstract protected void createNewVertexMap();
	abstract protected void createNewEdgeMap();
	abstract protected void createVertexValue(String v);
	abstract protected void createEdgeValues(Iterator<Long> inEdgesItr, int numPred);
	abstract protected void initModelVariables();
	
	/**
	 * It runs the cascade and returns the final spread and number of time
	 * steps involved. The initial seeds must be marked by the initSimulation()
	 * method.
	 * 
	 * @return
	 */
	public long[] runCascade(HashSet<String> seedSet) {
		int initialSize = seedSet.size();
		
		this.initCascade(seedSet);
		
		int roundActiveCount = 0;
		long runningActiveCount = 0;
		long rounds = 1;
		HashSet<String> activeSet = (HashSet<String>) seedSet.clone();
		while(true) {
			this.updateMetric(activeSet);
			roundActiveCount = this.runTimeStep(activeSet);
			if (roundActiveCount > 0) {
				runningActiveCount = runningActiveCount + roundActiveCount;
				rounds++;
			}
			else {
				break;
			}
		}
		
		long[] result = new long[2];
		result[0] = runningActiveCount + initialSize;
		result[1] = rounds;	
		this.numRounds = rounds;
		return result;
	}
	
	abstract protected void initCascade(HashSet<String> seedSet);
	abstract protected void updateMetric(HashSet<String> activeSet);
	
	/**
	 * Runs a single time step of the cascade. If any node is activated in this time step then 
	 * the node is added to <code>activeSet</code>, so that it is included in the <code>activeSet</code>
	 * of the next time step.
	 * 
	 * @param activeSet The set of vertices active at the beginning of this time step. It is modified by the method
	 * @return total number of vertices activated in this time step
	 */
	protected int runTimeStep(HashSet<String> activeSet) {
		//System.out.println("ActiveSet: " + activeSet.toString());

		boolean isThresholdChanged = this.isThresholdChanged(activeSet);
		
		// first check who all can be activated in this time step
		HashSet<String> candidates = new HashSet<String>();
		Iterator<String> actItr = activeSet.iterator();
		String active;
		Collection<String> succ;
		while(actItr.hasNext()) {
			active = actItr.next();
			succ = this.g.getSuccessors(active);
			candidates.addAll(succ);
		}
		
		//System.out.println("CandidateSet: " + candidates.toString());
		
		// check if any of the candidates turn active in the current time step
		int roundActiveCount = 0;
		Iterator<String> cItr = candidates.iterator();
		String c;
		double threshold;
		while(cItr.hasNext()) {
			c = cItr.next();
			threshold = this.getThreshold(c);
			if (!this.isActive(c)) {
				if (isActivated(c, 
								isThresholdChanged? this.newThreshold(threshold) : threshold)) {
					roundActiveCount++;
					activeSet.add(c);
					this.setActive(c);
				}
			}	
		}
		return roundActiveCount;
	}
	
	abstract protected boolean isThresholdChanged(HashSet<String> activeSet);
	abstract protected double getThreshold(String vertexId);
	abstract protected boolean isActive(String vertexId);
	abstract boolean isActivated(String vertexId, double threshold);
	abstract protected double newThreshold(double threshold);
	abstract protected void setActive(String vertexId);
}
