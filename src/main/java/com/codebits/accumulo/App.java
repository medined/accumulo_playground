package com.codebits.accumulo;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MultiTableBatchWriter;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

public class App {
    
    public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException, TableExistsException {
        System.out.println("START");

        String instanceName = "development";
        String zooKeepers = "localhost";
        String user = "root";
        byte[] pass = "password".getBytes();
        String tableName = "users";

        ZooKeeperInstance instance = new ZooKeeperInstance(instanceName, zooKeepers);
        Connector connector = instance.getConnector(user, pass);
        MultiTableBatchWriter writer = connector.createMultiTableBatchWriter(200000l, 300, 4);

        if (!connector.tableOperations().exists(tableName)) {
            connector.tableOperations().create(tableName);
        }

        BatchWriter bw = writer.getBatchWriter(tableName);

        try {
        String userId = "medined";
        int age = 48;
        int height = 70;
        Mutation m = new Mutation(new Text(userId));
        m.put(new Text("age"), new Text(""), new Value(new Integer(age).toString().getBytes()));
        m.put(new Text("height"), new Text(""), new Value(new Integer(height).toString().getBytes()));
        bw.addMutation(m);
        } finally {
        	if (writer != null) {
        		writer.close();
        	}
        }
        System.out.println("END");
    }
}
