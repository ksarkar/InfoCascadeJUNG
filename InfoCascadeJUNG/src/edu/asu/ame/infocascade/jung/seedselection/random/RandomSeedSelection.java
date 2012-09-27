package edu.asu.ame.infocascade.jung.seedselection.random;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.model.Model;
import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;
import edu.asu.ame.infocascade.jung.seedselection.OptResult;
import edu.asu.ame.infocascade.jung.seedselection.SeedSelectionAlgo;
import edu.asu.ame.infocascade.jung.simulation.Simulation;
import edu.uci.ics.jung.graph.Graph;

public class RandomSeedSelection implements SeedSelectionAlgo {
	private Model model;
	private Simulation simulation;
	private Random random;

	public RandomSeedSelection(Model model) {
		super();
		this.model = model;
		this.simulation = new Simulation(model);
		this.random = new Random(System.currentTimeMillis());
	}

	@Override
	public OptResult optimize(int numSeeds, int numRuns) {
		HashSet<String> seedSet = this.getRandomSeedSet(numSeeds);
		return new OptResult(seedSet, this.simulation.run(seedSet, numRuns));
	}
	
	public HashSet<String> getRandomSeedSet(int numSeeds) {
		Collection<String>	vertices = model.getVertices();
		int numVertices = vertices.size();
		Set<Integer> randomNodes = new HashSet<Integer>();
		
		for (int i = 0; i < numSeeds; i++) {
			randomNodes.add(this.random.nextInt(numVertices));
		}
		
		PriorityQueue<Integer> randomNodespq = new PriorityQueue<Integer>(randomNodes);
		Iterator<String> nodeItr = vertices.iterator();
		HashSet<String> seedSet = new HashSet<String>();
		
		int i = 0;
		while ((randomNodespq.size() != 0) && nodeItr.hasNext()) {
			String nextNode = nodeItr.next();
			int next = randomNodespq.peek();
			if (i == next) {
				seedSet.add(nextNode);
				randomNodespq.poll();
			}
			i++;
		}
		return seedSet;
	}
	
	public static void main(String[] args) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/CA-AstroPh.txt");
		//Graph<String, Long> g = reader.load("data/test-graph.txt");
		UniformWeightLTModel model = new UniformWeightLTModel();
		model.createModel(g);
		SeedSelectionAlgo random = new RandomSeedSelection(model);
		
		int numSeeds = 10000;
		int numRuns = 1;
		
		long startTime = System.currentTimeMillis();
		OptResult result = random.optimize(numSeeds, numRuns);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		System.out.println("Result of the simulation:\nSpread: " + result.spread);
		System.out.println("SeedSet: ");
		for (String s : result.seedSet) {
			System.out.println(s);
		}
	}
}
