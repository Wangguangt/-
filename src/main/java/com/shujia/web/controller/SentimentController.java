package com.shujia.web.controller;

import com.shujia.web.bean.Real;
import com.shujia.web.bean.Sentiment;
import com.shujia.web.bean.Word;
import com.shujia.web.util.HbaseUtil;
import com.shujia.web.util.JDBCUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Hash;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class SentimentController {

    /**
     * 增加舆情
     */
    @RequestMapping("/addSentiment")
    public RedirectView addSentiment(Sentiment sentiment) {
        System.out.println(sentiment);

        Connection connection = JDBCUtil.getConnection();

        String sql = "insert into tb_sentiment(name,words,date) values(?,?,?)";
        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setString(1, sentiment.getName());
            stat.setString(2, sentiment.getWords());
            stat.setDate(3, new Date(System.currentTimeMillis()));
            stat.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //返回主页面
        return new RedirectView("/");

    }


    /**
     * 获取舆情列表
     */
    @RequestMapping("/getSentimentList")
    public ArrayList<Sentiment> getSentimentList() {

        Connection connection = JDBCUtil.getConnection();

        ArrayList<Sentiment> sentiments = new ArrayList<>();

        String sql = "select * from tb_sentiment";

        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            ResultSet resultSet = stat.executeQuery();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String words = resultSet.getString("words");
                Date date = resultSet.getDate("date");

                String a_id = "<a href=\"sentimentinfo/" + id + "\">" + id + "</a>";

                Sentiment sentiment = new Sentiment(a_id, name, words, date);

                sentiments.add(sentiment);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //hi姐返回一个数组，spring  会自动将集合转换成json字符串
        return sentiments;

    }

    /**
     * 获取舆情走势
     */
    @RequestMapping("/getRealTimeSentiment")
    public Real getRealTimeSentiment(String id) {

        HConnection connection = HbaseUtil.getConnection();
        Real real = new Real();
        try {
            HTableInterface table = connection.getTable("sentiment_realtime");

            Get get = new Get(id.getBytes());
            get.addFamily("info".getBytes());
            //如果不指定版本号，默认只查询一个版本的数据
            get.setMaxVersions(100);


            ArrayList<String> xlist = new ArrayList<>();
            ArrayList<Integer> y1list = new ArrayList<Integer>();
            ArrayList<Integer> y2list = new ArrayList<Integer>();
            ArrayList<Integer> y3list = new ArrayList<Integer>();

            real.setX(xlist);
            real.setY1(y1list);
            real.setY2(y2list);
            real.setY3(y3list);

            Result result = table.get(get);
            List<Cell> columnCells = result.getColumnCells("info".getBytes(), "real".getBytes());

            for (Cell columnCell : columnCells) {
                long timestamp = columnCell.getTimestamp();
                Date date = new Date(timestamp);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                String x = format.format(date);
                xlist.add(x);

                String value = Bytes.toString(CellUtil.cloneValue(columnCell));

                HashMap<String, Integer> map = new HashMap<>();
                map.put("0", 0);
                map.put("1", 0);
                map.put("2", 0);

                for (String kv : value.split("\\|")) {
                    String k = kv.split(":")[0];
                    Integer v = Integer.parseInt(kv.split(":")[1]);
                    map.put(k, v);
                }
                y1list.add(map.get("0"));
                y2list.add(map.get("1"));
                y3list.add(map.get("2"));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return real;

    }


    /**
     * 舆情详细页面
     */
    @RequestMapping(value = "/sentimentinfo/{id}", method = RequestMethod.GET)
    public ModelAndView page(@PathVariable("id") String id) {


        Connection connection = JDBCUtil.getConnection();

        ArrayList<Sentiment> sentiments = new ArrayList<>();

        String sql = "select * from tb_sentiment where id=?";
        Sentiment sentiment = new Sentiment();

        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setString(1, id);

            ResultSet resultSet = stat.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String words = resultSet.getString("words");
                Date date = resultSet.getDate("date");
                sentiment.setId(id);
                sentiment.setName(name);
                sentiment.setWords(words);
                sentiment.setDate(date);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ModelAndView sentimentinfo = new ModelAndView("sentimentinfo");

        //设置参数 在页面通过过  ${sentiment.name }
        sentimentinfo.addObject("sentiment", sentiment);

        return sentimentinfo;
    }

    /**
     * 舆情词云图
     */
    @RequestMapping(value = "/getWordCloud/{id}", method = RequestMethod.GET)
    public List<Word> getWordCloud(@PathVariable("id") String id) {

        ArrayList<Word> words = new ArrayList<>();
        //创建连接
        Jedis jedis = new Jedis("node1", 6379);
        //默认连接db0  可以手动修改
        jedis.select(0);

        Map<String, String> map = jedis.hgetAll(id);
        ;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String word = entry.getKey();
            Integer value = Integer.parseInt(entry.getValue());
            words.add(new Word(word, value));

        }
        return words;
    }
}
