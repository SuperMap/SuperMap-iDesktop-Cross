package com.supermap.desktop.process.parameter.definedClass;

/**
 * Created by yuanR on 2017/11/29 0029.
 * 空间统计分析结果存储类
 */
public class SpatialStatisticsCollection {
	// 最近邻指数/莫兰指数
	private double Morans;
	//预期平均距离/期望值
	private double Expectation;
	//平均观测距离/方差
	private double Variance;
	// z得分
	private double ZScore;
	// p值
	private double PValue;

	public SpatialStatisticsCollection() {
	}

	public SpatialStatisticsCollection(double Morans, double Expectation, double Variance, double ZScore, double PValue) {
		this.Morans = Morans;
		this.Expectation = Expectation;
		this.Variance = Variance;
		this.ZScore = ZScore;
		this.PValue = PValue;
	}

	public double getMorans() {
		return Morans;
	}

	public void setMorans(double morans) {
		Morans = morans;
	}

	public double getExpectation() {
		return Expectation;
	}

	public void setExpectation(double expectation) {
		Expectation = expectation;
	}

	public double getVariance() {
		return Variance;
	}

	public void setVariance(double variance) {
		Variance = variance;
	}

	public double getZScore() {
		return ZScore;
	}

	public void setZScore(double ZScore) {
		this.ZScore = ZScore;
	}

	public double getPValue() {
		return PValue;
	}

	public void setPValue(double PValue) {
		this.PValue = PValue;
	}


}
