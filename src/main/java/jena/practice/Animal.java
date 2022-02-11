package jena.practice;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DC;

import static org.apache.jena.sparql.vocabulary.DOAP.category;

public class Animal {
    private static EnumeratedClass CategorySet;

    public static void main(String[] args) {
        // 1.建立本体模型，规则为OWL语言规则
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        // 2.exNs为名称空间的标识
        String exNs = "http://doc.xuehai.net/owl/ontologies/example#";
        // 2.1创建类
        OntClass Animal = m.createClass(exNs + "Animal");
        OntClass Dog = m.createClass(exNs + "Dog");
        OntClass Cat = m.createClass(exNs + "Cat");
        OntClass People = m.createClass(exNs + "People");
        // 2.2设置类的继承关系
        Animal.addSubClass(Dog);
        Animal.addSubClass(Cat);
        // 2.3设置属性
        ObjectProperty hasDog = m.createObjectProperty(exNs + "hasDog");
        ObjectProperty hasPet = m.createObjectProperty(exNs + "hasPet");
        // 2.4设置属性的继承关系
        hasDog.addSuperProperty(hasPet);
        // 2.5.将属性和类关联起来。
        // 本文将属性hasPet的定义域设为People类，值域设为Animal类。
        // 即hasPet只可以用于这样的RDF陈述[People类实例，hasPet，Animal类实例]。
        hasPet.addDomain(People);
        hasPet.addRange(Animal);

        // 3.创建实例。下面创建了一个叫Charles狗的Dog类实例。
        Individual inst = m.createIndividual(exNs + " Charles", Dog);
        // 4.加入维护此本体文档所需的元数据。
        Ontology ont = m.createOntology(exNs);
//        ont.addProperty( http://doc.xuehai.netment,"this is an example ontology about Pets Managing");
//        ont.addProperty(http://doc.xuehai.netbel,"Pet Management Ontology");
        // 规则设置
        Dog.addDisjointWith(Cat);
        OntClass Doggie = m.createClass(exNs + "Doggie");
        Dog.addEquivalentClass(Doggie);
        // 增加图书出版信息
        OntClass Publication = m.createClass(exNs + "Publication");
        // 此外，本文建立了一个CategorySet(种类集合)的枚举类，所对应的实例为Bookcategory，Magazinecategory，Papercategory之一。
        // 现在可以为Publication添加一个category(种类)属性，将其值设置为CategorySet实例。
        Resource bookcategory = m.createResource(exNs + "book category");
        Resource magazinecategory = m.createResource(exNs + "magazinecategory");
        Resource papercategory = m.createResource(exNs + "paper category");
        RDFList cs = m.createList(new RDFNode[]{bookcategory, magazinecategory, papercategory});
        CategorySet = m.createEnumeratedClass(exNs + "CategorySet", cs);
        // Jena为Dublin Core专门提供了一个DC类，用户可直接利用DC.title等属性。
        // 但Jena只是简单创建了title、creator等属性，而没有为这些属性制定任何约束，所以本文根据需要对这些属性的定义做了些修改。
        // 例如，可以为title属性建立一个基数约束，让每个Publication类的实例只可以有一个title。
        CardinalityRestriction titleR = m.createCardinalityRestriction(null, DC.title, 1);
        Publication.addSuperClass(titleR);
        AllValuesFromRestriction categoryR = m.createAllValuesFromRestriction(null, category, CategorySet);
        Publication.addSuperClass(m.createAllValuesFromRestriction(null, category, CategorySet));
        ObjectProperty relation = m.createObjectProperty(DC.relation.getNameSpace() + DC.relation.getLocalName());
        relation.addDomain(Publication);
        relation.addRange(Publication);
        // 写出
        m.write( System.out,"RDF/XML");
    }
}
