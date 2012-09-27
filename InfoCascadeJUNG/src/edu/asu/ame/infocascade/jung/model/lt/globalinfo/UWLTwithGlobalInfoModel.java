package edu.asu.ame.infocascade.jung.model.lt.globalinfo;

import java.util.HashSet;

import edu.asu.ame.infocascade.jung.model.lt.UniformWeightLTModel;

public class UWLTwithGlobalInfoModel extends UniformWeightLTModel {
	double criticalPoint;
	
	public UWLTwithGlobalInfoModel(double criticalPoint) {
		this.criticalPoint = criticalPoint;
	}
	
	@Override
	protected boolean isThresholdChanged(HashSet<String> activeSet){
		double activeProportion = (double) activeSet.size() / super.g.getVertexCount();
		return ((activeProportion >= this.criticalPoint)? true : false);
	}
	
	@Override
	protected double newThreshold(double threshold) {
		return threshold / 2;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
