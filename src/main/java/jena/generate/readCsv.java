package jena.generate;

import net.sf.json.JSONObject;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonValue;

import java.io.*;
import java.util.LinkedList;

public class readCsv {
    public static void main(String args[]) throws IOException {

    }
    public static LinkedList<Entry> getJsonData() throws IOException {
        File file=new File("src/main/resources/water/水利大辞典-定义-整理数据.json");
        JsonValue jsonArr = readerMethod(file);
        JsonArray asArray = jsonArr.getAsArray();
        LinkedList<Entry> jsonData = new LinkedList<>();
        /**
         * 需要有id到name的映射
         * 有name到id的映射
         *
         */
        for (int i=0;i<asArray.size();i++){
            jsonData.add(transforClass(asArray.get(i).toString()));
        }
        return jsonData;
    }

    private static JsonValue readerMethod(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), "Utf-8");
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = sb.toString();
//        System.out.println(JSON.parseAny(jsonStr));
        return JSON.parseAny(jsonStr);
    }

    private static Entry transforClass(String jsonStr){
        //JSON-LIB
        JSONObject jsonResult = JSONObject.fromObject(jsonStr);
        Entry entry = (Entry) JSONObject.toBean(jsonResult, Entry.class);
//        System.out.println("Entry:" + entry);
        return entry;
    }
}