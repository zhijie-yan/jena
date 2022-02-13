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

    public HashMap<String,Integer> name2id;
    public HashMap<Integer,String> id2name;
    public HashMap<Integer, Entity> id2entry;

    public HashMap<Integer, Integer> dictRel;
    public HashMap<Integer, Integer> hypernymRel;
    public HashMap<Integer, Integer> hyponymsRel;
    public LinkedList<Entity> jsonData;

    public LinkedList<Integer> sortWords;
    public LinkedList<Integer> orgData;
    public LinkedList<Integer> perData;
    public LinkedList<Integer> termData;

    private String pathJson = "src/main/resources/water/水利大辞典-定义-整理数据.json";
    private String pathHypernym = "src/main/resources/water/hypernym.csv";
    private String pathHyponyms = "src/main/resources/water/hyponyms.csv";
    private String pathDictHyponyms = "src/main/resources/water/dictHyponyms.csv";
    private String pathSortWords = "src/main/resources/water/sortWord.csv";

    private String pathOrg = "src/main/resources/water/水利科技.csv";
    private String pathPer = "src/main/resources/water/水利史.csv";
    private String pathTerm = "src/main/resources/water/terms.csv";

    public getBaseData() throws IOException {
        name2id = new HashMap<>();
        id2name = new HashMap<>();
        id2entry = new HashMap<>();
        jsonData = getJsonData(pathJson);
        sortWords = getSignalWord(pathSortWords);
        orgData = getSignalWord(pathOrg);
        perData = getSignalWord(pathPer);
        termData = getSignalWord(pathTerm);
        dictRel = getCsvRelationships(pathDictHyponyms);
        hypernymRel = getCsvRelationships(pathHypernym);
        hyponymsRel = getCsvRelationships(pathHyponyms);
    }

    public LinkedList<Integer> getSignalWord(String pathName){
        LinkedList<Integer> signalArr = new LinkedList<>();
        // 创建scanner
        try (Scanner scanner = new Scanner(Paths.get(pathName).toFile())) {
            while (scanner.hasNext()) {
                String str = scanner.next();
                int idNum = Integer.parseInt(str);
                signalArr.add(idNum);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return signalArr;
    }

    /**
     * 读取关系
     * @return 一个关系的hashMap
     */
    public HashMap<Integer, Integer> getCsvRelationships(String pathName){
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
    public LinkedList<Entity> getJsonData(String pathname) throws IOException {
        File file=new File(pathname);
        JsonValue jsonArr = readerMethod(file);
        JsonArray asArray = jsonArr.getAsArray();
        LinkedList<Entity> jsonData = new LinkedList<>();
        // 必要参数获取
        for(int i=0;i<asArray.size();i++){
            Entity newEntity = transforClass(asArray.get(i).toString());
            jsonData.add(newEntity);
            Integer id = newEntity.getId();
            String name = newEntity.getName();
            id2name.put(id,name);
            name2id.put(name,id);
            id2entry.put(id, newEntity);
        }
        return jsonData;
    }

    /**
     * 读取json数据
     * @return 列表形式的json结果
     */
    private JsonValue readerMethod(File file) throws IOException {
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
    private Entity transforClass(String jsonStr){
        //JSON-LIB
        JSONObject jsonResult = JSONObject.fromObject(jsonStr);
        Entity entity = (Entity) JSONObject.toBean(jsonResult, Entity.class);
        return entity;
    }

}