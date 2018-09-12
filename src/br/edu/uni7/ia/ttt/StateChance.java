package br.edu.uni7.ia.ttt;

public class StateChance {

	public final float winChance;
	public final float drawChance;
	public final float lossChance;

	public StateChance(float winChance, float drawChance, float lossChance) {
		this.winChance = winChance;
		this.drawChance = drawChance;
		this.lossChance = lossChance;
	}
}