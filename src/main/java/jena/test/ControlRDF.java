package jena.test;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;

public class ControlRDF {
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

        System.out.println("原始内容：");
        model.write(System.out);

        //删除Statement
        model.removeAll(null, VCARD.N, (RDFNode)null);
        //也可以用model.remove(StmtIterator itor)方法
        // model.remove(model.listStatements(null, VCARD.N,(RDFNode)null )); 下面两句也可以相对替换

        model.removeAll(null,VCARD.Given,(RDFNode)null);
        model.removeAll(null,VCARD.Family,(RDFNode)null);
        System.out.println("\n删除后的内容：");
        model.write(System.out);

        //增加Statement
        //用model.add(Resource arg1, Property arg2, RDFNode arg3)方法
        model.add(johnSmith,VCARD.N,model.createResource().addProperty(VCARD.Given, givenName).addProperty(VCARD.Family, familyName));
        System.out.println("\n重新增加后的内容：");
        model.write(System.out);
    }
}
