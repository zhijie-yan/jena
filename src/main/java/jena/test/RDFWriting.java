package jena.test;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;

public class RDFWriting {
    public static void main(String[] args){

        String personURI = "http://somewhere/JohnSmith";
        Model model = ModelFactory.createDefaultModel();

        Resource johnSmith = model.createResource(personURI);
        johnSmith.addProperty(VCARD.FN,"John Smith");
        johnSmith.addProperty(VCARD.N,model.createResource().addProperty(VCARD.Given,"John").addProperty(VCARD.Family,"Smith"));

        //以不同格式输出RDF
        System.out.println("RDF/XML:");
        model.write(System.out,"RDF/XML");
        System.out.println("\n"+"RDF/XML-ABBREV:");
        model.write(System.out, "RDF/XML-ABBREV");
        System.out.println("\n"+"N-TRIPLE:");
        model.write(System.out,"N-TRIPLE");
        System.out.println("\n"+"TURTLE:");
        model.write(System.out,"TURTLE");
    }
}
