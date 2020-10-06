package com.hjy.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

public class WatcherNode implements Watcher{

	@Override
	public void process(WatchedEvent event) {
		if(event.getType()==EventType.NodeChildrenChanged)
		{
			System.out.println(event.getPath()+"父节点下的子节点发生了变化");
		}
		else if(event.getType() == Event.EventType.NodeDeleted) {
			
            System.out.println(event.getPath()+"监控到了该节点被删除");

        }
		
	}

}
