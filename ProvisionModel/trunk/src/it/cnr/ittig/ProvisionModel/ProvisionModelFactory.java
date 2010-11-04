package it.cnr.ittig.ProvisionModel;

//import com.hp.hpl.jena.graph.Graph;
//import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.UnionClass;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.ValidityReport;
//import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
//import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import java.io.*;
import java.util.Iterator;

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
	
	// URI declarations
	String baseURI = new String("http://provisions.org/model/1.0");
	
	//NS declaration
	String NS = new String(baseURI + "#");
	
	
	OntModel ProvisionModel = null;
	
	
	
	public OntModel getProvisionModel() {

		// Create an empty Provision Ontology Model
		ProvisionModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);

		
		//******BUILD TOP CLASSES
		buildProvisionTopClasses();
		
		//*****BUILD Constitutive
		buildConstitutiveProvisions();
		buildConstitutiveProvisionsSubClasses();
		
		//*****BUILD Regulative
		buildRegulativeProvisions();
		buildRegulativeProvisionsSubClasses();

		
		//******BUILD ARGUMENTS (Properties)****************************************	
		buildHasDefiniendum();
		buildHasDefiniens();
		buildHasAddressee();
		buildHasCounterpart();
		buildHasActivity();
		buildHasObject();
		buildHasBearer();
		buildHasActionProp();
		buildHasEffect();
		buildHasPenalty();
		
		
		//******BUILD IMPLICIT PROVISIONS *************
		buildImplicitRight();
		
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
		
		//Constitutives provisions creation
		OntClass Definition = ProvisionModel.createClass(NS + "Definition");		
		OntClass Creation = ProvisionModel.createClass(NS + "Creation");
		OntClass Attribution = ProvisionModel.createClass(NS + "Attribution");
		
		//Constitutive class creation and subclass provisions linking
	    OntClass Constitutive = ProvisionModel.createClass(NS + "Constitutive");
		Constitutive.addSubClass(Definition);
		Constitutive.addSubClass(Creation);
		Constitutive.addSubClass(Attribution);


		//Rule and Constitutive subclass linking 
		OntClass Rule = ProvisionModel.getOntClass(NS + "Rule");
		Rule.addSubClass(Constitutive);
		
		
	}
	
	private void buildRegulativeProvisions()
	{
		
		
		//Regulative provisions creation	
		OntClass Action = ProvisionModel.createClass(NS + "Action");
		OntClass Remedy = ProvisionModel.createClass(NS + "Remedy");

		//Regulative class creation and subclass provisions linking
		OntClass Regulative = ProvisionModel.createClass(NS + "Regulative");
		Regulative.addSubClass(Action);
		Regulative.addSubClass(Remedy);
		
		OntClass Rule = ProvisionModel.getOntClass(NS + "Rule");
		Rule.addSubClass(Regulative);		
		
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
	
	
	//Costruisce un ImplicitRight relativo ad una Duty e i relativi assiomi secondo Hohefeld
	private void buildImplicitRight()
	{
		//Implicit Regulative - "Action" Sub Class
		OntClass ImplicitRight = ProvisionModel.createClass(NS + "ImplicitRight");
		//OntClass Duty = ProvisionModel.getOntClass(NS + "Duty");
		//Duty.addEquivalentClass(ImplicitRight);
		
		
		//Proprietà di ImplicitRight
		OntProperty HasImplicitBearer = ProvisionModel.createOntProperty(NS + "hasImplicitBearer");
		OntProperty HasImplicitCounterpart = ProvisionModel.createOntProperty(NS + "hasImplicitCounterpart");
		
			
		HasImplicitBearer.addDomain(ImplicitRight);
		HasImplicitBearer.addRange(OWL.Class);
		
		HasImplicitCounterpart.addDomain(ImplicitRight);
		HasImplicitCounterpart.addRange(OWL.Class);
		
		
		//*******
		//***	Selezionare proprietà Bearer con dominio Duty (Restriction)
			
		//	
			
		//	Aggiungere proprietà equivalente ImplicitCounterpart
		//***************		
		HasImplicitCounterpart.addEquivalentProperty(ProvisionModel.getOntProperty(NS + "hasBearer"));
		HasImplicitBearer.addEquivalentProperty(ProvisionModel.getOntProperty(NS + "hasCounterpart"));
		
		//Duty ha come Right implicito ImplicitRight (FORSE E' SUFFICIENTE L'ASSIOMA DI EQUIVALENZA?)
		//OntClass Duty = ProvisionModel.getOntClass(NS + "Duty");
		//OntProperty hasImplicitRight = ProvisionModel.createOntProperty(NS + "hasImplicitRight");
		//hasImplicitRight.addDomain(Duty);
		//hasImplicitRight.addRange(ImplicitRight);
	
		
		//Axioms
		//OntClass Duty = ProvisionModel.getOntClass(NS + "Duty");
		//ImplicitRight.setEquivalentClass(Duty);
		//ImplicitCounterpart.setEquivalentProperty(Duty.);
		
		
	}

	
	
	private void buildHasDefiniendum()
	{
		OntProperty hasDefiniendum = ProvisionModel.createOntProperty(NS + "hasDefiniendum");

		//******DEFINIENDUM Property

		hasDefiniendum.addDomain(ProvisionModel.getOntClass(NS + "Term")); //Più efficiente di setDomain che deve prima eliminare eventuali Domain esistenti


		//Definiendum.addRange(RDFS.Literal);
		hasDefiniendum.addRange(XSD.xstring);
		hasDefiniendum.addRange(XSD.IDREF);
	}

	private void buildHasDefiniens()
	{
		OntProperty hasDefiniens = ProvisionModel.createOntProperty(NS + "hasDefiniens");
		
		//******DEFINIENS Property
		hasDefiniens.addDomain(ProvisionModel.getOntClass(NS + "Term"));
		hasDefiniens.addRange(XSD.xstring);
		hasDefiniens.addRange(XSD.IDREF);
	}
	
	private void buildHasAddressee()
	{
		OntProperty HasAddressee = ProvisionModel.createOntProperty(NS + "hasAddressee");
		
		//******ADDRESSEE Property
		
		
		UnionClass HasAddresseeDomain = ProvisionModel.createUnionClass(null, null);
		HasAddresseeDomain.addOperand(ProvisionModel.getOntClass(NS + "Procedure"));
		HasAddresseeDomain.addOperand(ProvisionModel.getOntClass(NS + "Establishment"));
		HasAddresseeDomain.addOperand(ProvisionModel.getOntClass(NS + "Organization"));
		HasAddresseeDomain.addOperand(ProvisionModel.getOntClass(NS + "Power"));
		HasAddresseeDomain.addOperand(ProvisionModel.getOntClass(NS + "Liability"));
		HasAddresseeDomain.addOperand(ProvisionModel.getOntClass(NS + "Status"));
		HasAddresseeDomain.addOperand(ProvisionModel.getOntClass(NS + "Organization"));
		
		HasAddressee.addDomain(HasAddresseeDomain);

/*		hasAddressee.addDomain(ProvisionModel.getOntClass(NS + "Procedure"));
		hasAddressee.addDomain(ProvisionModel.getOntClass(NS + "Establishment"));
		hasAddressee.addDomain(ProvisionModel.getOntClass(NS + "Organization"));
		hasAddressee.addDomain(ProvisionModel.getOntClass(NS + "Power"));
		hasAddressee.addDomain(ProvisionModel.getOntClass(NS + "Liability"));
		hasAddressee.addDomain(ProvisionModel.getOntClass(NS + "Status"));
		hasAddressee.addDomain(ProvisionModel.getOntClass(NS + "Organization"));
*/
		HasAddressee.addRange(OWL.Class);
		
	}
	
	private void buildHasCounterpart()
	{
		OntProperty HasCounterpart = ProvisionModel.createOntProperty(NS + "hasCounterpart");
		

		//Construction of a Domain as Union of Provision classes
		UnionClass HasCounterpartDomain = ProvisionModel.createUnionClass(null, null);
		
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Procedure"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Power"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Liability"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Right"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Duty"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Prohibition"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Permission"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Redress"));
		HasCounterpartDomain.addOperand(ProvisionModel.getOntClass(NS + "Violation"));
		
		//********hasCounterpart Property
		HasCounterpart.addDomain(HasCounterpartDomain);
		HasCounterpart.addRange(OWL.Class);

	}
	
	private void buildHasActivity()
	{
		OntProperty HasActivity = ProvisionModel.createOntProperty(NS + "hasActivity");

		//Construction of a Domain as Union of Provision classes
		UnionClass HasActivityDomain = ProvisionModel.createUnionClass(null, null);
		
		HasActivityDomain.addOperand(ProvisionModel.getOntClass(NS + "Power"));
		HasActivityDomain.addOperand(ProvisionModel.getOntClass(NS + "Liability"));
		
		//********hasActivity Property
		HasActivity.addDomain(HasActivityDomain);
		HasActivity.addRange(OWL.Class);
		
	}
	
	private void buildHasObject()
	{
		OntProperty HasObject = ProvisionModel.createOntProperty(NS + "hasObject");
		
		//Construction of a Domain as Union of Provision classes
		UnionClass HasObjectDomain = ProvisionModel.createUnionClass(null, null);
				
		//********OBJECT Property
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Procedure"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Power"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Liability"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Status"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Right"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Duty"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Prohibition"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Permission"));
		
		HasObject.addDomain(HasObjectDomain);
		HasObject.addRange(OWL.Class);

		
	}
	
	private void buildHasBearer()
	{
		OntProperty HasBearer = ProvisionModel.createOntProperty(NS + "hasBearer");

		//Construction of a Domain as Union of Provision classes
		UnionClass HasObjectDomain = ProvisionModel.createUnionClass(null, null);
		
		//********hasBearer Property
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Right"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Duty"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Prohibition"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Permission"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Redress"));
		HasObjectDomain.addOperand(ProvisionModel.getOntClass(NS + "Violation"));
		
		HasBearer.addDomain(HasObjectDomain);
		HasBearer.addRange(OWL.Class);
		
	}
	
	private void buildHasActionProp()
	{
		OntProperty HasActionProp = ProvisionModel.createOntProperty(NS + "hasActionProp");
		
		//Construction of a Domain as Union of Provision classes
		UnionClass HasActionPropDomain = ProvisionModel.createUnionClass(null, null);
		
		//********hasActionProp Property
		HasActionPropDomain.addOperand(ProvisionModel.getOntClass(NS + "Procedure"));
		HasActionPropDomain.addOperand(ProvisionModel.getOntClass(NS + "Right"));
		HasActionPropDomain.addOperand(ProvisionModel.getOntClass(NS + "Duty"));
		HasActionPropDomain.addOperand(ProvisionModel.getOntClass(NS + "Prohibition"));
		HasActionPropDomain.addOperand(ProvisionModel.getOntClass(NS + "Permission"));
		HasActionPropDomain.addOperand(ProvisionModel.getOntClass(NS + "Redress"));
		HasActionPropDomain.addOperand(ProvisionModel.getOntClass(NS + "Violation"));
		
		HasActionProp.addDomain(HasActionPropDomain);
		HasActionProp.addRange(OWL.Class);
		
		
	}
	
	private void buildHasEffect()
	{
		OntProperty HasEffect = ProvisionModel.createOntProperty(NS + "hasEffect");

		
		//********EFFECT Property		
		HasEffect.addDomain(ProvisionModel.getOntClass(NS + "Redress"));
		HasEffect.addRange(OWL.Class);

		
	}
	
	private void buildHasPenalty()
	{
		OntProperty HasPenalty = ProvisionModel.createOntProperty(NS + "hasPenalty");
		
		//********PENALTY Property	
		HasPenalty.addDomain(ProvisionModel.getOntClass(NS + "Violation"));
		HasPenalty.addRange(OWL.Class);
		
	}
		
	
    public static void printIterator(Iterator<?> i, String header) {
        System.out.println(header);
        for(int c = 0; c < header.length(); c++)
            System.out.print("=");
        System.out.println();
        
        if(i.hasNext()) {
	        while (i.hasNext()) 
	            System.out.println( i.next() );
        }       
        else
            System.out.println("<EMPTY>");
        
        System.out.println();
    }

	
	
	public static void main(String[] args) throws FileNotFoundException {
	
		
		ProvisionModelFactory ProvisionModelF = new ProvisionModelFactory(); 

		
		OntModel ProvisionModel = ProvisionModelF.getProvisionModel();
		
//		ExtendedIterator<OntClass> TopClasses = OntUtils.getTopClasses(ProvisionModel);
//	    OntUtils.showClassesList(TopClasses);
		
		/*ExtendedIterator<OntClass> iter = ProvisionModel.listNamedClasses();
	    while(iter.hasNext()) {
	          OntClass ProvisionModelClass = (OntClass) iter.next();
	          System.out.println(ProvisionModelClass.getLocalName());
	      }
	    */
		ProvisionModel.write(System.out, "RDF/XML-ABBREV");
		
		File file = new File("ProvisionModel.owl");
	    FileOutputStream f = new FileOutputStream(file);
		
	    ProvisionModel.write(f, "RDF/XML-ABBREV");
	    
	    
	    ProvisionModelReasoner ProvisionModelR = new ProvisionModelReasoner();
	    ProvisionModelR.setProvisionModel(ProvisionModel);
	    OntModel InferredProvisionModel = ProvisionModelR.getInferredProvisionModel();
		
        // print validation report
        ValidityReport report = InferredProvisionModel.validate();
        printIterator( report.getReports(), "Validation Results" );
	    
		/*ProvisionModelViewer ProvisionModelV = new ProvisionModelViewer();
		ProvisionModelV.setProvisionModel(ProvisionModel);
		ProvisionModelV.showProvisionModel();*/
		

		
	}

}
