package it.cnr.ittig.ProvisionModel;

import javax.swing.JFrame;
//import javax.swing.JPanel;

import com.hp.hpl.jena.ontology.OntModel;

import owl2prefuse.graph.*;
import owl2prefuse.tree.OWLTreeConverter;
import owl2prefuse.tree.TreeDisplay;
import owl2prefuse.tree.TreePanel;
import prefuse.data.*;


public class ProvisionModelViewer {
	
	OntModel ProvisionModel;
	TreePanel treePanel;

	public ProvisionModelViewer(){
		
	}
	
	void setProvisionModel(OntModel ProvisionModel){
		this.ProvisionModel = ProvisionModel;
	}
	
	
	public void showProvisionModel() {
		
		//GraphPanel graphPanel = createGraphPanel(ontModel);
		TreePanel treePanel = createTreePanel(this.ProvisionModel);
		
	    
		
		JFrame frame = new JFrame("Provision Model");
	    //JPanel panel = new JPanel();
		frame.setSize(256,256);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(treePanel);
		frame.pack();
		frame.setVisible(true);
		
	}
	

	public TreePanel createTreePanel(OntModel ontModel)
	{
	    // Step 1 - Create the Prefuse tree from a Jena OntModel.
	    OWLTreeConverter treeConverter = new OWLTreeConverter(ontModel);
	    Tree tree = treeConverter.getTree();
	    
	    // Step 2 - Create a tree display.
	    TreeDisplay treeDisp = new TreeDisplay(tree);
	    
	    // Step 3 - Create a panel for the tree display, showing the legend and the 
	    // widgets to control the orientation of the tree.
	    TreePanel treePanel = new TreePanel(treeDisp, true, true);
	    
	    return treePanel;
	    

	}
	
	
	/*
	 * Creates a JPanel containing a Prefuse graph, created from a Jena OntModel
	 */
	public GraphPanel createGraphPanel(OntModel p_ontModel)	{
		    // Step 1 - Create the directed Prefuse graph from an OWL file.
		    OWLGraphConverter graphConverter = new OWLGraphConverter(p_ontModel, true);
		    Graph graph = graphConverter.getGraph();
		    
		    // Step 2 - Create a graph display, using the graph distance filter.
		    GraphDisplay graphDisp = new GraphDisplay(graph, true);
		    
		    // Step 3 - Create a panel for the graph display, showing the legend and the 
		    // widget to control the number of hops of the graph distance filter.
		    GraphPanel graphPanel = new GraphPanel(graphDisp, true, true);

		    return graphPanel;
		}
		
}
