package it.uniba.di.lacam.ontologymining.owl2neo4j;

import org.neo4j.driver.*;
import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.Node;
import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceDepth;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import static org.neo4j.driver.v1.Values.parameters;


import java.io.File;
import java.util.Set;
/**
 * Neo4jConnector main application
 *
 */

public class Neo4jConnectorMain 
{
	static final OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	public static void main( String[] args ) throws OWLOntologyCreationException
	{
		
		String defaultIP=args[0]==null?"bolt://localhost:7687": args[0];
		String user= args[1];
		String pwd=  args[2];
		String ontoFile= args[3]; //C:/Users/Utente/Documents/Dottorato/Dataset/ontologies/biopax.owl";
		long currentTimeMillis = System.currentTimeMillis();
		//db connection
		Neo4jConnector conn=null;
		try{
		conn= new Neo4jConnector(defaultIP, user, pwd);
		}
		catch(Exception e){
			
			System.out.println("****   Please, insert  correct connection information ****");
		}
		System.out.println(" **** Connected to the database ****");
		//load ontology
 
		KnowledgeBase kb= new KnowledgeBase(ontoFile);
		Set<OWLClass> classesInSignature = kb.getClassesInSignature();
		Set<OWLObjectProperty> propertyInSignature = kb.getPropertyInSignature();
		Set<OWLNamedIndividual> instances = kb.getInstances();
		OWLReasoner reasoner = kb.getReasoner();
		
		for (OWLClass c: classesInSignature){

			String classString1 =c.isOWLThing()? "Thing":renderer.render(c);
			//System.out.println(classString1);
			conn.write( "CREATE (a:"+classString1+")");

		}

		// set subclasses relationship

		System.out.println("**** Loading ontology: "+ ontoFile+ " **** ");
        System.out.println("**** 1- Classes loaded ****");

		// loading individuals
System.out.println("**** 2- Individuals loaded ****");
		for (OWLNamedIndividual i:instances){
			String ind = renderer.render(i);
			conn.write("create (a:Individual {name:{name}})",
					parameters("name", ind));
			//session.run(" MATCH (a:"+classString1+"), (b:Individual) where b.name="+ ind +"CREATE (b)-[r:instanceOf]->(a)");


		} 
		
		System.out.println("**** 3- Subsumption axioms ****");

		for (OWLClass c: classesInSignature){

			String classString1 =c.isOWLThing()? "Thing":renderer.render(c);
			//System.out.println(classString1);
			Set<OWLClass> superClasses = reasoner.getSuperClasses(c, true).getFlattened();
			//System.out.println("Superclasses:"+superClasses);
			// String classString1 =c.isOWLThing()? "Thing":renderer.render(c);
			//subsumption hierarchy
			for (OWLClass d: superClasses){

				String classString2 =d.isOWLThing()? "Thing":renderer.render(d);
				//System.out.println("Edge"+classString1 +" subClassOf "+classString2);
				if (classString2.compareTo(classString1)!=0) // evitare cappi
					conn.write(" MATCH (a:"+classString1+"), (b:"+classString2+")  CREATE (a)-[r:subClassOf]->(b)");
			}



			Set<OWLNamedIndividual> retrieved = reasoner.getInstances(c, InferenceDepth.DIRECT).getFlattened();// retrieve all the direct instances
			for (OWLNamedIndividual a: retrieved){
				String ind = renderer.render(a);
				conn.write(" MATCH (a:"+classString1+"), (b:Individual) where b.name= '"+ ind +"' CREATE (b)-[r:instanceOf]->(a)");
			}


		}
 System.out.println("**** 4- Class assertions loaded ****");


		
		for (OWLObjectProperty d: propertyInSignature){ 	 
			String propString =renderer.render(d);

			Set<OWLNamedIndividual> individualsInSignature = d.getIndividualsInSignature();
			for (OWLNamedIndividual i:individualsInSignature){
				i.getObjectPropertiesInSignature();
				String ind1 = renderer.render(i);
				Set<OWLNamedIndividual> objectPropertyValues = reasoner.getObjectPropertyValues(i, d).getFlattened(); //fillers

				for (OWLNamedIndividual ind2:objectPropertyValues){
					
					String ind2String = renderer.render(ind2);
					
					conn.write(" MATCH (a:Individual), (b:Individual) where a.name= '"+ ind1 +"' and b.name= '"+ ind2 +"' CREATE (b)-[r:"+propString+"]->(a)");
					
				
				}
					
				
			}

			//session.run( "CREATE (a:"+classString+")");

		}

		System.out.println("**** 4- Role assertions loaded ****");
		//	tr.success();
		//}
		//finally {
		// tr.close();;
		//}

		/*Driver  driver= GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j", "neo4j2"));
    	Session session = driver.session();*/

		//node creation

		//session.run("create (a:Person {name:{name}, title:{title}})",
		//			parameters("name", "Ginevre", "title", "Queen"));
		// retrieve the king and the queen and create a relationship between them
		//session.run (" match (a:Person) where a.name = 'Arthur' match (b:Person) where  b.name='Ginevre' create (a)-[:Loves]->(b)");

		//	session.run("match(n:Person) return n.title");
		conn.close();
		System.out.println("****  END  ****");
						System.out.println( "Overall Time"+ (System.currentTimeMillis()-currentTimeMillis));
	}




}
