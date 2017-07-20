package it.uniba.di.lacam.ontologymining.owl2neo4j;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class KnowledgeBase {
	
	private Set<OWLClass> classesInSignature;
	private Set<OWLNamedIndividual> instances;
	private Set<OWLObjectProperty> propertyInSignature;
	private OWLReasoner reasoner;
	public KnowledgeBase(String ontoFile) throws OWLOntologyCreationException{
		
		
		OWLDataFactoryImpl owlDataFactoryImpl = new OWLDataFactoryImpl();
		OWLOntologyManager manager =OWLManager.createOWLOntologyManager();
		
		OWLOntology onto=manager.loadOntologyFromOntologyDocument(new File(ontoFile));
		OWLReasonerFactory f= new JFactFactory();
		reasoner = f.createNonBufferingReasoner(onto);

		//Transaction tr= session.beginTransaction();

		//	try{


		classesInSignature = onto.getClassesInSignature();
		 instances = onto.getIndividualsInSignature();
		propertyInSignature = onto.getObjectPropertiesInSignature();
	}
	public OWLReasoner getReasoner() {
		return reasoner;
	}
	public void setReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}
	public Set<OWLClass> getClassesInSignature() {
		return classesInSignature;
	}
	public void setClassesInSignature(Set<OWLClass> classesInSignature) {
		this.classesInSignature = classesInSignature;
	}
	public Set<OWLNamedIndividual> getInstances() {
		return instances;
	}
	public void setInstances(Set<OWLNamedIndividual> instances) {
		this.instances = instances;
	}
	public Set<OWLObjectProperty> getPropertyInSignature() {
		return propertyInSignature;
	}
	public void setPropertyInSignature(Set<OWLObjectProperty> propertyInSignature) {
		this.propertyInSignature = propertyInSignature;
	}

}
