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
    // 实例
    public static HashMap<String, Individual> str2Individual = new HashMap<>();
    // 已经被创建的类id集合
//    public static HashSet<String> createdNames = new HashSet<>();
    // 创建的本体骨架
    public static String boneOwl = "src/main/resources/output/boneOntology.owl";
    public static String bonePrefix = "http://www.semanticweb.org/jeffery/ontologies/2021/5/hydro-ontology.owl#";

    public static void main(String[] args) throws IOException {
        baseData = new getBaseData();
        // 生成类 设置词条对应的解释 别名
        ObjectProperty hasAlias = m.createObjectProperty(exNs+"hasAlias");
        m.read(boneOwl);
        m.write( System.out,"RDF/XML");
        int start = 0;
        int end = 50;
        for(int i=start;i<end;i++){
            String name = baseData.sortWords.get(i);
            OntClass existClass =  m.getOntClass(bonePrefix+name);
            if(existClass != null){
                System.out.println(name);
                str2Class.put(name,existClass);
                str2Class.get(name).addLabel(baseData.jsonData.get(name).getContext(),"注释");
            }else {
                str2Class.put(name,m.createClass(exNs + name));
                str2Class.get(name).addLabel(baseData.jsonData.get(name).getContext(),"注释");
            }

            for(String alias : baseData.jsonData.get(name).getAlias()){
                str2Class.get(name).addLabel(alias,"亦称");
            }

        }

//        for (Entity entity :baseData.jsonData){
//            String name = entity.getName();
//            str2Class.put(name,m.createClass(exNs + name));
//        }
        // 设置类的继承关系
        setSubClass(baseData.hypernymRel);
        setSubClass(baseData.hyponymsRel);
        setSubClass(baseData.dictRel);
        // 设置实例
        setIndividual(baseData.orgData,"组织");
        setIndividual(baseData.perData,"人物");
//        setIndividual(baseData.termData,"水利术语");
        // 写出
        wirteRdf();
    }

    private static void wirteRdf() {
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter("E:\\idea\\jena\\src\\main\\resources\\output\\myrdf.rdf");//没有文件会自动创建
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        m.write(fwriter);
        m.write( System.out,"RDF/XML");
    }

    private static void setIndividual(LinkedList<String> data, String className) {
        for (String name:data){
            OntClass boneClass = m.getOntClass(bonePrefix+className);
            if (!str2Class.containsKey(className) && boneClass == null){
                str2Class.put(className,m.createClass(exNs + className));
            }else {
                str2Class.put(className,boneClass);
            }

            str2Individual.put(name,m.createIndividual(exNs + name, str2Class.get(className)));
        }
    }


    private static void setSubClass(HashMap<Integer, Integer> rel) {
        for (Map.Entry<Integer, Integer> entry:rel.entrySet()){
            String startName = baseData.id2name.get(entry.getKey());
            String endName = baseData.id2name.get(entry.getValue());
            OntClass endClass = m.getOntClass(bonePrefix+endName);
            if(str2Class.containsKey(startName) && str2Class.containsKey(endName) && endClass == null){
                str2Class.get(startName).addSubClass(str2Class.get(endName));
            }
        }
    }
}
