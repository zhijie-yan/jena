package jena.test;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.VCARD;

import java.io.InputStream;


public class RDFQuery {
    public static String fileName = "resource.rdf";
    public static void main(String[] args) {
        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open(fileName);
        if(in == null)
            throw new IllegalArgumentException("file: "+fileName+" not found");
        model.read(in,null);

        ResIterator itor = model.listSubjectsWithProperty(VCARD.FN);
        if(itor.hasNext())
        {
            System.out.println("The database contains vcard for: ");
            while(itor.hasNext())
                System.out.println(" "+itor.nextResource().getProperty(VCARD.FN).getString());
        }
        else
            System.out.println("No vcards were found in the database");
    }
}
