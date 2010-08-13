package it.cnr.ittig.ProvisionModel;


import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class OntUtils {
	
	
	//Search for top classes in an OntModel	
	public static ExtendedIterator<OntClass> getTopClasses(OntModel Model)
	{

			ExtendedIterator<OntClass> iterTop = Model.listHierarchyRootClasses();
		
		return iterTop;
	}
	
	
	/*
	 * Show a list of classes
	 */
	public static void showClassesList(ExtendedIterator<OntClass> classesSet)
	
	{
	    while(classesSet.hasNext()) {
	          OntClass Class = (OntClass) classesSet.next();
	          System.out.println(Class.getLocalName());
	      }

	}
	

	
	
/*	
	public OntModel getProvisionModelOriginal() {

		// Create an empty Provision Ontology Model
		OntModel ProvModel = ModelFactory.createOntologyModel();

		
		//TopClass
		OntClass Provision = ProvModel.createClass(NS + "Provision");
		
		//
		OntClass Rule = ProvModel.createClass(NS + "Rule");
		OntClass RuleOnRule = ProvModel.createClass(NS + "RuleOnRule");

		Provision.addSubClass(Rule);
		Provision.addSubClass(RuleOnRule);
		

		
	
		//Constitutive
		OntClass Definition = ProvModel.createClass(NS + "Definition");		
		OntClass Creation = ProvModel.createClass(NS + "Creation");
		OntClass Attribution = ProvModel.createClass(NS + "Attribution");
		
		Rule.addSubClass(Definition);
		Rule.addSubClass(Creation);
		Rule.addSubClass(Attribution);		
	
		
		
		//Regulative
		OntClass Action = ProvModel.createClass(NS + "Action");
		OntClass Remedy = ProvModel.createClass(NS + "Remedy");
		
		Rule.addSubClass(Action);
		Rule.addSubClass(Remedy);
		

		
		//*********CONSTITUTIVES SUB-CLASSES*************************************
	
		//Constitutive - "Definition" Sub-Class
		OntClass Term = ProvModel.createClass(NS + "Term");
		OntClass Procedure = ProvModel.createClass(NS + "Procedure");
		
		Definition.addSubClass(Term);	
		Definition.addSubClass(Procedure);
		
		//Constitutive - "Creation" Sub-Class
		OntClass Establishment = ProvModel.createClass(NS + "Establishment");
		OntClass Organization = ProvModel.createClass(NS + "Organization");
		
		Creation.addSubClass(Establishment);
		Creation.addSubClass(Organization);
		
		
		//Constitutive - "Attribution" Sub-Class
		OntClass Power = ProvModel.createClass(NS + "Power");
		OntClass Liability = ProvModel.createClass(NS + "Liability");
		OntClass Status = ProvModel.createClass(NS + "Status");
		
		Attribution.addSubClass(Power);
		Attribution.addSubClass(Liability);
		Attribution.addSubClass(Status);
		
		
		//***********REGULATIVE SUB-CLASSES***********************************	
		
		//Regulative - "Action" Sub-Class
		OntClass Right = ProvModel.createClass(NS + "Right");
		OntClass Duty = ProvModel.createClass(NS + "Duty");
		OntClass Prohibition = ProvModel.createClass(NS + "Prohibition");
		OntClass Permission = ProvModel.createClass(NS + "Permission");
		
		Action.addSubClass(Right);
		Action.addSubClass(Duty);
		Action.addSubClass(Prohibition);
		Action.addSubClass(Permission);
		
		
		//Regulative - "Remedy" Sub-Class		
		OntClass Redress = ProvModel.createClass(NS + "Redress");
		OntClass Violation = ProvModel.createClass(NS + "Violation");
		
		Remedy.addSubClass(Redress);
		Remedy.addSubClass(Violation);

		//******ARGUMENTS****************************************	
		
		OntProperty Definiendum = ProvModel.createOntProperty(NS + "Definiendum");
		OntProperty Definiens = ProvModel.createOntProperty(NS + "Definiens");
		OntProperty Addressee = ProvModel.createOntProperty(NS + "Addressee");
		OntProperty Counterpart = ProvModel.createOntProperty(NS + "Counterpart");
		OntProperty Activity = ProvModel.createOntProperty(NS + "Activity");
		OntProperty Object = ProvModel.createOntProperty(NS + "Object");
		OntProperty Bearer = ProvModel.createOntProperty(NS + "Bearer");
		OntProperty ActionProp = ProvModel.createOntProperty(NS + "ActionProp");
		OntProperty Effect = ProvModel.createOntProperty(NS + "Effect");
		OntProperty Penalty = ProvModel.createOntProperty(NS + "Penalty");
		
		
		
		//******DEFINIENDUM Property
		Definiendum.addDomain(Term); //Più efficiente di setDomain che deve prima eliminare eventuali Domain esistenti
		//Definiendum.addRange(RDFS.Literal);
		Definiendum.addRange(XSD.xstring);
		Definiendum.addRange(XSD.IDREF);

		//******DEFINIENS Property
		Definiens.addDomain(Term);
		Definiens.addRange(XSD.xstring);
		Definiens.addRange(XSD.IDREF);
		
		
		//******ADDRESSEE Property
		Addressee.addDomain(Procedure);
		Addressee.addDomain(Establishment);
		Addressee.addDomain(Organization);
		Addressee.addDomain(Power);
		Addressee.addDomain(Liability);
		Addressee.addDomain(Status);
		Addressee.addDomain(Organization);
		Addressee.addRange(OWL.Class);
		
		
		
		//********BEARER Property
			
		Bearer.addDomain(Right);
		Bearer.addDomain(Duty);
		Bearer.addDomain(Prohibition);
		Bearer.addDomain(Permission);
		Bearer.addDomain(Redress);
		Bearer.addDomain(Violation);
		Bearer.addRange(OWL.Class);
		
		//********COUNTERPART Property
		Counterpart.addDomain(Procedure);
		Counterpart.addDomain(Power);
		Counterpart.addDomain(Liability);
		Counterpart.addDomain(Right);
		Counterpart.addDomain(Duty);
		Counterpart.addDomain(Prohibition);
		Counterpart.addDomain(Permission);
		Counterpart.addDomain(Redress);
		Counterpart.addDomain(Violation);
		Counterpart.addRange(OWL.Class);
		
		//********ACTIVITY Property
		Activity.addDomain(Power);
		Activity.addDomain(Liability);
		Activity.addRange(OWL.Class);
		
		//********ACTION Property
		ActionProp.addDomain(Procedure);
		ActionProp.addDomain(Right);
		ActionProp.addDomain(Duty);
		ActionProp.addDomain(Prohibition);
		ActionProp.addDomain(Permission);
		ActionProp.addDomain(Redress);
		ActionProp.addDomain(Violation);
		ActionProp.addRange(OWL.Class);
		

		//********EFFECT Property		
		Effect.addDomain(Redress);
		Effect.addRange(OWL.Class);
		
		//********PENALTY Property	
		Penalty.addDomain(Violation);
		Penalty.addRange(OWL.Class);
		
		//********OBJECT Property
		Object.addDomain(Procedure);
		Object.addDomain(Power);
		Object.addDomain(Liability);
		Object.addDomain(Status);
		Object.addDomain(Right);
		Object.addDomain(Duty);
		Object.addDomain(Prohibition);
		Object.addDomain(Permission);
		Object.addRange(OWL.Class);
		

		
		return ProvModel;

	}
	
*/
	
}
