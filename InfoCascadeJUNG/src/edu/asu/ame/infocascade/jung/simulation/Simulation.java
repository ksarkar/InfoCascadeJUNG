package edu.asu.ame.infocascade.jung.simulation;

import java.util.HashSet;

import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.model.Model;
import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;
import edu.uci.ics.jung.graph.Graph;

public class Simulation {
	private Model model;
	private int numRun;
	
	public Simulation(Model model, int numRun) {
		super();
		this.model = model;
		this.numRun = numRun;
	}

	public Simulation(Model model) {
		this(model, 1);
	}

	public double run(HashSet<String> seedSet, int numRun) {
		if (numRun != 0) {
			this.numRun = numRun;
		}
		
		long sum = 0;
		for (int i = 0; i < this.numRun; i++) {
			sum = sum + model.runCascade(seedSet)[0];
		}
		
		return (double) sum / this.numRun;
	}
	
	public double run(HashSet<String> seedSet) {
		return this.run(seedSet, 0);
	}
	
	public long[] runStat(HashSet<String> seedSet, int numRun) {
		if (numRun != 0) {
			this.numRun = numRun;
		}
		
		long[] results = new long[this.numRun];
		for (int i = 0; i < this.numRun; i++) {
			results[i] = model.runCascade(seedSet)[0];
		}
		
		return results;		
	}

	public static void main(String[] args) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/in/CA-AstroPh.txt");
		//Graph<String, Long> g = reader.load("data/test-graph.txt");
		
		HashSet<String> seeds = new HashSet<String>();
		seeds.add("1086");
		seeds.add("21718");
		seeds.add("35290");
		seeds.add("53213");
		seeds.add("111161");
		
		
		UniformWeightLTModel model = new UniformWeightLTModel();
		model.createModel(g);
		Simulation sim = new Simulation(model);
		
		long startTime = System.currentTimeMillis();
		double mean = sim.run(seeds, 1000);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		System.out.println("Spread = " + mean);
		
		/*
		startTime = System.currentTimeMillis();
		long[] result = sim.runStat(seeds, 5);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		System.out.println("Spread statistics:");
		System.out.print(result[0]);
		for (int i = 1; i < result.length; i++) {
			System.out.print(", " + result[i]);
		}*/
	}

}
