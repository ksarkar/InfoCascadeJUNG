package edu.asu.ame.infocascade.jung.model.ltwithresource;

import edu.asu.ame.infocascade.jung.model.lt.LTVertexValue;

/**
 * Data structure for storing node specific data 
 * @author ksarkar1
 *
 */

public class LTwRVertexValue extends LTVertexValue {
	/**
	 * Available resource at this node
	 */
	double resource;

	public LTwRVertexValue(double threshold, boolean isActive, double resource) {
		super(threshold, isActive);
		this.resource = resource;
	}

	public LTwRVertexValue(double resource) {
		super();
		this.resource = resource;
	}


	public LTwRVertexValue() {
		super();
		this.resource = 0.0d;
	}
	
	public String toString() {
		return "LTwRV[threshold=" + this.threshold + 
				",isActive=" + this.isActive +
				",resource=" + this.resource +
				"]";
	}	

}
