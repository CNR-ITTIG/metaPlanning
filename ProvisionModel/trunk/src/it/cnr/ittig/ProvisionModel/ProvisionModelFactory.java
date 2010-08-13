package it.cnr.ittig.ProvisionModel;

//import com.hp.hpl.jena.graph.Graph;
//import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
//import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import java.io.*;

/*import javax.swing.JFrame;

import owl2prefuse.tree.OWLTreeConverter;
import owl2prefuse.tree.TreeDisplay;
import owl2prefuse.tree.TreePanel;
import prefuse.data.Tree;
*/

public class ProvisionModelFactory {

	/**
	 * @param args
	 */
	
	String baseURI = null;
	String NS = null;
	
	
	OntModel ProvisionModel = null;
	
	
	
	public void setBaseURI() {

		// URI declarations
		baseURI = new String("http://provisions.org/model/1.0");
	}
	
	
	public void setNS() {

		NS = new String(baseURI + "#");

	}
	
	
	
	public OntModel getProvisionModel() {

		// Create an empty Provision Ontology Model
		ProvisionModel = ModelFactory.createOntologyModel();

		
		//******BUILD TOP CLASSES
		buildProvisionTopClasses();
		
		//*****BUILD Constitutive
		buildConstitutiveProvisions();
		buildConstitutiveProvisionsSubClasses();
		
		//*****BUILD Regulative
		buildRegulativeProvisions();
		buildRegulativeProvisionsSubClasses();

		
		//******BUILD ARGUMENTS (Properties)****************************************	
		buildDefiniendum();
		buildDefiniens();
		buildAddressee();
		buildCounterpart();
		buildActivity();
		buildObject();
		buildBearer();
		buildActionProp();
		buildEffect();
		buildPenalty();
		
		
		return ProvisionModel;

	}
	

	private void buildProvisionTopClasses()
	{
		
		OntClass Provision = ProvisionModel.createClass(NS + "Provision");
		OntClass Rule = ProvisionModel.createClass(NS + "Rule");
		OntClass RuleOnRule = ProvisionModel.createClass(NS + "RuleOnRule");
		
		Provision.addSubClass(Rule);
		Provision.addSubClass(RuleOnRule);
		
	}
	
	private void buildConstitutiveProvisions()
	{
		//Constitutive
		OntClass Definition = ProvisionModel.createClass(NS + "Definition");		
		OntClass Creation = ProvisionModel.createClass(NS + "Creation");
		OntClass Attribution = ProvisionModel.createClass(NS + "Attribution");
		
		OntClass Rule = ProvisionModel.getOntClass(NS + "Rule");
		Rule.addSubClass(Definition);
		Rule.addSubClass(Creation);
		Rule.addSubClass(Attribution);
		
		
	}
	
	private void buildRegulativeProvisions()
	{
		
		//Regulatives
		OntClass Action = ProvisionModel.createClass(NS + "Action");
		OntClass Remedy = ProvisionModel.createClass(NS + "Remedy");
		
		OntClass Rule = ProvisionModel.getOntClass(NS + "Rule");
		
		Rule.addSubClass(Action);
		Rule.addSubClass(Remedy);
		
	}

	
	private void buildConstitutiveProvisionsSubClasses()
	{
		//*********CONSTITUTIVES SUB-CLASSES*************************************
	
		//Constitutive - "Definition" Sub-Class
		OntClass Term = ProvisionModel.createClass(NS + "Term");
		OntClass Procedure = ProvisionModel.createClass(NS + "Procedure");
	
		OntClass Definition = ProvisionModel.getOntClass(NS + "Definition");	
		Definition.addSubClass(Term);	
		Definition.addSubClass(Procedure);
	
		//Constitutive - "Creation" Sub-Class
		OntClass Establishment = ProvisionModel.createClass(NS + "Establishment");
		OntClass Organization = ProvisionModel.createClass(NS + "Organization");

		OntClass Creation = ProvisionModel.getOntClass(NS + "Creation");	
		Creation.addSubClass(Establishment);
		Creation.addSubClass(Organization);
	
	
		//Constitutive - "Attribution" Sub-Class
		OntClass Power = ProvisionModel.createClass(NS + "Power");
		OntClass Liability = ProvisionModel.createClass(NS + "Liability");
		OntClass Status = ProvisionModel.createClass(NS + "Status");
	
		OntClass Attribution = ProvisionModel.getOntClass(NS + "Attribution");	
		Attribution.addSubClass(Power);
		Attribution.addSubClass(Liability);
		Attribution.addSubClass(Status);
	
	}	
	
	private void buildRegulativeProvisionsSubClasses()
	{
		//***********REGULATIVE SUB-CLASSES***********************************	
		
		//Regulative - "Action" Sub-Class
		OntClass Right = ProvisionModel.createClass(NS + "Right");
		OntClass Duty = ProvisionModel.createClass(NS + "Duty");
		OntClass Prohibition = ProvisionModel.createClass(NS + "Prohibition");
		OntClass Permission = ProvisionModel.createClass(NS + "Permission");
		
		OntClass Action = ProvisionModel.getOntClass(NS + "Action");		
		Action.addSubClass(Right);
		Action.addSubClass(Duty);
		Action.addSubClass(Prohibition);
		Action.addSubClass(Permission);
		
		
		//Regulative - "Remedy" Sub-Class		
		OntClass Redress = ProvisionModel.createClass(NS + "Redress");
		OntClass Violation = ProvisionModel.createClass(NS + "Violation");
		
		OntClass Remedy = ProvisionModel.getOntClass(NS + "Remedy");		
		Remedy.addSubClass(Redress);
		Remedy.addSubClass(Violation);
		
	}
	

	
	private void buildDefiniendum()
	{
		OntProperty Definiendum = ProvisionModel.createOntProperty(NS + "Definiendum");

		//******DEFINIENDUM Property

		Definiendum.addDomain(ProvisionModel.getOntClass(NS + "Term")); //Più efficiente di setDomain che deve prima eliminare eventuali Domain esistenti


		//Definiendum.addRange(RDFS.Literal);
		Definiendum.addRange(XSD.xstring);
		Definiendum.addRange(XSD.IDREF);
	}

	private void buildDefiniens()
	{
		OntProperty Definiens = ProvisionModel.createOntProperty(NS + "Definiens");
		
		//******DEFINIENS Property
		Definiens.addDomain(ProvisionModel.getOntClass(NS + "Term"));
		Definiens.addRange(XSD.xstring);
		Definiens.addRange(XSD.IDREF);
	}
	
	private void buildAddressee()
	{
		OntProperty Addressee = ProvisionModel.createOntProperty(NS + "Addressee");
		
		//******ADDRESSEE Property
		Addressee.addDomain(ProvisionModel.getOntClass(NS + "Procedure"));
		Addressee.addDomain(ProvisionModel.getOntClass(NS + "Establishment"));
		Addressee.addDomain(ProvisionModel.getOntClass(NS + "Organization"));
		Addressee.addDomain(ProvisionModel.getOntClass(NS + "Power"));
		Addressee.addDomain(ProvisionModel.getOntClass(NS + "Liability"));
		Addressee.addDomain(ProvisionModel.getOntClass(NS + "Status"));
		Addressee.addDomain(ProvisionModel.getOntClass(NS + "Organization"));
		Addressee.addRange(OWL.Class);
		
	}
	
	private void buildCounterpart()
	{
		OntProperty Counterpart = ProvisionModel.createOntProperty(NS + "Counterpart");
		
		//********COUNTERPART Property
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Procedure"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Power"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Liability"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Right"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Duty"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Prohibition"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Permission"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Redress"));
		Counterpart.addDomain(ProvisionModel.getOntClass(NS + "Violation"));
		Counterpart.addRange(OWL.Class);

	}
	
	private void buildActivity()
	{
		OntProperty Activity = ProvisionModel.createOntProperty(NS + "Activity");

		//********ACTIVITY Property
		Activity.addDomain(ProvisionModel.getOntClass(NS + "Power"));
		Activity.addDomain(ProvisionModel.getOntClass(NS + "Liability"));
		Activity.addRange(OWL.Class);
		
		
	}
	
	private void buildObject()
	{
		OntProperty Object = ProvisionModel.createOntProperty(NS + "Object");
		
		
		//********OBJECT Property
		Object.addDomain(ProvisionModel.getOntClass(NS + "Procedure"));
		Object.addDomain(ProvisionModel.getOntClass(NS + "Power"));
		Object.addDomain(ProvisionModel.getOntClass(NS + "Liability"));
		Object.addDomain(ProvisionModel.getOntClass(NS + "Status"));
		Object.addDomain(ProvisionModel.getOntClass(NS + "Right"));
		Object.addDomain(ProvisionModel.getOntClass(NS + "Duty"));
		Object.addDomain(ProvisionModel.getOntClass(NS + "Prohibition"));
		Object.addDomain(ProvisionModel.getOntClass(NS + "Permission"));
		Object.addRange(OWL.Class);

		
	}
	
	private void buildBearer()
	{
		OntProperty Bearer = ProvisionModel.createOntProperty(NS + "Bearer");

		//********BEARER Property
		Bearer.addDomain(ProvisionModel.getOntClass(NS + "Right"));
		Bearer.addDomain(ProvisionModel.getOntClass(NS + "Duty"));
		Bearer.addDomain(ProvisionModel.getOntClass(NS + "Prohibition"));
		Bearer.addDomain(ProvisionModel.getOntClass(NS + "Permission"));
		Bearer.addDomain(ProvisionModel.getOntClass(NS + "Redress"));
		Bearer.addDomain(ProvisionModel.getOntClass(NS + "Violation"));
		Bearer.addRange(OWL.Class);
		
	}
	
	private void buildActionProp()
	{
		OntProperty ActionProp = ProvisionModel.createOntProperty(NS + "ActionProp");
		
		//********ACTION Property
		ActionProp.addDomain(ProvisionModel.getOntClass(NS + "Procedure"));
		ActionProp.addDomain(ProvisionModel.getOntClass(NS + "Right"));
		ActionProp.addDomain(ProvisionModel.getOntClass(NS + "Duty"));
		ActionProp.addDomain(ProvisionModel.getOntClass(NS + "Prohibition"));
		ActionProp.addDomain(ProvisionModel.getOntClass(NS + "Permission"));
		ActionProp.addDomain(ProvisionModel.getOntClass(NS + "Redress"));
		ActionProp.addDomain(ProvisionModel.getOntClass(NS + "Violation"));
		ActionProp.addRange(OWL.Class);
		
		
	}
	
	private void buildEffect()
	{
		OntProperty Effect = ProvisionModel.createOntProperty(NS + "Effect");
		
		//********EFFECT Property		
		Effect.addDomain(ProvisionModel.getOntClass(NS + "Redress"));
		Effect.addRange(OWL.Class);

		
	}
	
	private void buildPenalty()
	{
		OntProperty Penalty = ProvisionModel.createOntProperty(NS + "Penalty");
		
		//********PENALTY Property	
		Penalty.addDomain(ProvisionModel.getOntClass(NS + "Violation"));
		Penalty.addRange(OWL.Class);
		
	}
		
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
	
		
		ProvisionModelFactory ProvisionModelF = new ProvisionModelFactory(); 
		ProvisionModelF.setBaseURI();
		ProvisionModelF.setNS();
		
		OntModel ProvisionModel = ProvisionModelF.getProvisionModel();
		
		ExtendedIterator<OntClass> TopClasses = OntUtils.getTopClasses(ProvisionModel);
	    OntUtils.showClassesList(TopClasses);
		
		/*ExtendedIterator<OntClass> iter = ProvisionModel.listNamedClasses();
	    while(iter.hasNext()) {
	          OntClass ProvisionModelClass = (OntClass) iter.next();
	          System.out.println(ProvisionModelClass.getLocalName());
	      }
	    */
		ProvisionModel.write(System.out, "RDF/XML-ABBREV");
		
		File file = new File("ProvisionModel.rdf");
	    FileOutputStream f = new FileOutputStream(file);
		
	    ProvisionModel.write(f);
	    
	    
		/*ProvisionModelViewer ProvisionModelV = new ProvisionModelViewer();
		ProvisionModelV.setProvisionModel(ProvisionModel);
		ProvisionModelV.showProvisionModel();*/
		

		
	}

}
