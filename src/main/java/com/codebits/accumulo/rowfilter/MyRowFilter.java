package com.codebits.accumulo.rowfilter;

import java.io.IOException;
import java.util.HashSet;

import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.apache.accumulo.core.iterators.user.RowFilter;
import org.apache.log4j.Logger;

public class MyRowFilter extends RowFilter {

	private static final Logger log = Logger.getLogger(MyRowFilter.class);

	@Override
	public boolean acceptRow(SortedKeyValueIterator<Key, Value> rowIterator)
			throws IOException {
		int sum = 0;
		int sum2 = 0;

		Key firstKey = null;

		if (rowIterator.hasTop()) {
			firstKey = new Key(rowIterator.getTopKey());
		}

		log.info("firstkey:" + firstKey);
		
		while (rowIterator.hasTop()) {
			sum += Integer.parseInt(rowIterator.getTopValue().toString());
			rowIterator.next();
			log.info("sum: " + sum);
		}

		// ensure that seeks are confined to the row
		rowIterator.seek(new Range(), new HashSet<ByteSequence>(), false);
		while (rowIterator.hasTop()) {
			sum2 += Integer.parseInt(rowIterator.getTopValue().toString());
			rowIterator.next();
			log.info("sum2: " + sum2);
		}

		rowIterator.seek(new Range(firstKey.getRow(), false, null, true),
				new HashSet<ByteSequence>(), false);
		while (rowIterator.hasTop()) {
			sum2 += Integer.parseInt(rowIterator.getTopValue().toString());
			rowIterator.next();
		}

		return sum == 2 && sum2 == 2;
	}

}
