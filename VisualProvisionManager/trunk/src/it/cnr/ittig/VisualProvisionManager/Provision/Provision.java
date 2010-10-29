package it.cnr.ittig.VisualProvisionManager.Provision;

import com.hp.hpl.jena.ontology.*;

public class Provision {
	
	private OntClass typeClass; //CLASSE OWL DELLA DISPOSIZIONE (serve????)
	private String type; //INDICA IL TIPO DELLA DISPOSIZIONE (TERM, PROCEDURE...)
	private String ID; //UTILE PER INDIVIDUARE IN MODO UNIVOCO LA DISPOSIZIONE, E' IL SUO NOME. IMPOSTATO IN PROVISIONFRAME QUANDO CREO LA DISPOSIZIONE
	private String baseURI = new String("http://provisions.org/model/1.0");
	private String NS= new String(baseURI + "#");
	private OntModel model;
	private String text; //TODO è giusto che sia una String??
	
	
	public Provision(OntModel ontModel)
	{
		model=ontModel;
	}
	
	public Provision(OntClass provClass){
		typeClass=provClass;
		type=getProvisionType(provClass);
	}

	
	public void setID(String name){
		ID=name;
	}
	
	
	public String getID(){
		return ID;
	}
	//ritorna il tipo di provision cui la disposizione appartiene 
	public String getType(){
		return type;
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
		return typeClass;
	}
	
	public void setOntClass(String string){
		typeClass=this.model.getOntClass(string);
	}
	
	
	//RITORNA IL TESTO DELLA DISPOSIZIONE
	public String getText(){
		return text;
	}
	
	public void setText(String string){
		text=string;
	}
	
	private String getProvisionType(OntClass ont){
		String provisionType=ont.toString();
		for(int i=provisionType.length()-1;i!=-1;i--)
			if(provisionType.charAt(i)=='#'){
				provisionType=provisionType.substring(i+1,provisionType.length());
				break;
			}
		return provisionType;
	}

}
