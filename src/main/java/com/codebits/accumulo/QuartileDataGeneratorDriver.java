package com.codebits.accumulo;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

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

public class QuartileDataGeneratorDriver {
	
	ThreadGroup rootThreadGroup = null;
	 
	ThreadGroup getRootThreadGroup( ) {
	    if ( rootThreadGroup != null )
	        return rootThreadGroup;
	    ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
	    ThreadGroup ptg;
	    while ( (ptg = tg.getParent( )) != null )
	        tg = ptg;
	    return tg;
	}
	
	Thread[] getAllThreads( ) {
	    final ThreadGroup root = getRootThreadGroup( );
	    final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
	    int nAlloc = thbean.getThreadCount( );
	    int n = 0;
	    Thread[] threads;
	    do {
	        nAlloc *= 2;
	        threads = new Thread[ nAlloc ];
	        n = root.enumerate( threads, true );
	    } while ( n == nAlloc );
	    return java.util.Arrays.copyOf( threads, n );
	}
    
	ThreadInfo getThreadInfo( final long id ) {
	    final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
	 
	    if ( !thbean.isObjectMonitorUsageSupported( ) ||
	        !thbean.isSynchronizerUsageSupported( ) )
	        return thbean.getThreadInfo( id );
	 
	    final ThreadInfo[] infos = thbean.getThreadInfo(
	        new long[] { id }, true, true );
	    if ( infos.length == 0 )
	        return null;
	    return infos[0];
	}
	
	ThreadInfo getThreadInfo( final Thread thread ) {
	    if ( thread == null )
	        throw new NullPointerException( "Null thread" );
	    return getThreadInfo( thread.getId( ) );
	}
	
	ThreadInfo getThreadInfo( final String name ) {
	    if ( name == null )
	        throw new NullPointerException( "Null name" );
	    final Thread[] threads = getAllThreads( );
	    for ( Thread thread : threads )
	        if ( thread.getName( ).equals( name ) )
	            return getThreadInfo( thread.getId( ) );
	    return null;
	}
	
	Thread getThread( final long id ) {
	    final Thread[] threads = getAllThreads( );
	    for ( Thread thread : threads )
	        if ( thread.getId( ) == id )
	            return thread;
	    return null;
	}
	
	Thread getThread( final ThreadInfo info ) {
	    if ( info == null )
	        throw new NullPointerException( "Null thread info" );
	    return getThread( info.getThreadId( ) );
	}
	
	Thread getBlockingThread( final Thread thread ) {
	    final ThreadInfo info = getThreadInfo( thread );
	    if ( info == null )
	        return null;
	    final long id = info.getLockOwnerId( );
	    if ( id == -1 )
	        return null;
	    return getThread( id );
	}
	
	public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException, TableExistsException {
        System.out.println("START");

        String instanceName = "instance";
        String zooKeepers = "li459-74.members.linode.com";
        String user = "root";
        byte[] pass = "secret".getBytes();
        String tableName = "quartiles";

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
