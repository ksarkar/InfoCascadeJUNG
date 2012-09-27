package edu.asu.ame.infocascade.jung.model.lt;

/**
 * Data structure for holding the single vertex related model parameters
 * like - threshold, isActive etc.
 * 
 * @author ksarkar1
 *
 */
public class LTVertexValue {

	public double threshold;
	public boolean isActive;

	public LTVertexValue(double threshold, boolean isActive) {
		super();
		this.threshold = threshold;
		this.isActive = isActive;
	}
	
	public LTVertexValue() {
		this(0.0d, false);
	}

	public String toString() {
		return "LTV[threshold=" + this.threshold + 
				",isActive=" + this.isActive + 
				"]";
	}
	
	/*
	public boolean equals(Object o) {
		if (!(o instanceof LTVertexValue)) {
			return false;
		}
		
		LTVertexValue other = (LTVertexValue)o;
		return this.threshold == other.threshold && this.isActive == other.isActive;
	}
	
	public int hashCode() {
		return new Double(this.threshold).hashCode() ^ new Boolean(this.isActive).hashCode();
				
	}
	*/

}
