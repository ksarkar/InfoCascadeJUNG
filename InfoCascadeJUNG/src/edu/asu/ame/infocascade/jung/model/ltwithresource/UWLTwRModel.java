package edu.asu.ame.infocascade.jung.model.ltwithresource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.model.lt.LTVertexValue;
import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;
import edu.uci.ics.jung.graph.Graph;


public class UWLTwRModel extends UniformWeightLTModel {
	
	/**
	 * Total available resource of the network (per round)
	 */
	double totalResource = 0.0d;
	
	/**
	 * Running sum of utilized resources in a cascade
	 */
	double utilResource = 0.0d;


	public UWLTwRModel() {
		super();
	}

	@Override
	protected void createVertexValue(String v) {
		this.vertexMap.put(v, new LTwRVertexValue(super.random.nextDouble()));
	}
	
	@Override
	protected void initModelVariables(){
		Iterator<Entry<String, LTVertexValue>> entries = super.vertexMap.entrySet().iterator(); 
		this.totalResource = 0.0d;
		while (entries.hasNext()){
			this.totalResource = this.totalResource 
							+ ((LTwRVertexValue)entries.next().getValue()).resource;
		}
	}
	
	@Override
	protected void initCascadeVariables(){
		this.utilResource = 0.0d;
	}

	@Override
	protected void updateMetric(HashSet<String> activeSet) {
		Iterator<String> actItr = activeSet.iterator();
		while(actItr.hasNext()) {
			this.utilResource = this.utilResource 
							+ ((LTwRVertexValue)super.vertexMap.get(actItr.next())).resource;
		}
	}
	
	public double reportUtil(){
		return this.utilResource / (this.totalResource * super.numRounds);
	}
	
	public static void main(String[] args) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load("data/in/CA-AstroPh.txt");
		
		HashSet<String> seeds = new HashSet<String>();
		
		/*
		seeds.add("1");
		seeds.add("3");
		*/
		
		seeds.add("1086");
		seeds.add("21718");
		seeds.add("35290");
		seeds.add("53213");
		seeds.add("111161");
	
		long startTime = System.currentTimeMillis();
		UWLTwRModel model = new UWLTwRModel();
		model.createModel(g);
		long[] result1 = model.runCascade(seeds);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		System.out.println("Spread = " + result1[0] + ", Timestep = " + result1[1] + ", Utilization = " + model.reportUtil()*100 + "%");
	}

}
