package it.cnr.ittig.VisualProvisionManager.Provision;

import com.hp.hpl.jena.ontology.*;

public abstract class Provision {
	
	private OntClass type; //TIPO DI DISPOSIZIONE
	private String baseURI = new String("http://provisions.org/model/1.0");
	private String NS= new String(baseURI + "#");
	private OntModel model;
	private String text; //TODO è giusto che sia una String??
	
	
	public Provision(OntModel ontModel)
	{
		model=ontModel;
	}
	public String getNS(){
		return NS;
	}
	
	//SERVIRA'??
	public void setNS(String string){
		NS=string;
	}
	
	public OntModel getOntModel(){
		return model;
	}
	
	//SERVIRA'??
	
	public void setOntModel(OntModel ont){
		model=ont;
	}
	
	
	public OntClass getOntClass(){
		return type;
	}
	
	public void setOntClass(String string){
		type=this.model.getOntClass(string);
	}
	
	
	//ritorna il tipo di provision cui la disposizione appartiene 
	public String getType(){
		return type.toString();
		
	}
	//RITORNA IL TESTO DELLA DISPOSIZIONE
	public String getText(){
		return text;
	}
	
	public void setText(String string){
		text=string;
	}

}
