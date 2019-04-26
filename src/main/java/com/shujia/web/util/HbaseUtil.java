package com.shujia.web.util;

import com.shujia.web.bean.Real;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;

import java.io.IOException;

public class HbaseUtil {
    static HConnection connection = null;

    static {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "node1:2181,node2:2181,node3:2181");
        Real real = new Real();
        try {
            //丽娜姐regionserver,  负责表的增删改查
            connection = HConnectionManager.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HConnection getConnection() {
        return connection;
    }
}
