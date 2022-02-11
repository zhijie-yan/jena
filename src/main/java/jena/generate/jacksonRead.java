package jena.generate;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;

public class jacksonRead {
    private static String jsonStr = "{\"name\":\"zhangshan\", \"age\": 10, \"address\": \"中国深圳\"}";

    public static void main(String[] args) {

        //JSON-LIB
        JSONObject jsonResult = JSONObject.fromObject(jsonStr);
        Entry entry = (Entry) JSONObject.toBean(jsonResult, Entry.class);
        System.out.println("Entry:" + entry);

        //fastjson
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonStr);
        Entry entry2 = com.alibaba.fastjson.JSONObject.toJavaObject(jsonObject, Entry.class);
        System.out.println("userInfo2:" + entry2);
    }

}
