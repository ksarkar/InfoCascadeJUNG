package edu.asu.ame.infocascade.jung.seedselection.lazygreedy;

class NodeGain implements Comparable<NodeGain> {
	
	private String nodeId;
	private double nodeGain;
	private boolean isValid;
	

	public NodeGain(String nodeId, double nodeGain, boolean isValid) {
		super();
		this.nodeId = nodeId;
		this.nodeGain = nodeGain;
		this.isValid = isValid;
	}

	public String getNodeId() {
		return nodeId;
	}

	public double getNodeGain() {
		return nodeGain;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setNodeGain(double nodeGain) {
		this.nodeGain = nodeGain;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * Greater node gain value is smaller in the ordering
	 */
	
	@Override
	public int compareTo(NodeGain o) {
		return -(Double.compare(this.getNodeGain(), o.getNodeGain()));
	}
	
}
