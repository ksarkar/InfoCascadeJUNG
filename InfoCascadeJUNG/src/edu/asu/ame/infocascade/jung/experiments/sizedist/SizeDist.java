package edu.asu.ame.infocascade.jung.experiments.sizedist;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;

import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.model.Model;
import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;
import edu.asu.ame.infocascade.jung.model.lt.globalinfo.UWLTwithGlobalInfoModel;
import edu.asu.ame.infocascade.jung.seedselection.random.RandomSeedSelection;
import edu.uci.ics.jung.graph.Graph;

public class SizeDist {

	public void getSpreadDataPoints(Model model, 
									String outFile, 
									int numSeeds, int 
									numRuns, 
									boolean fixedSet) throws FileNotFoundException {
		
		RandomSeedSelection random = new RandomSeedSelection(model);
		HashSet<String> seedSet = null;
		seedSet = random.getRandomSeedSet(numSeeds);
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(outFile);
			out.print(model.runCascade(seedSet)[0]);
			for (int i = 0; i < numRuns - 1; i++) {
				if (!fixedSet) {
					seedSet = random.getRandomSeedSet(numSeeds);
				}
				out.print("," + model.runCascade(seedSet)[0]);
			}
			
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private static void runSpreadDataPoints(Model model, 
										 int numSeeds,
										 int numRuns,
										 boolean fixedSet) throws FileNotFoundException {
		SizeDist sizeDist = new SizeDist();
		String outData = null;
		String modelS = "";
		String fixedSetS = null;
		if (model instanceof UWLTwithGlobalInfoModel) {
			modelS = "global__";
		}
		
		fixedSetS = fixedSet? "_fixedSet_" : "_randomSet_";
		outData = "./data/out/" + modelS + "SizeDist" + fixedSetS + numSeeds + "_" + numRuns + ".csv";

		
		long startTime = System.currentTimeMillis();
		sizeDist.getSpreadDataPoints(model, outData, numSeeds, numRuns, fixedSet);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		System.out.println("Wrote file " + outData);
		
	}
	
	private static void runExpt(boolean global, int[] numSeeds, int[] numRuns) throws FileNotFoundException {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/in/CA-AstroPh.txt");
		Model model = null;
		
		for (int numSeed : numSeeds) {
			if (numSeed <= 500) {
				if (global) {
					if (numSeed <= 50) {
						model = new UWLTwithGlobalInfoModel(0.05);
						model.createModel(g);
					}
					else {
						model = new UWLTwithGlobalInfoModel(0.15);
						model.createModel(g);
					}
				}
				else {
					model = new UniformWeightLTModel();
					model.createModel(g);
				}
				runSpreadDataPoints(model, numSeed, numRuns[0], true);
				runSpreadDataPoints(model, numSeed, numRuns[0], false);
			}
			else if (numSeed <= 5000) {
				if (global) {
					if (numSeed <= 2000) {
						model = new UWLTwithGlobalInfoModel(0.25);
						model.createModel(g);
					}
					else {
						model = new UWLTwithGlobalInfoModel(0.50);
						model.createModel(g);
					}
				}
				else {
					model = new UniformWeightLTModel();
					model.createModel(g);
				}
				runSpreadDataPoints(model, numSeed, numRuns[2], true);
				runSpreadDataPoints(model, numSeed, numRuns[1], false);
			}
			else {
				if (global) {
					model = new UWLTwithGlobalInfoModel(0.75);
					model.createModel(g);
				}
				else {
					model = new UniformWeightLTModel();
					model.createModel(g);
				}
				runSpreadDataPoints(model, numSeed, numRuns[3], true);
				runSpreadDataPoints(model, numSeed, numRuns[2], false);
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {		
		//SNAPReader reader = new SNAPReader();
		//Graph<String, Long> g = reader.load("data/in/CA-AstroPh.txt");
		
		//Model model = new UniformWeightLTModel(g);
		//Model model = new UWLTwithGlobalInfoModel(g, 0.15);
		
		int[] numSeeds = {10, 50, 100, 500, 1000, 2000, 5000, 10000};
		int[] numRuns = {5000, 5000, 5000, 10000};
		
		boolean global = false;
		
		runExpt(global, numSeeds, numRuns);
	}

}
