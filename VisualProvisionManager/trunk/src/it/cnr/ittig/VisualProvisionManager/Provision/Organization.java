package it.cnr.ittig.VisualProvisionManager.Provision;

import it.cnr.ittig.VisualProvisionManager.applicationFrame;

import com.hp.hpl.jena.ontology.*;

//RAPPRESENTA UN INDIVIDUO DELLA CLASSE ORGANIZATION
public class Organization extends Provision{
	
	private Individual individual;
	private String address;
	
	
	public Organization(OntModel ontModel,String addr, String law){
		super(ontModel);
		setOntClass(getNS()+"Organization");
		individual=getOntModel().createIndividual(getNS()+"individuo",getOntClass());//per ora gli faccio sempre salvare col nome individuo
		address=addr;
		setText(law);
		System.out.println("Creo: "+getType()+" individuo "+individual.toString());;
	}
	
	public String getAddr(){
		return address;
	}

}
