package com.codebits.accumulo;

import java.io.IOException;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;

public class ReversingStringSortOrderWithMockInstanceDriver {

	static byte[] convert(byte[] row) {
		for (int i = 0; i < row.length; i++) {
			row[i] = (byte) (255 - row[i]);
		}
		return row;
	}

	public static void main(String[] args) throws IOException, AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException {
		Instance mock = new MockInstance("development");
		Connector connector = mock.getConnector("root", "password".getBytes());
		connector.tableOperations().create("TABLEA");

		BatchWriter wr = connector.createBatchWriter("TABLEA", 10000000, 10000, 5);
		for (int i = 5; i > 0; --i) {
			byte[] key = ("row_" + String.format("%04d", i)).getBytes();
			byte[] reverse_key = convert(key);
			Mutation m = new Mutation(new Text(reverse_key));
			m.put("cf_" + String.format("%04d", i), "cq_" + 1, "val_" + 1);
			wr.addMutation(m);
		}
		wr.close();

		Scanner scanner = connector.createScanner("TABLEA", new Authorizations());
		for (String rowId : new RowIdIterator(scanner)) {
			System.out.println("ROW ID: " + rowId);
		}
	}
}
