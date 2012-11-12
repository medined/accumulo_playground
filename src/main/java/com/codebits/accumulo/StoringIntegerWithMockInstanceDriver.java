package com.codebits.accumulo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;

public class StoringIntegerWithMockInstanceDriver {

	public static String toHexString(byte[] ba) {
	    StringBuilder str = new StringBuilder();
	    for(int i = 0; i < ba.length; i++)
	        str.append(String.format("%x", ba[i]));
	    return str.toString();
	}
	
	public static void main(String[] args) throws IOException, AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException {
		Instance mock = new MockInstance("development");
		Connector connector = mock.getConnector("root", "password".getBytes());
		connector.tableOperations().create("TABLEA");

		BatchWriter wr = connector.createBatchWriter("TABLEA", 10000000, 10000, 5);
		for (int i = 5; i > 0; --i) {
			byte[] key = ByteBuffer.allocate(4).putInt(i).array();
			Mutation m = new Mutation(new Text(key));
			m.put("cf", "cq", "value");
			wr.addMutation(m);
		}
		wr.close();

		Scanner scanner = connector.createScanner("TABLEA", new Authorizations());
		Iterator<Map.Entry<Key, Value>> iterator = scanner.iterator();
		while (iterator.hasNext()) {
			Map.Entry<Key, Value> entry = iterator.next();
			Key key = entry.getKey();
			System.out.println(toHexString(key.getRow().getBytes()));
		}
	}
}
