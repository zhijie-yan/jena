package jena.generate;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class generateOWL {
    public static getBaseData baseData;
    // 建立本体模型，规则为OWL语言规则
    public static OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    // exNs为名称空间的标识
    public static String exNs = "http://www.xidian.edu.cn/owl/ontologies/zjyan#";
    // 类
    public static HashMap<String, OntClass> str2Class = new HashMap<>();
    // 实例
    public static HashMap<String, Individual> str2Individual = new HashMap<>();

    public static void main(String[] args) throws IOException {
        baseData = new getBaseData();
        // 生成类
        for (Entity entity :baseData.jsonData){
            str2Class.put(entity.getName(),m.createClass(exNs + entity.getName()));
        }
        // 设置类的继承关系
        setSubClass(baseData.hypernymRel);
        setSubClass(baseData.hyponymsRel);
//        setSubClass(baseData,str2Class,baseData.dictRel);
        // 设置实例
        setIndividual(baseData.orgData,"组织机构");
        setIndividual(baseData.perData,"人员");
        setIndividual(baseData.termData,"水利术语");
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
//        m.write(fwriter);
        m.write( System.out,"RDF/XML");
    }

    private static void setIndividual(LinkedList<String> data, String className) {
        for (String name:data){
//            String name = baseData.id2name.get(id);
            if (!str2Class.containsKey(className)){
                str2Class.put(className,m.createClass(exNs + className));
            }
            str2Individual.put(name,m.createIndividual(exNs + name, str2Class.get(className)));
        }
    }


    private static void setSubClass(HashMap<Integer, Integer> rel) {
        for (Map.Entry<Integer, Integer> entry:rel.entrySet()){
            String startName = baseData.id2name.get(entry.getKey());
            String endName = baseData.id2name.get(entry.getKey());
            str2Class.get(startName).addSubClass(str2Class.get(endName));
        }
    }
}
