package com.codebits;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryMathPlay {

	public static void main(String[] args) {
		Random random = new Random();
		SummaryStatistics stats = new SummaryStatistics();

		for (int i = 0; i < 100; i++) {
			stats.addValue(random.nextInt(100));
		}

		// Compute the statistics
		double mean = stats.getMean();
		double std = stats.getStandardDeviation();

		System.out.println("mean: " + mean);
		System.out.println("std: " + std);
	}

}
