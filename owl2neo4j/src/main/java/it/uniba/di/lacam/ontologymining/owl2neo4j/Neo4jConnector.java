package it.uniba.di.lacam.ontologymining.owl2neo4j;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Value;

/**
 * A wrapper for Neo4j connector
 * @author Utente
 *
 */
public class Neo4jConnector {
	
	
	Driver driver;
	Session session;
	String path;
	String usr;
	String pwd;
	public Neo4jConnector(String path, String usr, String pwd){
		
		this.path = path;
		this.usr= usr;//"C:/Users/Utente/Documents/Dottorato/Dataset/ontologies/biopax.owl";
		this.pwd= pwd;
		driver= GraphDatabase.driver(path,AuthTokens.basic(usr, pwd));
		session = driver.session();
		
	}
	public Driver getDriver() {
		return driver;
	}
	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getUsr() {
		return usr;
	}
	public void setUsr(String usr) {
		this.usr = usr;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public void write(String query){
		
		this.session.run(query);
		
		
	}
public void write(String query,Value value){
		
		this.session.run(query, value);
		
		
	}

public void close(){
	session.close();
	driver.close();
}
}
