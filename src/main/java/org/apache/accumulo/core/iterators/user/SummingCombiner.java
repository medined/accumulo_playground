package org.apache.accumulo.core.iterators.user;

import java.util.Iterator;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.iterators.LongCombiner;
import org.apache.accumulo.core.iterators.OptionDescriber.IteratorOptions;

public class SummingCombiner extends LongCombiner {

	@Override
	public Long typedReduce(Key key, Iterator<Long> iter) {
		System.out.println("\nSummingCombiner; K: " + key);
		long sum = 0;
		while (iter.hasNext()) {
			long n = iter.next();
			System.out.println("SummingCombiner; n: " + n);
			System.out.println("SummingCombiner; sum1: " + sum);
			sum = safeAdd(sum, n);
			System.out.println("SummingCombiner; sum2: " + sum);
		}
		return sum;
	}

	@Override
	public IteratorOptions describeOptions() {
		IteratorOptions io = super.describeOptions();
		io.setName("sum");
		io.setDescription("SummingCombiner interprets Values as Longs and adds them together.  A variety of encodings (variable length, fixed length, or string) are available");
		return io;
	}
}
