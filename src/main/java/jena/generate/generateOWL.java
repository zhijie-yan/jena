package jena.generate;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class generateOWL {
    public static getBaseData baseData;
    // 建立本体模型，规则为OWL语言规则
    public static OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    // exNs为名称空间的标识
    public static String exNs = "http://www.xidian.edu.cn/owl/ontologies/zjyan#";
    //    public static String exNs = "http://www.semanticweb.org/jeffery/ontologies/2021/5/hydro-ontology.owl#";
    // 类
    public static HashMap<String, OntClass> str2Class = new HashMap<>();
    public static HashMap<String, OntClass> oldStr2Class = new HashMap<>();
    // 实例
    public static HashMap<String, Individual> str2Individual = new HashMap<>();
    // 已经被创建的类id集合
//    public static HashSet<String> createdNames = new HashSet<>();
    // 创建的本体骨架
    public static String boneOwl = "src/main/resources/ontology/水利本体_v2_0.owl";
    public static String bonePrefix = "http://www.semanticweb.org/jeffery/ontologies/2021/5/hydro-ontology.owl#";
    // 输出的位置
    public static String outputOwl = "src/main/resources/ontology/水利本体_v2_0.owl";

    public static void main(String[] args) throws IOException {
        baseData = new getBaseData();
        // 生成类 设置词条对应的解释 别名
        ObjectProperty hasAlias = m.createObjectProperty(exNs + "hasAlias");
        m.read(boneOwl);
        m.write(System.out, "RDF/XML");
        int start = 0;
        int end = 50;
        for (int i = start; i < end; i++) {
            String name = baseData.sortWords.get(i);
            OntClass existClass = m.getOntClass(bonePrefix + name);
            if (existClass != null) {
                // 旧类
                System.out.println(name);
                oldStr2Class.put(name, existClass);
                if (existClass.getLabel("注释") == null) {
                    oldStr2Class.get(name).addLabel(baseData.jsonData.get(name).getContext(), "注释");
                }
            } else {
                str2Class.put(name, m.createClass(bonePrefix + name));
                str2Class.get(name).addLabel(baseData.jsonData.get(name).getContext(), "注释");
                for (String alias : baseData.jsonData.get(name).getAlias()) {
                    str2Class.get(name).addLabel(alias, "亦称");
                }
            }


        }

//        for (Entity entity :baseData.jsonData){
//            String name = entity.getName();
//            str2Class.put(name,m.createClass(exNs + name));
//        }
        //设置相等类关系
        setEqualsClass(baseData.sameMeanRel);
        // 设置类的继承关系
//        setSubClassById(baseData.hypernymRel);
//        setSubClassById(baseData.hyponymsRel);
//        setSubClassById(baseData.dictRel);

        setSubClassByName(baseData.wordInOthWordsRel);
        setSubClassByName(baseData.tripletRel);
        setSubClassByName(baseData.segWordsRel);
        setSubClassByName(baseData.noFindWordsRel);

        // 设置实例
        setIndividual(baseData.orgData, "组织");
        setIndividual(baseData.perSciData, "人物");
        setIndividual(baseData.perHisData, "人物");
        setIndividual(baseData.rivData, "河流");

//        setIndividual(baseData.termData,"水利术语");
        // 写出
        wirteRdf();
    }

    private static void wirteRdf() {
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(outputOwl);//没有文件会自动创建
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        m.write(fwriter);
        m.write(System.out, "RDF/XML");
    }

    private static void setIndividual(LinkedList<String> data, String className) {
        for (String name : data) {
            OntClass boneClass = m.getOntClass(bonePrefix + className);
            if (!str2Class.containsKey(className) && boneClass == null) {
                str2Class.put(className, m.createClass(exNs + className));
            } else {
                str2Class.put(className, boneClass);
            }

            str2Individual.put(name, m.createIndividual(exNs + name, str2Class.get(className)));
        }
    }

    private static void setEqualsClass(HashMap<String, String> rel) {
        for (Map.Entry<String, String> entry : rel.entrySet()) {
            String startName = entry.getKey();
            String endName = entry.getValue();
            if (str2Class.containsKey(startName) && str2Class.containsKey(endName) && !oldStr2Class.containsKey(endName)) {
                str2Class.get(startName).addEquivalentClass(str2Class.get(endName));
            } else if (str2Class.containsKey(endName) && str2Class.containsKey(startName) && !oldStr2Class.containsKey(startName)) {
                str2Class.get(endName).addEquivalentClass(str2Class.get(startName));
            }
        }
    }

    private static void setSubClassByName(HashMap<String, String> rel) {
        for (Map.Entry<String, String> entry : rel.entrySet()) {
            String startName = entry.getKey();
            String endName = entry.getValue();
            if (str2Class.containsKey(startName) && str2Class.containsKey(endName) && !oldStr2Class.containsKey(endName)) {
                str2Class.get(startName).addSubClass(str2Class.get(endName));
            } else if (str2Class.containsKey(endName) && str2Class.containsKey(startName) && !oldStr2Class.containsKey(startName)) {
                str2Class.get(endName).addSubClass(str2Class.get(startName));
            }
        }
    }

    private static void setSubClassById(HashMap<Integer, Integer> rel) {
        for (Map.Entry<Integer, Integer> entry : rel.entrySet()) {
            String startName = baseData.id2name.get(entry.getKey());
            String endName = baseData.id2name.get(entry.getValue());
            if (str2Class.containsKey(startName) && str2Class.containsKey(endName) && !oldStr2Class.containsKey(endName)) {
                str2Class.get(startName).addSubClass(str2Class.get(endName));
            } else if (str2Class.containsKey(endName) && str2Class.containsKey(startName) && !oldStr2Class.containsKey(startName)) {
                str2Class.get(endName).addSubClass(str2Class.get(startName));
            }
        }
    }
}
