package com.codebits;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class RollingMeanMathPlay {

	public static void main(String[] args) {
		Random random = new Random();
		DescriptiveStatistics stats = new DescriptiveStatistics();
		stats.setWindowSize(7);

		for (int i = 0; i < 100; i++) {
			int n = random.nextInt(100);
			stats.addValue(n);
            System.out.println("n: " + n + " mean: " + stats.getMean());
		}
	}

}
