package jena.generate;

import net.sf.json.JSONObject;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonValue;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class getBaseData {

    public static HashMap<String,Integer> name2id = new HashMap<>();
    public static HashMap<Integer,String> id2name = new HashMap<>();
    public static HashMap<Integer,Entry> id2entry = new HashMap<>();


    private static String pathJson = "src/main/resources/water/水利大辞典-定义-整理数据.json";
    private static String pathHypernym = "src/main/resources/water/hypernym.csv";
    private static String pathHyponyms = "src/main/resources/water/hyponyms.csv";
    private static String pathDictHyponyms = "src/main/resources/water/dictHyponyms.csv";
    private static String pathSortWords = "src/main/resources/water/sortWord.csv";


    public getBaseData() throws IOException {
        LinkedList<Entry> jsonData = getJsonData(pathJson);
    }

    public static LinkedList<Integer> getSortWord(String pathName){
        LinkedList<Integer> sortArr = new LinkedList<>();
        // 创建scanner
        try (Scanner scanner = new Scanner(Paths.get(pathName).toFile())) {
            while (scanner.hasNext()) {
                String str = scanner.next();
                int idNum = Integer.parseInt(str);
                sortArr.add(idNum);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sortArr;
    }

    /**
     * 读取关系
     * @return 一个关系的hashMap
     */
    public static HashMap<Integer, Integer> getCsvRelationships(String pathName){
        HashMap<Integer,Integer> relations = new HashMap<>();
        // 创建scanner
        try (Scanner scanner = new Scanner(Paths.get(pathName).toFile())) {
            // CSV文件分隔符
            String DELIMITER = ",";
            // 设置分隔符
//            scanner.useDelimiter(DELIMITER);
            // 读取
            while (scanner.hasNext()) {
                String str = scanner.next();
                String[] splitStr = str.split(DELIMITER);
                String start = splitStr[0];
                String end = splitStr[1];
                relations.put(Integer.valueOf(start),Integer.valueOf(end));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return relations;
    }

    /**
     * 读取json数据并解析出需要的参数形式
     * 需要的数据有：
     * 1.id和name的互相映射，两个map
     * 2.id和json词条的map映射，可以根据id找到entry对象
     */
    public static LinkedList<Entry> getJsonData(String pathname) throws IOException {
        File file=new File(pathname);
        JsonValue jsonArr = readerMethod(file);
        JsonArray asArray = jsonArr.getAsArray();
        LinkedList<Entry> jsonData = new LinkedList<>();
        // 必要参数获取
        for(int i=0;i<asArray.size();i++){
            Entry newEntry = transforClass(asArray.get(i).toString());
            jsonData.add(newEntry);
            Integer id = newEntry.getId();
            String name = newEntry.getName();
            id2name.put(id,name);
            name2id.put(name,id);
            id2entry.put(id,newEntry);
        }
        return jsonData;
    }

    /**
     * 读取json数据
     * @return 列表形式的json结果
     */
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
        return JSON.parseAny(jsonStr);
    }

    /**
     * 将json字符串形式数据解析成entry对象
     */
    private static Entry transforClass(String jsonStr){
        //JSON-LIB
        JSONObject jsonResult = JSONObject.fromObject(jsonStr);
        Entry entry = (Entry) JSONObject.toBean(jsonResult, Entry.class);
        return entry;
    }
}