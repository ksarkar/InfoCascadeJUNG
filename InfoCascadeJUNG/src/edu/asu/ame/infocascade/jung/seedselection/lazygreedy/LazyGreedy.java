package edu.asu.ame.infocascade.jung.seedselection.lazygreedy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;

import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.model.Model;
import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;
import edu.asu.ame.infocascade.jung.seedselection.OptResult;
import edu.asu.ame.infocascade.jung.seedselection.SeedSelectionAlgo;
import edu.asu.ame.infocascade.jung.seedselection.greedy.Greedy;
import edu.uci.ics.jung.graph.Graph;

public class LazyGreedy extends Greedy {

	public LazyGreedy(Model model) {
		super(model);
	}
	
	
	
	@Override
	public OptResult optimize(int numSeeds, int numRuns) {
		HashSet<String> seedSet = new HashSet<String>();
		ArrayList<String> seedList = new ArrayList<String>(numSeeds);
		HashSet<String> currentSeedSet = null;
		
		Collection<String>	vertices = model.getVertices();
		
		// create priority queue of all nodes, with marginal gain delta +inf
		PriorityQueue<NodeGain> pq = this.createPriorityQueueofNodeGain(vertices);
		
		double maxSpread = 0.0d;
		NodeGain max = null;
		double delta = 0.0d;
		
		for (int i = 0; i < numSeeds; i++) {
			this.setNodeGainsInvalid(pq);
			while(true) {
				max = pq.poll();
				if (max.isValid() == true) {
					seedSet.add(max.getNodeId());
					super.appendToSeedList(max.getNodeId(), seedList, i);
					maxSpread = maxSpread + max.getNodeGain();
					break;
				} 
				else {
					super.appendToSeedList(max.getNodeId(), seedList, i);
					currentSeedSet = new HashSet<String>(seedList);
					delta = super.simulation.run(currentSeedSet, numRuns) - maxSpread;
					max.setNodeGain(delta);
					max.setValid(true);
					pq.add(max);
				}
			}
		}
		return new OptResult(seedSet, maxSpread);
	}
	
	private void setNodeGainsInvalid(PriorityQueue<NodeGain> pq) {
		for (NodeGain ng : pq) {
			ng.setValid(false);
		}	
	}

	private PriorityQueue<NodeGain> createPriorityQueueofNodeGain(
			Collection<String> vertices) {
		
		PriorityQueue<NodeGain> pq = new PriorityQueue<NodeGain>(); 
		
		for (String vertex : vertices) {
			pq.add(new NodeGain(vertex, Double.NaN, false));
		}
		return pq;
	}

	public static void main(String[] args) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/CA-AstroPh.txt");
		//Graph<String, Long> g = reader.load("data/test-graph.txt");
		UniformWeightLTModel model = new UniformWeightLTModel();
		model.createModel(g);
		SeedSelectionAlgo greedy = new LazyGreedy(model);
		
		int numSeeds = 1;
		int numRuns = 5;
		
		long startTime = System.currentTimeMillis();
		OptResult result = greedy.optimize(numSeeds, numRuns);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		System.out.println("Result of the simulation:\nSpread: " + result.spread);
		System.out.println("SeedSet: ");
		for (String s : result.seedSet) {
			System.out.println(s);
		}
	}

}
