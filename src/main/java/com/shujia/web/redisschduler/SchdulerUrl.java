package com.shujia.web.redisschduler;

import com.shujia.web.bean.Sentiment;
import com.shujia.web.util.JDBCUtil;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SchdulerUrl {
    /**
     * 调度url,没5分钟执行一次
     */
    public static void main(String[] args) throws InterruptedException, SQLException {

        System.out.println("4364031345034407".hashCode());

        while (true) {
            //创建连接
            Jedis jedis = new Jedis("node1", 6379);
            //默认连接db0  可以手动修改
            jedis.select(0);

            Connection connection = JDBCUtil.getConnection();
            ArrayList<Sentiment> sentiments = new ArrayList<>();
            String sql = "select * from tb_sentiment";
            String baseUrl = "https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D60%26q%3D$1%26t%3D0&page_type=searchall&page=$2";
            try {
                PreparedStatement stat = connection.prepareStatement(sql);
                ResultSet resultSet = stat.executeQuery();
                while (resultSet.next()) {
                    String words = resultSet.getString("words");
                    int id = resultSet.getInt("id");
                    for (String word : words.split("([ ,])")) {
                        for (int i = 1; i < 100; i++) {
                            String url = baseUrl
                                    .replace("$1", URLEncoder.encode(word, "UTF-8"))
                                    .replace("$2", String.valueOf(i));
                            //将搜索url写入redis
                            jedis.lpush("weibo_search_spider:start_urls", url);

                            //将url和舆情编号的对应关系写入数据库
                            jedis.hset("url_label", url, String.valueOf(id));
                            System.out.println(url);

                            Thread.sleep(1000);
                        }


                    }
                }
            } catch (SQLException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            connection.close();
            jedis.close();

            //5分钟轮询一次
            Thread.sleep(5 * 1000);
        }
    }
}
