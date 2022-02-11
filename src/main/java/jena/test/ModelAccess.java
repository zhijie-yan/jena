package jena.test;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.VCARD;

public class ModelAccess {
    public static void main(String[] args) {
        String personURI = "http://somewhere/JohnSmith";
        String givenName = "John";
        String familyName = "Smith";
        String fullName = givenName + " " + familyName;
        Model model = ModelFactory.createDefaultModel();

        Resource johnSmith = model.createResource(personURI);
        johnSmith.addProperty(VCARD.FN, fullName);
        johnSmith.addProperty(VCARD.N,
                model.createResource()
                        .addProperty(VCARD.Given, givenName)
                        .addProperty(VCARD.Family, familyName));

        /*------假设以上一切已是一个RDF文件中的声明，且我们未知，现在要对一个Model操作（一个Model是一个Description）--------------------------------------*/

        //从Model中获得资源，假设我们已经知道这个Description主语的URI
        Resource vcard = model.getResource(personURI);

        //如果知道该属性(VCARD.N)的值是属性，可以直接使用属性的getResource()方法
        //Resource name = vcard.getProperty(VCART.N).getResource();
        Resource name = (Resource) vcard.getProperty(VCARD.N).getObject();

        //如果知道该属性的值是literal，可以直接使用属性的getString()方法
        fullName = vcard.getProperty(VCARD.FN).getString();

        //可以为这个主语增加属性
        vcard.addProperty(VCARD.NICKNAME,"Smithy").addProperty(VCARD.NICKNAME, "Adman");

        //用迭代器查看该属性的值
        System.out.println("The nicknames of \""+fullName+"\" are:");
        StmtIterator itor = vcard.listProperties(VCARD.NICKNAME);
        while(itor.hasNext()){
            System.out.println("   "+itor.nextStatement().getObject().toString());
        }
        System.out.println("----------------------------------------");
        //查看加入新属性后的RDF/XML
        model.write(System.out);
    }
}

