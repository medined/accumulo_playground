package com.codebits.accumulo;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.accumulo.core.client.RowIterator;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

public class RowIdIterator implements Iterator<String>, Iterable<String> {

	Scanner scanner = null;
	RowIterator iterator = null;

	public RowIdIterator(Scanner scanner) {
		super();
		this.scanner = scanner;
		this.iterator = new RowIterator(scanner);
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String next() {
		Iterator<Entry<Key, Value>> entry = iterator.next();
		return entry.next().getKey().getRow().toString();
	}

	@Override
	public void remove() {
	}

	@Override
	public Iterator<String> iterator() {
		return new RowIdIterator(scanner);
	}
}
