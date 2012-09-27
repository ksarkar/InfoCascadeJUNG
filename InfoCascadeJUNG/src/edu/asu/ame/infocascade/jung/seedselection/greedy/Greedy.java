package edu.asu.ame.infocascade.jung.seedselection.greedy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.model.Model;
import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;
import edu.asu.ame.infocascade.jung.seedselection.OptResult;
import edu.asu.ame.infocascade.jung.seedselection.SeedSelectionAlgo;
import edu.asu.ame.infocascade.jung.simulation.Simulation;
import edu.uci.ics.jung.graph.Graph;

public class Greedy implements SeedSelectionAlgo {
	protected Model model;
	protected Simulation simulation;
	
	public Greedy(Model model) {
		super();
		this.model = model;
		this.simulation = new Simulation(model);
	}

	public OptResult optimize(int numSeeds, int numRuns) {
		
		HashSet<String> seedSet = new HashSet<String>();
		ArrayList<String> seedList = new ArrayList<String>(numSeeds);
		HashSet<String> currentSeedSet = null;
		
		Collection<String>	vertices = model.getVertices();
		
		double maxSpread = 0.0d;
		String max = null;
		double spread = 0.0d;
		for (int i = 0; i < numSeeds; i++) {	
			maxSpread = 0.0d;
			max = null;
			for (String vertex : vertices) {
				if (!seedSet.contains(vertex)) {
					this.appendToSeedList(vertex, seedList, i);
					currentSeedSet = new HashSet<String>(seedList);
					spread = this.simulation.run(currentSeedSet, numRuns);
					if (spread > maxSpread) {
						maxSpread = spread;
						max = vertex;
					}
				}
			}
			seedSet.add(max);
			this.appendToSeedList(max, seedList, i);
		}
		return new OptResult(seedSet, maxSpread);	
	}

	protected void appendToSeedList(String vertex,
								  ArrayList<String> seedList,
								  int i) {
		if (i == seedList.size()) {
			seedList.add(vertex);
		}
		else if (i == seedList.size() - 1){
			seedList.set(i, vertex);
		}
		else {
			throw new RuntimeException("Index i " + i + "is not pointing to the last position");
		}
	}

	public static void main(String[] args) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/CA-AstroPh.txt");
		//Graph<String, Long> g = reader.load("data/test-graph.txt");
		UniformWeightLTModel model = new UniformWeightLTModel();
		model.createModel(g);
		SeedSelectionAlgo greedy = new Greedy(model);
		
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
