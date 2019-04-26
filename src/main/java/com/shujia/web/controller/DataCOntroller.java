package com.shujia.web.controller;

import com.shujia.web.bean.Data;
import com.shujia.web.bean.Word;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class DataCOntroller {

    @RequestMapping("/data")
    public Data data() {
        Data data = new Data();
        data.setX("衬衫,羊毛衫,雪纺衫,裤子,高跟鞋,袜子".split(","));
        Integer[] ints = {5, 20, 40, 10, 10, 20};
        data.setY(ints);
        return data;
    }

    @RequestMapping("/word")
    public ArrayList word() {

        ArrayList<Word> list = new ArrayList<Word>();

        String words = "回\t3,身\t2,所有\t2,天堂\t4,和平\t2,换\t3,、\t3,辈\t2,去\t2,更\t3,英烈\t2,向\t3,默哀\t8,儿子\t3,永远\t4,为什么\t2,为\t3,自己\t3,希望\t3,一路\t15,你们\t17,不再\t2,揪心\t2,平安\t2,][\t2,安全\t2,可爱\t2,谁\t3,一个\t4,无论\t2,让\t4,会\t2,这么\t2,能\t2,我\t10,勇敢\t3,下辈子\t2,消防\t7,太\t4,真正\t2,与\t2,了\t15,哀\t2,吧\t2,好\t28,不行\t2,再\t4,我们\t7,每天\t2,人民\t3,杠\t2,员\t6,安\t2,公布\t2,山林\t2,们\t5,救火\t2,用\t3,心\t2,有\t2,是\t17,只\t2,也\t4,愿\t3,这个\t2,孩子\t2,过\t2,在\t14,没有\t3,大\t2,蠟\t3,后\t6,\t5,太平\t2,没\t4,时代\t2,？\t3,都\t13,记得\t2,个\t5,走\t19,烈士\t2,致敬\t11,不\t6,多少\t2,守护\t2,今天\t2,吗\t2,来\t3,人生\t2,应该\t2,一\t5,连\t2,一起\t3,下\t2,着\t3,这\t6,家庭\t4,生命\t7,目\t2,！\t12,那些\t2,何\t2,英雄\t32,无\t3,不要\t2,燭\t3,最\t5,而\t2,但\t3,欠\t4,将\t2,最后\t2,年轻\t6,\uD83D\uDE4F\uD83D\uDE4F\uD83D\uDE4F\t2,\uD83D\uDD6F\t2,痛\t5,认识\t2,送别\t7,两\t2,：\t2,多\t2,铭记\t2,就\t4,哭\t2,只有\t2,安息\t2,烈火\t2,鲜活\t2,和\t7,父亲\t3,他们\t11,90\t2,00\t2,什么\t2,方\t2,牺牲\t7,人\t7,几\t2,丈夫\t2,名字\t2";
        for (String word : words.split(",")) {
            if (word.split("\t").length == 2) {
                String name = word.split("\t")[0];
                Integer value = Integer.parseInt(word.split("\t")[1]);

                list.add(new Word(name, value));
            }
        }
        return list;

    }


}
