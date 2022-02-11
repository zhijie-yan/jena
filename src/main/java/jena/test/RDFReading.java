package jena.test;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class RDFReading {
    public static void main(String[] args) {
        String inputFileName = "resource.rdf";
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(inputFileName);
        if(in == null)
            throw new IllegalArgumentException("File: "+inputFileName+" not found");
        model.read(in,null);
        model.write(System.out);
    }
}
