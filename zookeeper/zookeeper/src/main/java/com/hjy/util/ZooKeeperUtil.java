package com.hjy.util;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperUtil {
	/**
		 * 创建zookeeper客户端，连接到zookeeper服务器端
		 * 
		 * @param address
		 * @return
		 * @throws IOException
		 */
		public static ZooKeeper getZookeeperConn(String address)  {
			String connectString = address;
			int sessionTimeout = 3000; // 会话超时时间
			ZooKeeper zooKeeper=null;
			try {
				zooKeeper = new ZooKeeper(connectString, sessionTimeout,new WatcherNode());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("zookeeper connection success!");
			
			return zooKeeper;
		}
}

