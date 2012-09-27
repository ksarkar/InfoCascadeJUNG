package edu.asu.ame.infocascade.jung.hadoop.simulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import edu.asu.ame.infocascade.jung.graphreader.SNAPReader;
import edu.asu.ame.infocascade.jung.hadoop.common.CommonFilename;
import edu.asu.ame.infocascade.jung.model.Model;
import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;
import edu.asu.ame.infocascade.jung.simulation.Simulation;
import edu.uci.ics.jung.graph.Graph;

public class HadoopSimMapper extends MapReduceBase
	implements Mapper<IntWritable, Writable, DoubleWritable, DoubleWritable>  {

	Model model;
	HashSet<String> seedSet;
	
	@Override
	public void configure(JobConf conf) {
		try {
			String graphCacheName = new Path(CommonFilename.GRAPH_FILE).getName();
			String seedCacheName = new Path(CommonFilename.SEED_FILE).getName();
			Path [] cacheFiles = DistributedCache.getLocalCacheFiles(conf);
			if (null != cacheFiles && cacheFiles.length > 0) {
				boolean isGraphRead = false;
				boolean isSeedRead = false;
		        for (Path cachePath : cacheFiles) {
		        	if (cachePath.getName().equals(graphCacheName)) {
		        		this.initiateModel(cachePath.toString());
		        		isGraphRead = true;
		        	}
		        	if (cachePath.getName().equals(seedCacheName)) {
		        		this.loadSeedList(cachePath.toString());
		        		isSeedRead = true;
		        	}
		        		
		        	if (isGraphRead && isSeedRead) {
		        		break;
		        	}
		        }
			}
	    } catch (IOException ioe) {
	    	System.err.println("IOException reading from distributed cache");
		    System.err.println(ioe.toString());
	    }
	}
	
	void initiateModel(String graphFileName) {
		SNAPReader reader = new SNAPReader();
		Graph<String, Long> g = reader.load(graphFileName);
		this.model = new UniformWeightLTModel();
		model.createModel(g);
	}
	
	void loadSeedList(String SeedFileName) throws IOException {
	    // note use of regular java.io methods here - this is a local file now
	    BufferedReader wordReader = new BufferedReader(
	        new FileReader(SeedFileName));
	    try {
	      String line;
	      this.seedSet = new HashSet<String>();
	      while ((line = wordReader.readLine()) != null) {
	        this.seedSet.add(line);
	      }
	    } finally {
	      wordReader.close();
	    }
	  }
	
	@Override
	public void map(IntWritable key, 
					Writable dummy,
					OutputCollector<DoubleWritable, DoubleWritable> out, 
					Reporter rep)
			throws IOException {
		
		int numRuns = key.get();
		if (HadoopSim.estimateOnly) {
			Simulation sim = new Simulation(this.model);
			double mean = sim.run(this.seedSet, numRuns);
			out.collect(new DoubleWritable(mean), new DoubleWritable(0.0d));
		}
		else {
			long[] result;
			for (int i = 0; i < numRuns; i++) {
				result = this.model.runCascade(this.seedSet);
				out.collect(new DoubleWritable(result[0]), new DoubleWritable(result[1]));
			}
		}
	}

}
