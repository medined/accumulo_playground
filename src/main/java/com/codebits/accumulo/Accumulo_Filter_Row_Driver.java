package com.codebits.accumulo;

import java.util.Iterator;
import java.util.Map;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

import com.codebits.accumulo.rowfilter.MyRowFilter;

public class Accumulo_Filter_Row_Driver {

	public static void main(String[] args) throws AccumuloException,
			AccumuloSecurityException, TableNotFoundException,
			TableExistsException {
		String instanceName = "instance";
		String zooKeepers = "li459-74.members.linode.com";
		String user = "root";
		byte[] pass = "secret".getBytes();
		String tableName = "demo";
		String rowId = "John";

		ZooKeeperInstance instance = new ZooKeeperInstance(instanceName,
				zooKeepers);
		Connector connector = instance.getConnector(user, pass);

	    IteratorSetting is = new IteratorSetting(40, MyRowFilter.class);
		
		Scanner scan = connector.createScanner(tableName, new Authorizations());
		scan.addScanIterator(is);
		scan.setRange(new Range(rowId, rowId));

		Iterator<Map.Entry<Key, Value>> iterator = scan.iterator();
		while (iterator.hasNext()) {
			Map.Entry<Key, Value> entry = iterator.next();
			Key key = entry.getKey();
			Value value = entry.getValue();
			System.out.println(key + " ==> " + value);
		}
	}
}