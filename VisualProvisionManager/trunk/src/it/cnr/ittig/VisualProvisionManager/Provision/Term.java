package it.cnr.ittig.VisualProvisionManager.Provision;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;

public class Term extends Provision{
	
	private Individual individual;
	private String definiendum;
	private String definiens;

	public Term(OntModel ontModel, String param1,String param2,String law) {
		super(ontModel);
		definiendum=param1;
		definiens=param2;
		String []parameters=new String[]{param1,param2};
		//Object []value=new Object[]{param1,param2};
		setText(law);
		setOntClass(getNS()+"Term");
		individual=getOntModel().createIndividual(getNS()+"individuo",getOntClass());//per ora gli faccio sempre salvare col nome individuo
		OntProperty property=null;
		//OntProperty pippo=null;//NO
		int i=0;
		for(ExtendedIterator it=getOntClass().listDeclaredProperties();it.hasNext();){
			property=(OntProperty)it.next();
			//pippo=ontModel.createOntProperty(getNS()+parameters[i]);;//SBAGLIATO, CREA UN ALTRA PROPRIETA' PER LA CLASSE
			//individual.setPropertyValue(property,(RDFNode)pippo);
			i++;
		}
		System.out.println("Creo: "+getType()+" individuo "+individual.toString());
	}
	
	

}
