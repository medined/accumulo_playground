package com.codebits.accumulo;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.security.Authorizations;

public class RowIdInteratorDriver {

	public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
        System.out.println("START");

        String instanceName = "development";
        String zooKeepers = "localhost";
        String user = "root";
        byte[] pass = "password".getBytes();
        String tableName = "test_row_iterator";
        Authorizations authorizations = new Authorizations();

        ZooKeeperInstance instance = new ZooKeeperInstance(instanceName, zooKeepers);
        Connector connector = instance.getConnector(user, pass);
        Scanner scanner = connector.createScanner(tableName, authorizations);
        
        for (String rowId : new RowIdIterator(scanner)) {
        	System.out.println("ROW ID: " + rowId);
        }
	}

}
