package com.hjy.config;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ZookeeperConfig implements Watcher {

	Logger log = LoggerFactory.getLogger(this.getClass());
	CountDownLatch countDown = new CountDownLatch(1);
	ZooKeeper zookeeper = null;

	@Bean(name = "zookeeper")
	public ZooKeeper getZookeeper() throws Exception {
		ZooKeeper zk = null;

		zk = new ZooKeeper("119.3.165.213:2181,119.3.165.213:2182,119.3.165.213:2183", 60000, this);
		countDown.await();

		// 可能会有并发问题
		Stat stat = zk.exists("/IS_SALE_OUT", false);
		if (stat == null) {
			zk.create("/IS_SALE_OUT", "the flag for miaosha ".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
		}
		countDown = null;
		zookeeper = zk;
		return zk;

	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getType() == EventType.None && event.getPath() == null) {
			log.info("zookeeper 连接成功");
			countDown.countDown();
		}
		// 节点内容发生变化，就删除已售完的标志
		else if (event.getType() == EventType.NodeDataChanged) {


		}

	}
}
