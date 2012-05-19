package com.codebits;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DescriptiveMathPlay {

	public static void main(String[] args) {
		Random random = new Random();
		DescriptiveStatistics stats = new DescriptiveStatistics();

		// Add the data from the array
		for (int i = 0; i < 100; i++) {
			stats.addValue(random.nextInt(100));
		}

		// Compute some statistics
		double mean = stats.getMean();
		double std = stats.getStandardDeviation();
		double median = stats.getPercentile(50);
		
		System.out.println("mean: " + mean);
		System.out.println("std: " + std);
		System.out.println("median: " + median);
	}

}
