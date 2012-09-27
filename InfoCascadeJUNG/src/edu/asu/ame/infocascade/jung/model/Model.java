package edu.asu.ame.infocascade.jung.model;

import java.util.Collection;
import java.util.HashSet;

import edu.uci.ics.jung.graph.Graph;

/**
 * This is the main interface file for a model
 * @author ksarkar1
 *
 */
public interface Model {
	
	public void createModel(Graph<String, Long> g);
	public long[] runCascade(HashSet<String> seedSet);
	public Collection<String> getVertices();
}
