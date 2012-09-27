package edu.asu.ame.infocascade.jung.hadoop.simulation;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import edu.asu.ame.infocascade.jung.hadoop.common.CommonFilename;

public class HadoopSimReducer extends MapReduceBase
	implements Reducer<DoubleWritable, DoubleWritable, WritableComparable<NullWritable>, Writable>{
	
	double sum;
	JobConf conf;
	
	@Override
    public void configure(JobConf job) {
		this.sum = 0.0d;
		this.conf = job;
    }

	@Override
	public void reduce(DoubleWritable mean, 
					   Iterator<DoubleWritable> dummy,
					   OutputCollector<WritableComparable<NullWritable>, Writable> out, 
					   Reporter rep)
			throws IOException {
		this.sum = this.sum + mean.get();
		
	}
	
	@Override
    public void close() throws IOException {
		Path outFile = new Path(CommonFilename.REDUCE_OUT);
		FileSystem fileSys = FileSystem.get(this.conf);
		SequenceFile.Writer writer = SequenceFile.createWriter(fileSys, conf, 
                                                             outFile, DoubleWritable.class, NullWritable.class, 
                                                             CompressionType.NONE);
		writer.append(new DoubleWritable(this.sum), NullWritable.get());
		writer.close();
    }
	
	
}
