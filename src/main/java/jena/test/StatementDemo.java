package jena.test;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

public class StatementDemo {
    public static void main(String[] args){

        //声明主语的URI并建立模型 对应protege的ontology
        String personURI = "http://somewhere/JohnSmith";
        //model是一切的基础，
        Model model = ModelFactory.createDefaultModel();

        //创立主谓宾关系，谓语是jena.vocabulary.VCARD自带的
        Resource johnSmith = model.createResource(personURI);
        johnSmith.addProperty(VCARD.FN,"John Smith");
        johnSmith.addProperty(VCARD.N,model.createResource().addProperty(VCARD.Given,"John").addProperty(VCARD.Family,"Smith"));

        //建立声明迭代器
        StmtIterator iter = model.listStatements();

        while(iter.hasNext()){
            //取出每一个声明，每一个声明的主语、谓语、宾语
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();

            //用toString方法输出主谓宾
            System.out.print(subject.toString()+" -->");
            System.out.print(" "+predicate.toString()+" -->");

            //判断宾语是资源还是文字（literal），如果是资源（的实例），则直接输出，如果是文字，加引号输出
            if(object instanceof Resource){
                System.out.print(" "+object.toString());
            }else{
                System.out.print(" "+"\"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }
}
