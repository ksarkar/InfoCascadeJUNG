package edu.asu.ame.infocascade.jung.hadoop.simulation;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.asu.ame.infocascade.jung.hadoop.common.CommonFilename;
import edu.asu.ame.infocascade.jung.hadoop.util.Util;

public class HadoopSim extends Configured implements Tool {
	
	public static boolean estimateOnly = true;
	private double spread = 0.0d;
	
	private void addToDistCache(JobConf conf, 
								String localFilename, 
								String hdfsFilename) throws IOException {
	    FileSystem fs = FileSystem.get(conf);
	    Path localPath = new Path(localFilename);
	    Path hdfsPath = new Path(hdfsFilename);

	    // upload the file to hdfs. Overwrite any existing copy.
	    fs.copyFromLocalFile(false, true, localPath, hdfsPath);

	    DistributedCache.addCacheFile(hdfsPath.toUri(), conf);
	}
	
	private double estimate(int numMappers, int samplesPerMap, String graphFile, String seedFile) throws IOException {
		String inDir = CommonFilename.MAPRED_IN;
		String outDir = CommonFilename.MAPRED_OUT;
		
		// Set the map-reduce job
		JobConf jobConf = Util.getMapRedJobConf(this.getClass(),
				  								this.getConf(),
				  								"hadoop-based-simulation",
				  								SequenceFileInputFormat.class,
				  								HadoopSimMapper.class,
				  								numMappers,
				  								DoubleWritable.class,
				  								DoubleWritable.class,
				  								1,
				  								HadoopSimReducer.class,
				  								WritableComparable.class,
				  								Writable.class,
				  								SequenceFileOutputFormat.class,
				  								inDir,
				  								outDir);
		
		// turn off speculative execution, because DFS doesn't handle
	    // multiple writers to the same file.
	    jobConf.setSpeculativeExecution(false);
		
		FileSystem fileSys = FileSystem.get(jobConf);
	    fileSys.delete(new Path(inDir), true);
	    if (!fileSys.mkdirs(new Path(inDir))) {
	      throw new IOException("Mkdirs failed to create " + inDir.toString());
	    }
	    
		// Write inputs
		for(int idx=0; idx < numMappers; ++idx) {
			Path file = new Path(inDir, "part-" + idx);
			SequenceFile.Writer writer = SequenceFile.createWriter(fileSys, jobConf, 
		                                                           file, IntWritable.class, 
		                                                           IntWritable.class, 
		                                                           CompressionType.NONE);
			try {
				writer.append(new IntWritable(samplesPerMap), new IntWritable(0));
			} finally {
				writer.close();
			}
	      	System.out.println("Wrote input for Map #"+idx);
		}
		
		// Add graphFile and seedFile to distributed cache
		this.addToDistCache(jobConf, graphFile, CommonFilename.GRAPH_FILE);
		this.addToDistCache(jobConf, seedFile, CommonFilename.SEED_FILE);
		
		// Run the map-reduce task
		System.out.println("Starting map-reduce Job");
	    long startTime = System.currentTimeMillis();
		JobClient.runJob(jobConf);
		System.out.println("Job Finished in "+
                (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
		
		// Read the output and return it
		double estimate = 0.0d;
		Path outFile = new Path(CommonFilename.REDUCE_OUT);
		try {
			SequenceFile.Reader reader = new SequenceFile.Reader(fileSys, outFile,
                                                       			 jobConf);
			DoubleWritable sum = new DoubleWritable();
			reader.next(sum, NullWritable.get());
			estimate = sum.get() / numMappers;
			reader.close();
		} finally {
		      fileSys.delete(outFile, true);
		}
		return estimate;
	}
	
	
	@Override
	public int run(String[] args) throws Exception {
		/*
		int numMappers = 2;
		int samplePerMap = 100;
		
		String graphFile = "./data/in/test-graph.txt";
		String seedFile = "./data/in/seed-set-test-graph.txt";
		*/
		
		String usage = new String("Usage: HadoopSim <numMapper> <samplePerMapper> <localGraphFileName> <localSeedFileName>");
		
		if (4 != args.length) {
			System.out.println(usage);
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}
		
		int numMappers = Integer.parseInt(args[0]);
		int samplePerMap = Integer.parseInt(args[1]);
		
		String graphFile = args[2];
		String seedFile = args[3];
		
		this.spread = this.estimate(numMappers, samplePerMap, graphFile, seedFile);
		
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		/*
		args = new String[4];
		args[0] = "2";
		args[1] = "100";
		
		args[2] = "./data/in/test-graph.txt";
		args[3] = "./data/in/seed-set-test-graph.txt";*/
		
		HadoopSim hadoopSim = new HadoopSim();
		int res = ToolRunner.run(new Configuration(), hadoopSim, args);
				
		System.out.println("Estimated spread: " + hadoopSim.spread);
		
		System.exit(res);
	}	
}
