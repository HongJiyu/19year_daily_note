package com.hjy.test;

import java.awt.MultipleGradientPaint.CycleMethod;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.ZooDefs.Ids;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.hjy.util.ZooKeeperUtil;


/**
 * @author Administrator
 * @Description 
 * @Time 下午5:08:28
 *	zookeeper实现分布式锁，解除backlog日志，不然打印一大堆心跳日志
 */
public class ZkLock {
	/*
	 * 1.判断是否存在根节点（持久的），不存在则创建根节点。
	 * 2.在根节点下创建子节点（临时有序的节点）
	 * 3.获取所有节点，排序，判断自己的节点是否是最小的。是则获取锁（doSomeThing）。否则监听他的上一个节点。
	 * 4.获取到锁的，doSomeThing之后，释放锁，删除自身节点。
	 * 5.~~~~
	 */
	public void  lockResource(CyclicBarrier cyc) throws Exception
	{
		//获得连接
		ZooKeeper zooKeeper=ZooKeeperUtil.getZookeeperConn("119.3.165.213:2182");
		//判断是否存在节点
		Stat stat=zooKeeper.exists("/PARENT_NODE", false);
		//不存在则创建
		if(stat==null)
		{
			zooKeeper.create("/PARENT_NODE", "don't move me,work for fenbushi  locak".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		//创建完等待，等所有线程都准备就绪
		cyc.await();
//		创建子节点
		String cur=zooKeeper.create("/PARENT_NODE/CHILDREN-", "don't move me".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
		JudgeIsMin(zooKeeper, cur);
	}
	
	/**
	 * 判断当前节点是否是最小的
	 * @param zooKeeper
	 * @param cur
	 * @throws Exception
	 */
	public void JudgeIsMin(ZooKeeper zooKeeper,String cur) throws Exception
	{
		//获取永久节点下的所有节点，监听
		List<String> allList=zooKeeper.getChildren("/PARENT_NODE", true);
		int node_name_ind=cur.lastIndexOf("/");
//		该线程创建的节点的名字
		String new_cur=cur.substring(node_name_ind+1);
		//节点排序
		Collections.sort(allList);
		int ind= allList.indexOf(new_cur);
		//当前线程创建节点所在顺序
		System.out.println(cur+"的位置是"+ind);
		//如果是第一个
		if (ind==0) {
			
			System.out.println("===================="+cur+"执行一些事情");
			zooKeeper.delete(cur, 0);
		}
		//否则准备监听上一个节点
		else if(ind>0) {
			System.out.println(new_cur+"监听上一个节点"+allList.get(ind-1));
			//还没设置监听上一个节点，上一个节点就执行完然后删除掉了
			Stat oneStat=zooKeeper.exists("/PARENT_NODE/"+allList.get(ind-1), new Watcher() {
				@Override
				public void process(WatchedEvent event) {
			
					System.out.println("===================="+cur+"执行一些事情");
					try {
						zooKeeper.delete(cur, 0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (KeeperException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			//上一个节点被删除了，所以这里直接执行当前节点并删除
			if (oneStat==null) {
				System.out.println("===================="+cur+"执行一些事情");
				try {
					zooKeeper.delete(cur, 0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {

		ThreadPoolExecutor Executors=new ThreadPoolExecutor	(59, 100, 10, TimeUnit.SECONDS,new ArrayBlockingQueue(100));
			
		CyclicBarrier cyclicBarrier=new CyclicBarrier(59);
		for (int i = 0; i < 59; i++) {
			Executors.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						ZkLock zkLock=new ZkLock();
						zkLock.lockResource(cyclicBarrier);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		}

	}

}
