# owl2neo4j
A simple utility tool to store an OWL ontology as a Neo4j graph

# Requirements
 -  JFact 5.0.01
 -  OWLAPI 5.0.0
 -  Neo4j Community Edition v1
 -  Neo4j Data Driver v. 1.2.1
 
# How to run
In order to store an OWL ontology, run the following command  
  `java <address:portnumber> <user> <pwd> <ontologypath/ontologyname>`
where `<address:portnumber> <user> <pwd>` are the address (with the number of port), the username and the password for Neo4j connection and `<ontologypath/ontologyname>` is the path on your file system  of the ontology you want to load 

