package edu.asu.ame.infocascade.jung.hadoop.common;

public class CommonFilename {
	public static final String APP_ROOT = "/pjung";
	public static final String REDUCE_OUT = APP_ROOT + "/reduce/reduce-out";
	public static final String STAT_FILE = APP_ROOT + "/stat_file";
	
	public static final String MAPRED = APP_ROOT + "/mapred";
	public static final String MAPRED_IN = MAPRED + "/in";
	public static final String MAPRED_OUT = MAPRED + "/out";
	
	public static final String CACHE = APP_ROOT + "/cache";
	public static final String GRAPH_FILE = CACHE + "/graphFile.txt";
	public static final String SEED_FILE = CACHE + "/seedFile.txt";
}
