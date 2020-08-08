package com.pl.restty.server.provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZkClientHelper implements RegisterHelper {
	private static Logger Log = LoggerFactory.getLogger(ZkClientHelper.class);
//	ZooKeeper zk=null;
	public final static int CONNECTION_TIMEOUT= 5000;
	private static Map<String,ZooKeeper> zkMap=new HashMap<>();
	
	@Override
	public void register(String ip, String port, String path,
			String configData) throws Exception {
		// TODO Auto-generated method stub
		ZooKeeper zk = connect(ip,port);
//		int pos = path.lastIndexOf("/");
//		String zpath = path.substring(0,pos);
//		String zname = path.substring(pos);
		String node = path.endsWith("/")?path+"node_" : path+"/node_";
		mkNode(zk,node,configData);
		
	}

	

	private void mkNode(ZooKeeper zk,String path, String data) throws KeeperException, InterruptedException {
		// TODO Auto-generated method stub
		String[] pathNames = path.substring(1).split("/");
		String ps="";
		for(String p: pathNames){
			ps += "/"+p;
			if(p.equals("node_")){
					zk.create(ps, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}
			else{
				if( zk.exists(ps, false)==null )
					zk.create(ps, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				
			}
		}
	}



	private ZooKeeper connect(String ip, String port) throws IOException {
		// TODO Auto-generated method stub
		ZooKeeper zk = zkMap.get(ip+":"+port);
		if(zk!=null) return zk;
		
		zk = new ZooKeeper(ip+":"+port,5000,new Watcher(){

			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				Log.info("ZooKeeper.watcher.process type="+event.getType()+
						",path="+event.getPath());
			}
			
		});
		zkMap.put(ip+":"+port, zk);
		return zk;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
//		zk不能关闭否则，临时节点则下线
//		try {
//			zk.close();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		zk=null;
	}

//	public static void main(String[] a){
//		String path="/zk/aaa/bbb/getUser";
//		int pos = path.lastIndexOf("/");
//		String zpath = path.substring(0,pos);
//		String zname = path.substring(pos+1);
//		System.out.println(zpath+"\n"+zname);
//	}
}
