package edu.asu.ame.infocascade.jung.model.lt;

public class LTEdgeValue {
	public float weight;

	public LTEdgeValue(float weight) {
		super();
		this.weight = weight;
	}

	public String toString() {
		return "LTE[weight=" + this.weight + 
				"]" ;
	}

	/*
	public boolean equals(Object o) {
		if (!(o instanceof LTEdgeValue)) {
			return false;
		}
		
		LTEdgeValue other = (LTEdgeValue)o;
		return this.weight == other.weight;	
	}
	
	public int hashCode() {
		return new Float(weight).hashCode();
	}
	*/
}
