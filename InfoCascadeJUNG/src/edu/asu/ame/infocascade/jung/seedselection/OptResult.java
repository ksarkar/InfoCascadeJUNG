package edu.asu.ame.infocascade.jung.seedselection;

import java.util.Set;

public class OptResult {

	public Set<String> seedSet;
	public double spread;
	
	public OptResult(Set<String> seedSet, double spread) {
		super();
		this.seedSet = seedSet;
		this.spread = spread;
	}
	
}
