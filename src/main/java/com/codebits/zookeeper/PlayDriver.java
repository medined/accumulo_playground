package com.codebits.zookeeper;

import java.util.List;

import org.apache.accumulo.server.zookeeper.ZooReaderWriter;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class PlayDriver {

	public void list(ZooKeeper zk, String path) {
		try {
			Stat stat = zk.exists(path, null);
			if (stat != null) {
				String value = new String(zk.getData(path, null, null));
				System.out.println(path + " -> " + value);
				int numChildren = stat.getNumChildren();
				if (numChildren == 0) {
				} else {
					List<String> children = zk.getChildren(path, null);
					for (String child : children) {
						String newPath = "/".equals(path) ? path + child : path + "/" + child;
						list(zk, newPath);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ZooKeeper zk = ZooReaderWriter.getInstance().getZooKeeper();
	    //String iid = HdfsZooInstance.getInstance().getInstanceID();
		PlayDriver driver = new PlayDriver();
		driver.list(zk, "/accumulo");
	}

}
