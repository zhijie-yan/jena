package jena.generate;

import net.sf.json.JSONObject;
import org.apache.http.entity.StringEntity;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonValue;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class getBaseData {

    public HashMap<String, Integer> name2id;
    public HashMap<Integer, String> id2name;
    public HashMap<Integer, Entity> id2entry;

    public HashMap<Integer, Integer> dictRel;
    public HashMap<Integer, Integer> hypernymRel;
    public HashMap<Integer, Integer> hyponymsRel;
    public HashMap<String, Entity> jsonData;

    public HashMap<String, String> sameMeanRel;
    public HashMap<String, String> wordInOthWordsRel;
    public HashMap<String, String> tripletRel;
    public HashMap<String, String> segWordsRel;
    public HashMap<String, String> noFindWordsRel;

    public LinkedList<String> sortWords;
    public LinkedList<String> orgData;
    public LinkedList<String> perHisData;
    public LinkedList<String> perSciData;
    public LinkedList<String> rivData;

    private String pathJson = "src/main/resources/water/水利大辞典-定义-整理数据.json";
    private String pathHypernym = "src/main/resources/water/hypernym.csv";
    private String pathHyponyms = "src/main/resources/water/hyponyms.csv";
    private String pathDictHyponyms = "src/main/resources/water/dictHyponyms.csv";
    private String pathSortWords = "src/main/resources/water/排序词条3.csv";

    private String pathOrg = "src/main/resources/individual/水利科技-组织机构.csv";
    private String pathHisPer = "src/main/resources/individual/水利史-人名.csv";
    private String pathSciPer = "src/main/resources/individual/水利科技-人名.csv";
    private String pathRiv = "src/main/resources/individual/河流沟渠.csv";

    private String pathSameMean = "src/main/resources/newRelation/sameMeanRel.csv";
    private String pathWordInOthWord = "src/main/resources/newRelation/wordInOtherWords.csv";
    private String pathTriplet = "src/main/resources/newRelation/tripletPath.csv";
    private String pathSegWords = "src/main/resources/newRelation/segWords.csv";
    private String pathNoFind = "src/main/resources/newRelation/noFindWords.csv";

    public getBaseData() throws IOException {
        name2id = new HashMap<>();
        id2name = new HashMap<>();
        id2entry = new HashMap<>();
        jsonData = getJsonData(pathJson);
        sortWords = getSignalWord(pathSortWords);

        orgData = getSignalWord(pathOrg);
        perHisData = getSignalWord(pathHisPer);
        perSciData = getSignalWord(pathSciPer);
        rivData = getSignalWord(pathRiv);

        dictRel = getCsvRelationships(pathDictHyponyms);
        hypernymRel = getCsvRelationships(pathHypernym);
        hyponymsRel = getCsvRelationships(pathHyponyms);

        sameMeanRel = getCsvNameRelationships(pathSameMean);
        wordInOthWordsRel = getCsvNameRelationships(pathWordInOthWord);
        tripletRel = getCsvNameRelationships(pathTriplet);
        segWordsRel = getCsvNameRelationships(pathSegWords);
        noFindWordsRel = getCsvNameRelationships(pathNoFind);
    }

    public LinkedList<String> getSignalWord(String pathName) {
        LinkedList<String> signalArr = new LinkedList<>();
        // 创建scanner
        try (Scanner scanner = new Scanner(Paths.get(pathName).toFile())) {
            while (scanner.hasNext()) {
                String str = scanner.next();
//                int idNum = Integer.parseInt(str);
                signalArr.add(str);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return signalArr;
    }

    /**
     * 读取id类型关系
     * @return 一个关系的hashMap
     */
    public HashMap<Integer, Integer> getCsvRelationships(String pathName) {
        HashMap<Integer, Integer> relations = new HashMap<>();
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
                relations.put(Integer.valueOf(start), Integer.valueOf(end));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return relations;
    }

    /**
     * 读取name类型关系数据
     * @return 返回关系
     */
    public HashMap<String, String> getCsvNameRelationships(String pathName) {
        HashMap<String, String> relations = new HashMap<>();
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
                relations.put(start, end);
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
    public HashMap<String, Entity> getJsonData(String pathname) throws IOException {
        File file = new File(pathname);
        JsonValue jsonArr = readerMethod(file);
        JsonArray asArray = jsonArr.getAsArray();
        HashMap<String, Entity> jsonData = new HashMap<>();
        // 必要参数获取
        for (int i = 0; i < asArray.size(); i++) {
            Entity newEntity = transforClass(asArray.get(i).toString());
            Integer id = newEntity.getId();
            String name = newEntity.getName();
            jsonData.put(name, newEntity);
            id2name.put(id, name);
            name2id.put(name, id);
            id2entry.put(id, newEntity);
        }
        return jsonData;
    }

    /**
     * 读取json数据
     *
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
    private Entity transforClass(String jsonStr) {
        //JSON-LIB
        JSONObject jsonResult = JSONObject.fromObject(jsonStr);
        Entity entity = (Entity) JSONObject.toBean(jsonResult, Entity.class);
        return entity;
    }

}