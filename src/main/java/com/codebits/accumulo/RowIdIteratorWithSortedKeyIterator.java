package com.codebits.accumulo;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.SortedKeyIterator;

public class RowIdIteratorWithSortedKeyIterator implements Iterator<String>, Iterable<String> {

	Scanner scanner = null;
	Iterator<Entry<Key, Value>> iterator = null;

	public RowIdIteratorWithSortedKeyIterator(Scanner scanner) {
		super();
		this.scanner = scanner;
	    IteratorSetting cfg = new IteratorSetting(Integer.MAX_VALUE, "SKI98", SortedKeyIterator.class);
	    scanner.addScanIterator(cfg);
		this.iterator = scanner.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String next() {
		Entry<Key, Value> entry = iterator.next();
		return entry.getKey().getRow().toString();
	}

	@Override
	public void remove() {
	}

	@Override
	public Iterator<String> iterator() {
		return new RowIdIteratorWithSortedKeyIterator(scanner);
	}
}
