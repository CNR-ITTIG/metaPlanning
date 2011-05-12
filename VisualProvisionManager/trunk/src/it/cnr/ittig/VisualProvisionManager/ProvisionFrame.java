package it.cnr.ittig.VisualProvisionManager;

import javax.swing.JFrame;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.algebra.Transformer;

import it.cnr.ittig.ProvisionModel.OntUtils;
import it.cnr.ittig.VisualProvisionManager.Provision.Provision;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.jdom.*; 
import org.jdom.output.*;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

public class ProvisionFrame extends JFrame{
	private JPanel panel;
	private OntModel model; //TOGLIERE? PROBLEMA CON SOTTOCLASSI (USA TRANSITIVITA') E PROPRIETA' DELLE CLASSI(PROPRIETA' CONDIVISE NON RESTITUITE)
	private OntModel modelOutput;	//MODEL DI OUTPUT DOVE VERRANNO SALVATE LE ISTANZE CREATE
	private String savedPath;//UTILE PER VEDERE SE UN DATO FILE E' GIA' STATO SALVATO ED IN CHE PATH 
	private Document document=null;
	private JTextArea text;
	private JTree provisionTree;//ALBERO DELLE DISPOSIONI
	private JTree formalTree; //ALBERO RIGUARDANTE IL PROFILO FORMALE
	private int numberOfParagraph=0; //CONTA IL NUMERO DI PARAGRAFI PRESENTI NELL'ALBERO FORMALE
	private int numberOfArticle=0; //CONTA IL NUMERO DI ARTICOLI PRESENTI NELL'ALBERO FORMALE
	private Vector <TreePath> nodeExpanded=new Vector<TreePath>(); //CONTIENE TUTTI I NODI ESPANSI
	private DefaultMutableTreeNode radice=new DefaultMutableTreeNode("");
	private boolean modified=false; //indica se il lavoro ha subito modifiche dall'ultimo salvataggio
	private boolean init=false; //indica se il documento ha subito una qualsiasi operazione o se non è mai stato usato. Utile per quando
		//si apre un nuovo documento con il documento iniziale mai modificato
	private String selectedArticle=new String(""); //INDICA L'ARTICOLO IN CUI VOGLIAMO INSERIRE I RISULTATI DI UNA NUOVA RICERCA
	private Vector <Provision> provisions=new Vector<Provision>();//VETTORE DELLE DISPOSIZIONI
	private Vector <String> articles=new Vector<String>();//VETTORE DEGLI ARTICOLI (SERVE SOLO AD ELENCARLI)
	private int range=551; // INDICA QUANTE DISPOSIZIONI DI OGNI TIPO SONO GESTIBILI DAL PROGRAMMA (range-1)
	private Vector <String> usedID=new Vector<String>(); //TIENE TRACCIA DEGLI ID USATI PER LE DISPOSIZIONI, MEGLIO COME VECTOR?
	//private Vector <OntClass> rootVector;//CONTIENE LE RADICI DELL'ALBERO DELLE DISPOSIZIONI
	
	
	public static void main(String[] args){
		Runnable runner=new Runnable(){
		public void run(){
		final ProvisionFrame frame=new ProvisionFrame();
		frame.setTitle("Applicazione");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.text=new JTextArea(30,30);
		frame.text.setLineWrap(true);
		frame.text.setWrapStyleWord(true);
		frame.loadModel();
		frame.provisionTree=new JTree(frame.radice);
		//riscrittura del file su area di testo (togliere, per ora controllo almeno gli errori)
		ByteArrayOutputStream bout=null;
		bout=new ByteArrayOutputStream();
     	frame.model.write(bout);
     	frame.text.setText(new String(bout.toByteArray()));
		frame.initializeDimension();
		frame.setVisible(true);//METTO QUI IL SETvISIBLE PERCHE' SE LO METTO DOPO IL CREATEMAINPANEL SBAGLIA NEL CREARE LA SPLITPANE
		//IN QUANTO, NON ESSENDO ANCORA RESO VISIBILE IL FRAME, ESSO HA DIMENSIONI (0,0)
		frame.createMenu(frame);
		frame.createMainPanel(frame);
		}	
	};
	EventQueue.invokeLater(runner);
	}
	
	
	//CARICA IL MODELLO CONTENUTO NEL PROVISIONMODEL
	private void loadModel(){ 
		InputStream in = FileManager.get().open("ProvisionModel.owl");
		if (in == null) {
		    	throw new IllegalArgumentException("File non trovato");		                                 
		}
		model=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,null);
		model.read(in,"RDF/XML-ABBREV");
		modelOutput=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
		modelOutput.addSubModel(model);//AGGIUNGO AL MODELLO DI OUTPUT LE DEFINIZIONI DEL MODELLO BASE
		modelOutput.setNsPrefix("provision", "http://provisions.org/model/1.0#");
		ExtendedIterator <OntModel> r=modelOutput.listSubModels();
		if(!r.hasNext())System.out.println("No import");//STAMPO SE L'IMPORT è ANDATO A BUON FINE
		while (r.hasNext()){
		System.out.println("Importo"+r.next());
		}
	}
	
	private void initializeDimension(){
		//imposto le dimensioni della finesta basate sulle dimensioni dello schermo/2
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Dimension dimension;
		if(toolkit.isFrameStateSupported(JFrame.MAXIMIZED_BOTH)){//IMPOSTA LE DIMENSIONI TOGLIENDO IN AUTOMATICO LA WINDOWS TOOLBAR
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			dimension=this.getSize();
			provisionTree.setPreferredSize(dimension);
		}else{//SE NON E' SUPPORTATO IL METODO CHE RILEVA LA WINDOWS TOOLBAR
			Dimension screen=toolkit.getScreenSize();
			int larghezza=(int) screen.getWidth()/2;
			int altezza=(int) screen.getHeight()/2;
			dimension=new Dimension(larghezza-30,altezza*2);
			setSize(larghezza*2,altezza*2-30);// TOLGO 30 PER LA TOOLBAR (FATTO IN MANIERA ARBITRARIA,CERCA DI CAMBIARLA)
			setLocation(0,0);
			provisionTree.setPreferredSize(dimension);
		}
	}
	
	private void insertMenu(final ProvisionFrame frame,OntClass ont, JMenu menuOnt){
		ExtendedIterator<OntClass> iter1=ont.listSubClasses();
		OntClass figlio;
		while(iter1.hasNext()){
			figlio=iter1.next(); 
			if(figlio.hasSubClass()&&!(getProvisionType(figlio).equals("Right")||getProvisionType(figlio).equals("Duty"))){
				JMenu menuFiglio=new JMenu(getProvisionType(figlio));
				menuOnt.add(menuFiglio);
				insertMenu(frame,figlio,menuFiglio);				
			}else{
				if(getProvisionType(figlio).startsWith("Implicit")||getProvisionType(figlio).startsWith("Explicit")){
					//NON INSERIRE
				}
				final OntClass figlio1=figlio;//UTILE PERCHE' IL LISTENER SUCCESSIVO DEVE LAVORARE SU UNA VARIABILE FINAL
				JMenuItem menuItemFiglio=new JMenuItem(getProvisionType(figlio));
				//INSERISCO IL NODO DEL TIPO DI DISPOSIZIONE NELL'ALBERO
				DefaultMutableTreeNode child=new DefaultMutableTreeNode(getProvisionType(figlio));
				menuOnt.add(menuItemFiglio);
				radice.add(child);// AGGIUNGO IL NODO DELLA DISPOSIZIONE COME FIGLIO DELLA RADICE
				//AGGIUNGO UN NODO VUOTO COME FIGLIO DEL TIPO DI DISPOSIZIONE
				child.add(new DefaultMutableTreeNode("Blank"));
				menuItemFiglio.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						new InsertWindow(frame,model,figlio1); //SE NON TORNA MODELBASE
					}
				});
			}
		}
	}
	
	
	public void createMenu(final ProvisionFrame frame){
		JMenuBar menubar= new JMenuBar();
		
		//Creo le voci dei menù e relativi mnemonici
		JMenu menu=new JMenu("File");
		menu.setMnemonic('f');
		JMenu menu1=new JMenu("Edit");
		menu1.setMnemonic('e');
		//creo i MenuItem
		JMenuItem item=new JMenuItem("Nuovo",KeyEvent.VK_N);
		KeyStroke ctrlShiftNKeyStroke = KeyStroke.getKeyStroke("control shift N");
		item.setAccelerator(ctrlShiftNKeyStroke);
		JMenuItem item1 =new JMenuItem("Apri",KeyEvent.VK_A);
		JMenuItem item2= new JMenuItem("Salva",KeyEvent.VK_S);
		JMenuItem item3= new JMenuItem("Salva con nome",KeyEvent.VK_L);
		JMenuItem item4= new JMenuItem("Esci",KeyEvent.VK_E);
		KeyStroke ctrlEKeyStroke = KeyStroke.getKeyStroke("control E");
		item4.setAccelerator(ctrlEKeyStroke);
		Action actions[] = text.getActions();
		Action cutAction = FrameUtil.findAction(actions, DefaultEditorKit.cutAction);
		Action copyAction = FrameUtil.findAction(actions, DefaultEditorKit.copyAction);
		Action pasteAction = FrameUtil.findAction(actions, DefaultEditorKit.pasteAction);
		JMenuItem itemA=new JMenuItem(copyAction);
		KeyStroke ctrlCKeyStroke = KeyStroke.getKeyStroke("control C");
		itemA.setAccelerator(ctrlCKeyStroke);
		itemA.setText("Copia");
		itemA.setMnemonic(KeyEvent.VK_C);
		JMenuItem itemA1=new JMenuItem(cutAction);
		KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control X");
		itemA1.setAccelerator(ctrlXKeyStroke);
		itemA1.setText("Taglia");
		itemA1.setMnemonic(KeyEvent.VK_T);
		JMenuItem itemA2=new JMenuItem(pasteAction);
		KeyStroke ctrlVKeyStroke = KeyStroke.getKeyStroke("control V");
		itemA2.setAccelerator(ctrlVKeyStroke);
		itemA2.setText("Incolla");
		itemA2.setMnemonic(KeyEvent.VK_I);
		JMenu insertMenu=new JMenu("Inserisci"); //menu per l'inserimento dei vari tipi di disposizioni
		insertMenu.setMnemonic(KeyEvent.VK_N);
		JMenuItem itemCerca=new JMenuItem("Cerca");
		itemCerca.setMnemonic(KeyEvent.VK_E);
		JMenuItem itemScrivi=new JMenuItem("Scrivi XML");
		itemScrivi.setMnemonic(KeyEvent.VK_S);
		menu.add(item);
		menu.add(item1);
		menu.addSeparator();
		menu.add(item2);
		menu.add(item3);
		menu.addSeparator();
		menu.add(item4);
		menu1.add(itemA);
		menu1.add(itemA1);
		menu1.add(itemA2);
		menu1.addSeparator();
		menu1.add(insertMenu);
		menu1.addSeparator();
		menu1.add(itemCerca);
		menu1.add(itemScrivi);
		
		//PROCEDO CON LA CREAZIONE DEL MENU DI INSERIMENTO DELLE DISPOSIZIONI
		OntClass ont;
		ExtendedIterator <OntClass>  iter=OntUtils.getTopClasses(model);//CERCO LA CLASSE DI GERARCHIA PIU' ALTA(FUNZIONA SE NE ESISTE SOLO 1 INSERIMENTO NELL'ALBERO)
		while(iter.hasNext()){
			ont=iter.next();
			/*if(ont.isUnionClass()){
				//NON INSERIRE
			}
			else if(getProvisionType(ont).equals("ImplicitRight")){
				//NON INSERIRE
				}*/
			//insertMenu(frame, ont, insertMenu );
			/*menus[count]=new JMenu(getProvisionType(ont));
			insertMenu.add(menus[count]);
			ExtendedIterator<OntClass> iter1=ont.listSubClasses();
			insertMenu(frame,ont,menus[count]);*/
			//SONO INUTILI, POSSONO SERVIRE SE SI CAMBIA IL MODELLO
				/*if(ont.hasSubClass()){
					//rootVector.add(ont);
					//DefaultMutableTreeNode root=new DefaultMutableTreeNode(ont);
					if(getProvisionType(ont).equals("Duty")||getProvisionType(ont).equals("Right")){
						JMenuItem menuItemRoot=new JMenuItem(getProvisionType(ont));
						insertMenu.add(menuItemRoot);
					}else{
						JMenu menuRoot=new JMenu(getProvisionType(ont));
						radice.setUserObject(getProvisionType(ont));//SE VI SONO PIU' CLASSI DI PIU ALTO LIVELLO POTREBBE NON FARE
						reloadTree();
						insertMenu.add(menuRoot);
						insertMenu(frame,ont,menuRoot);
					}
				}
				else{
					if(getProvisionType(ont).startsWith("Implicit")||getProvisionType(ont).startsWith("Explicit")){
						//NON INSERIRE
					}
					else{		
						JMenuItem menuItemRoot=new JMenuItem(getProvisionType(ont));
						insertMenu.add(menuItemRoot);
					}
				}*/
			if(ont.hasSubClass()){
				JMenu menuRoot=new JMenu(getProvisionType(ont));
				radice.setUserObject(getProvisionType(ont));//SE VI SONO PIU' CLASSI DI PIU ALTO LIVELLO POTREBBE NON FARE
				reloadTree();
				insertMenu.add(menuRoot);
				insertMenu(frame,ont,menuRoot);
			}
			else{
				if(getProvisionType(ont).startsWith("Implicit")||getProvisionType(ont).startsWith("Explicit")){
					//NON INSERIRE
				}
				else{		
					JMenuItem menuItemRoot=new JMenuItem(getProvisionType(ont));
					insertMenu.add(menuItemRoot);
				}
			}
			}
		//LISTENER PER LA CHIUSURA DELL'"FORZATA" DELL'APPLICAZIONE (TRAMITE X ALTO A DX O TRAMITE SISTEMA) 
		addWindowListener(new WindowAdapter() {
			
            public void windowClosing(WindowEvent we) {
            	int conferma = JOptionPane.showConfirmDialog(frame,"Sei sicuro di voler uscire?","Termina l'applicazione",JOptionPane.YES_NO_OPTION);
				if(conferma==0){
					if(modified)
					{
						conferma=JOptionPane.showConfirmDialog(frame,"Vuoi salvare il lavoro?","Salva",JOptionPane.YES_NO_OPTION);
						if(conferma==0)
						{
							FrameUtil.saveFileWithName( frame,  frame.model,  frame.modified,  frame.savedPath);
						}
					}
					System.exit(0);	
				}
        }
    });

		
		ActionListener actionExit=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int conferma = JOptionPane.showConfirmDialog(frame,"Sei sicuro di voler uscire?","Termina l'applicazione",JOptionPane.YES_NO_OPTION);
				if(conferma==0){
					if(frame.modified)
					{
						conferma=JOptionPane.showConfirmDialog(frame,"Vuoi salvare il lavoro?","Salva",JOptionPane.YES_NO_OPTION);
						if(conferma==0)
						{
							FrameUtil.saveFileWithName( frame,  frame.model,  frame.modified,  frame.savedPath);
						}
						else{ //SE PIGIO ANNULLA QUANDO MI CHIEDE DI SALVARE IL FILE NON ESCO
						}
					}	
					System.exit(0);
				}
			}
		};
		item4.addActionListener(actionExit);
		
		//Listener per creare un nuovo file
		ActionListener actionNew=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int conferma=JOptionPane.showConfirmDialog(frame,"Creare un nuovo documento?","Nuovo documento",JOptionPane.YES_NO_OPTION);
				if(conferma==0)
				{
					if(!init){
						// SE IL DOCUMENTO NON HA SUBITO ALCUN TOCCO NON FA NIENTE
					}
					else {// SE IL DOCUMENTO HA SUBITO MODIFICHE
						if(modified)//SE NON E' STATO MODIFICATO DALL'ULTIMO SALVATAGGIO
						{
							conferma=JOptionPane.showConfirmDialog(frame,"Vuoi salvare il lavoro?","Salva",JOptionPane.YES_NO_OPTION);
						}
						if(conferma==0)//SE VOGLIO SALVARE IL LAVORO
						{
							FrameUtil.saveFileWithName( frame,  model,  modified,  savedPath);
						}
					}
				//System.out.println(text.getText());
				document=null; //PER ORA INUTILE
				text.setText("");
				init=false;
				modified=false;
				savedPath=null;
				modelOutput=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
				modelOutput.addSubModel(model);//AGGIUNGO AL MODELLO DI OUTPUT LE DEFINIZIONI DEL MODELLO BASE
				modelOutput.setNsPrefix("provision", "http://provisions.org/model/1.0#");
				deleteFormalTree();
				deleteProvisionTree();
				provisions=new Vector<Provision>();
				articles=new Vector<String>();
				usedID=new Vector<String>();
				numberOfArticle=numberOfParagraph=0;
				}
			}
		};
		
		item.addActionListener(actionNew);
		//Listener per aprire un nuovo File //TODO chiedere di salvare prima gestire il file aperto far partire il file chooser da una directory particolare
		ActionListener actionOpen=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int conferma=JOptionPane.showConfirmDialog(frame,"Aprire un nuovo documento?","Apertura documento",JOptionPane.YES_NO_OPTION);
				if(conferma==0)
				{
					if(!init){
						// SE IL DOCUMENTO NON HA SUBITO ALCUN TOCCO NON FA NIENTE
					}
					else {// SE IL DOCUMENTO HA SUBITO MODIFICHE
						if(modified)//SE NON E' STATO MODIFICATO DALL'ULTIMO SALVATAGGIO
						{
							conferma=JOptionPane.showConfirmDialog(frame,"Vuoi salvare il lavoro?","Salva",JOptionPane.YES_NO_OPTION);
						}
						if(conferma==0)//SE VOGLIO SALVARE IL LAVORO
						{
							FrameUtil.saveFileWithName( frame,  model,  modified,  savedPath);
						}
					}
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new RDFFileFilter());
				int status=chooser.showOpenDialog(frame);
				if (status == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					String path=selectedFile.getPath();
					InputStream in = FileManager.get().open(path);
			        if (in == null) {
			        	JOptionPane.showMessageDialog(frame,"Errore nell'apertura del file", "Errore", JOptionPane.ERROR_MESSAGE);
			            throw new IllegalArgumentException( "File: " + path + " not found");
			        }else{
			        	// lettura del file aperto
			        	text.setText("");
			        	savedPath=path;
			        	ByteArrayOutputStream bout=new ByteArrayOutputStream();
			        	text.setText(new String(bout.toByteArray()));
						init=false;
						modified=false;
						int conta=0;
						ExtendedIterator <Individual> iter=modelOutput.listIndividuals();
						Individual ind=null;
						while(iter.hasNext()){
							ind=iter.next();
							ind.remove();
							conta++;
						}
						//modelOutput=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
						//modelOutput.addSubModel(model);
						modelOutput.read(in,null);//MODELOUTPUT CONTIENE GLI INDIVIDUI SALVATI NEL FILE APERTO
						loadTreeFromFile();//AGGIORNO L'ALBERO FORMALE CON TALI INDIVIDUI
			        }
				}if(status == JFileChooser.ERROR_OPTION){
				// TO DO GESTIRE LA SITUAZIONE DI ERRORE
				}
				else{
					//SCELGO ANNULLA, NON FARE NIENTE
				}
					
				}
			}
		};
	
		item1.addActionListener(actionOpen);
		//Listener per salvare un File con nome//TODO far partire il file chooser da una directory particolare
		ActionListener actionSaveWithName=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FrameUtil.saveFileWithName( frame,  modelOutput,  modified,  savedPath);
			}
		};
		item3.addActionListener(actionSaveWithName);
		//Listener per salvare un File//TODO far partire il file chooser da una directory particolare
		ActionListener actionSave=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FrameUtil.saveFile(frame,  modelOutput,  modified,  savedPath);
			}
		};
		item2.addActionListener(actionSave); 
		
		ActionListener findListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new searchWindow(model,frame);
			}
		};
		itemCerca.addActionListener(findListener);
		
		ActionListener actionWriteXML=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String path=null;
				JFileChooser chooser=new JFileChooser();
				chooser.setFileFilter(new XMLFileFilter());
				File selectedFile;
				int status=chooser.showSaveDialog(frame);
				if (status == JFileChooser.APPROVE_OPTION) {
					selectedFile = chooser.getSelectedFile();
					path=selectedFile.getPath();
				}
				if(path!=null){
					if(!path.endsWith(".xml")){
						path=path+".xml";
					}
					writeXML(path);	
				}
			}
		};
		itemScrivi.addActionListener(actionWriteXML);
			
		//creo la toolbar
		JToolBar toolbar=new JToolBar("Toolbar");
		JSeparator toolBarSeparator = new JToolBar.Separator(new Dimension(10,10));
		toolbar.add(toolBarSeparator,JToolBar.HORIZONTAL);
		ImageIcon imgNew=new ImageIcon("img/new.png");
		JButton newButton=new JButton(imgNew);
		toolbar.add(newButton);
		newButton.addActionListener(actionNew);
		newButton.setToolTipText("Nuovo");
		newButton.setBorderPainted(true);
		ImageIcon imgOpen=new ImageIcon("img/open.png");
		JButton openButton=new JButton(imgOpen);
		openButton.addActionListener(actionOpen);
		openButton.setToolTipText("Apri");
		openButton.setBorderPainted(true);
		toolbar.add(openButton);
		ImageIcon imgSave=new ImageIcon("img/save1.png");
		JButton saveButton=new JButton(imgSave);
		saveButton.addActionListener(actionSave);
		saveButton.setToolTipText("Salva");
		saveButton.setBorderPainted(true);
		toolbar.add(saveButton);
		ImageIcon imgCopy=new ImageIcon("img/copy.png");
		JButton copyButton=new JButton(imgCopy);
		copyButton.addActionListener(copyAction);
		copyButton.setToolTipText("Copia");
		copyButton.setBorderPainted(true);
		toolbar.add(copyButton);
		ImageIcon imgCut=new ImageIcon("img/cut.png");
		JButton cutButton=new JButton(imgCut);
		cutButton.addActionListener(cutAction);
		cutButton.setToolTipText("Taglia");
		cutButton.setBorderPainted(true);
		toolbar.add(cutButton);
		ImageIcon imgPaste=new ImageIcon("img/paste.png");
		JButton pasteButton=new JButton(imgPaste);
		pasteButton.addActionListener(pasteAction);
		pasteButton.setToolTipText("Incolla");
		pasteButton.setBorderPainted(true);
		toolbar.add(pasteButton);
		ImageIcon imgFind=new ImageIcon("img/find.png");
		JButton findButton=new JButton(imgFind);
		findButton.addActionListener(findListener);
		findButton.setToolTipText("Cerca Disposizioni");
		findButton.setBorderPainted(true);
		toolbar.add(findButton);
		ImageIcon imgXML=new ImageIcon("img/xml.png");
		JButton xmlButton=new JButton(imgXML);
		xmlButton.setToolTipText("Produci scheletro XML");
		xmlButton.addActionListener(actionWriteXML);
		xmlButton.setBorderPainted(true);
		toolbar.add(xmlButton);
		menubar.add(menu);
		menubar.add(menu1);
		frame.setJMenuBar(menubar);
		frame.add(toolbar,BorderLayout.NORTH);
	}
	
	public OntModel getOutputModel(){
		return modelOutput;
	}
	private void deleteFormalTree(){
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)formalTree.getModel().getRoot();
			rootNode.removeAllChildren();
			((DefaultTreeModel)formalTree.getModel()).reload();
			//DefaultMutableTreeNode articles=new DefaultMutableTreeNode("Articoli");
			//DefaultTreeModel m= (DefaultTreeModel)formalTree.getModel();
			//m.insertNodeInto(articles, rootNode, rootNode.getChildCount());
	}
	
	/*private void insertTypeInProvisionTree(OntClass ont){
		ExtendedIterator<OntClass> iter1=ont.listSubClasses();
		OntClass figlio;
		while(iter1.hasNext()){
			figlio=iter1.next(); 
			if(figlio.hasSubClass()&&!(getProvisionType(figlio).equals("Right")||getProvisionType(figlio).equals("Duty"))){
				insertTypeInProvisionTree(figlio);				
			}else{
				if(getProvisionType(figlio).startsWith("Implicit")||getProvisionType(figlio).startsWith("Explicit")){
					//NON INSERIRE
				}
				//INSERISCO IL NODO DEL TIPO DI DISPOSIZIONE NELL'ALBERO
				DefaultMutableTreeNode child=new DefaultMutableTreeNode(getProvisionType(figlio));
				radice.add(child);// AGGIUNGO IL NODO DELLA DISPOSIZIONE COME FIGLIO DELLA RADICE
				//AGGIUNGO UN NODO VUOTO COME FIGLIO DEL TIPO DI DISPOSIZIONE
				child.add(new DefaultMutableTreeNode("Blank"));
			}
		}
		
	}*/
	
	/*private void deleteProvisionTree(){
		OntClass ont;
		ExtendedIterator <OntClass>  iter=OntUtils.getTopClasses(model);//CERCO LA CLASSE DI GERARCHIA PIU' ALTA(FUNZIONA SE NE ESISTE SOLO 1 INSERIMENTO NELL'ALBERO)
		while(iter.hasNext()){
			ont=iter.next();
			if(ont.hasSubClass()){
				radice.setUserObject(getProvisionType(ont));//SE VI SONO PIU' CLASSI DI PIU ALTO LIVELLO POTREBBE NON FARE
				reloadTree();
				insertTypeInProvisionTree(ont);
			}
			
		}
	}*/
	private void deleteProvisionTree(){
		DefaultTreeModel m= (DefaultTreeModel)provisionTree.getModel();
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)m.getRoot();
		DefaultMutableTreeNode childNode, nephewNode;
		for(int i=0;i<rootNode.getChildCount();i++){			
			childNode=(DefaultMutableTreeNode) rootNode.getChildAt(i);//QUESTI NODI SONO I TIPI DI DISPOSIZIONI
			/*if(childNode.getChildAt(0).toString().equals("Blank")){
				// NON CI SONO DISPOSIZIONI DI QUEL TIPO, SALTA IL CONTROLLO
			}else{*/
				for(int j=0;j<childNode.getChildCount();j++){//		QUESTI SONO NODI RELATIVI ALLE DISPOSIZIONI, LI ELIMINO
					nephewNode=(DefaultMutableTreeNode) childNode.getChildAt(j);
					m.removeNodeFromParent(nephewNode);
				}
				nephewNode=new DefaultMutableTreeNode("Blank");//RIAGGIUNGO IL NODO BLANK
				m.insertNodeInto(nephewNode,childNode,childNode.getChildCount());
			//}
			
		}
	}
	
	private void loadTreeFromFile(){
		deleteFormalTree();//RIDISEGNO ALBERO FORMALE
		deleteProvisionTree();//RIDISEGNO ALBERO DELLE DISPOSIZIONI
		numberOfArticle=numberOfParagraph=0;
		int numOfProvision=provisions.size();
		Provision temp;
		String provId=null, type=null;
		Individual ind=null;//INDIVIDUO DA RIMUOVERE 
		if(numOfProvision>0){
			for(int i=numOfProvision-1;i>=0;i--){
				temp=provisions.get(i);
				provId=temp.getID();
				type=temp.getType();
				System.out.println("rimozione"+provId);
				//TODO TOGLI LA STRING HTTP....SOTTO E METTI L'NS
				ExtendedIterator <Individual> iterIndividual=modelOutput.listIndividuals();
				while(iterIndividual.hasNext()){
					ind=(Individual)iterIndividual.next();
					if(ind.getLocalName().equals(temp.getID())){//rimuovo eventuali statement che riguardano l'elemento
						  ind.remove(); 
						  searchAndDeleteFromProvisionTree(provId,type);
					}
				}
			}
		}
		ExtendedIterator <Individual> iter=modelOutput.listIndividuals();
		//modelOutput.removeAll();//ELIMINO TUTTI GLI INDIVIDUI DAL MODELLO, LI RICREO DA ZERO
		Individual toInsert=null;
		OntClass ont=null;
		String []properties=null, param=null; //ARRAY CONTENENTI ARGOMENTI E VALORI ASSOCIATI ALL'INDIVIDUO
		int numOfProp=0;//CONTA IL NUMERO DI ARGOMENTI
		int counter=0; //UTILE PER INSERIRE ARGOMENTI E VALORI NEI RISPETTIVI ARRAY
		OntProperty currentProperty=null;
		while(iter.hasNext()){
			toInsert=iter.next();
			ont=toInsert.getOntClass();//RECUPERO LA CLASSE DELL'INDIVIDUO
			System.out.println(toInsert.toString());
			ExtendedIterator <OntProperty> prop=ont.listDeclaredProperties();//RECUPERO IL NUMERO DI PROPRIETà DELL'INDIVIDUO
			while(prop.hasNext()){
				prop.next();
				numOfProp++;
			}
			//System.out.println("Ho proprietà" +numOfProp);
			properties=new String[numOfProp+1];//AGGIUNGO 1 CAUSA IL TESTO
			param=new String[numOfProp+1];
			prop=ont.listDeclaredProperties();
			while(prop.hasNext()){
				currentProperty=prop.next();
				properties[counter]=FrameUtil.clearNS(currentProperty.toString());	
				//System.out.println("Proprietà "+properties[counter]);
				param[counter]=FrameUtil.clearNS(toInsert.getPropertyValue(currentProperty).toString());
				//System.out.println("Valore "+param[counter]);
				counter++;
			}
			properties[counter]="Testo";
			param[counter]="Text"; //IMPOSTO UN TESTO FITTIZIO A SCOPO DIMOSTRATIVO
			modelOutput.createIndividual(toInsert);
			//toInsert.remove();
			Provision prov=new Provision(ont);
			prov.setID(FrameUtil.clearNS(toInsert.getURI())); //CREO IL NOME DELLA DISPOSIZIONE INTERROGANDO IL TIPO DELLA DISPOSIZIONE
			for(int i=0;i<=param.length-2;i++){//LENGTH-2 PERCHE' UNO E' IL TESTO CHE LO AGGIUNGO A PARTE, E 1 PERCHE' GLI ELEMENTI SONO LENGTH-1
				prov.createArguments(properties[i], param[i]);
			}
			prov.setText("Text");//IMPOSTO IL TESTO DELLA DISPOSIZIONE
			addProvision(prov);
		//TODO AGGIUNGI ALL?ALBERO
			addProvisionToTree(prov);
			/*int conta=0;
			ExtendedIterator it=modelOutput.listIndividuals();
			while(it.hasNext()){
				it.next();
				conta++;
			}
			System.out.println("DDD"+conta);
			//createProvision(ont, properties, param);*/
		}
	}
	
	
	private void addProvisionToTree(Provision prov){
		DefaultMutableTreeNode node=new DefaultMutableTreeNode(prov.getID());
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)provisionTree.getModel().getRoot();		
		//CERCO IL NODO PADRE DELL'ISTANZA DI DISPOSIZIONE CHE VOGLIO INSERIRE
		DefaultMutableTreeNode father=new DefaultMutableTreeNode();
		DefaultTreeModel m= (DefaultTreeModel)provisionTree.getModel();
		Enumeration<DefaultMutableTreeNode> e=rootNode.children();
		while(e.hasMoreElements()){
			father=(DefaultMutableTreeNode)e.nextElement();
			String typeOfProvision=(String)father.toString();
			if(typeOfProvision.equals(prov.getType())){//HO TROVATO IL NODO PADRE, INSERISCO LA DISPOSIZIONE COME FIGLIO
				//SE IL NODO PADRE HA COME FIGLIO IL NODO ETICHETTATO COME BLANK, ALLORA LO ELIMINO
				System.out.println(father.getChildAt(0).toString());
				if(father.getChildAt(0).toString().equals("Blank")){
					m.removeNodeFromParent((DefaultMutableTreeNode) father.getChildAt(0));
					//father.removeAllChildren();
				}
				if(father.getChildCount()==1)				System.out.println(father.getChildAt(0));
				/*for(int i=0;i<father.getChildCount();i++){//MI EVITA UN COSTANTE VISUALIZZARSI DI NODI BLANK CREATI CONL METDO
					//SearchAndDeleteFromProvisionTree
					if(father.getChildAt(i).toString().equals("Blank")){
						father.remove(i);
						//m.removeNodeFromParent(father.getChildAt(i));
					}
					
				}*/
				
				m.insertNodeInto(node,father,father.getChildCount());
			}		
		}
				Enumeration<String> keys=prov.getKeys();
				String name,value;
				while(keys.hasMoreElements()){	
					name=keys.nextElement();//NOME DELLA PROPRIETA
					value=prov.getArgumentValue(name);//VALORE DELLA PROPRIETA
					DefaultMutableTreeNode k=new DefaultMutableTreeNode(name+": "+value);
					m.insertNodeInto(k,node,node.getChildCount());
				}
				DefaultMutableTreeNode argument=new DefaultMutableTreeNode("Testo: "+prov.getText());
				m.insertNodeInto(argument,node,node.getChildCount());
	}
	
	private void createFormalTree(){
		DefaultMutableTreeNode root=new DefaultMutableTreeNode("Formal Tree");
		formalTree=new JTree(root);
		((DefaultTreeModel)formalTree.getModel()).reload();
		//DefaultMutableTreeNode laws=new DefaultMutableTreeNode("Leggi");
		/*DefaultMutableTreeNode articles=new DefaultMutableTreeNode("Articoli");
		DefaultTreeModel m= (DefaultTreeModel)formalTree.getModel();
		m.insertNodeInto(articles, root, root.getChildCount());*/
	}
	
	public void addParagraph(Provision [] provisions,String []types){//AGGIUNGE PARAGRAFI IN UN NUOVO ARTICOLO ALL'ALBERO FORMALE
		if(provisions.length==0){// SE LA RICERCA NON HA RESTITUITO DISPOSIZIONI, NON FARE NULLA
			return;
		}
		numberOfArticle++; //AUMENTO IL CONTATORE DEGLI ARTICOLI
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)formalTree.getModel().getRoot();
		DefaultMutableTreeNode newArticleNode=new DefaultMutableTreeNode("Articolo "+numberOfArticle);
		articles.add("Articolo "+numberOfArticle); //AGGIUNGO IL NUOVO ARTICOLO NEL VETTORE DEGLI ARTICOLO CREATI (SERVE SOLO PER ELENCARLI)
		DefaultMutableTreeNode toInsert; //RAPPRESENTA LA DISPOSIZIONE DA INSERIRE
		//DefaultMutableTreeNode articlesNode=(DefaultMutableTreeNode)rootNode.getFirstChild();
		DefaultTreeModel m= (DefaultTreeModel)formalTree.getModel();
		//m.insertNodeInto(newArticleNode,articlesNode,articlesNode.getChildCount());//INSERISCO IL NUOVO ARTICOLO NELL'ALBERO
		m.insertNodeInto(newArticleNode,rootNode,rootNode.getChildCount());
		DefaultMutableTreeNode node;
		for(int i=0;i<provisions.length;i++){//PER OGNI DISPOSIZIONE RITORNATA
			numberOfParagraph++;//AUMENTO IL NUMERO DEI PARAGRAFI
			node=new DefaultMutableTreeNode("Comma"+(newArticleNode.getChildCount()+1));//CREO IL NODO DEL PARAGRAFO (numberOfParagraph)
			m.insertNodeInto(node, newArticleNode, newArticleNode.getChildCount());//LO INSERISCO ALL'INTERNO DEL NUOVO ARTICOLO	
			DefaultMutableTreeNode argument;
			toInsert=new DefaultMutableTreeNode(provisions[i].getID());
			
			Individual ind=null;
			//TODO TOGLI LA STRING HTTP....SOTTO E METTI L'NS
			ind=modelOutput.getIndividual("http://provisions.org/model/1.0#"+provisions[i].getID());
			ind.remove();

			searchAndDeleteFromProvisionTree(provisions[i].getID(),types[i]);//ELIMINO DALL'ALBERO DELLE DISP. LA DISPOSIZIONE CHE INSERISCO
			m.insertNodeInto(toInsert,node,node.getChildCount());//INSERISCO LA DISPOSIZIONE
			//NEL PARAGRAFO ALL'INTERNO DELL'ALBERO
			//INSERISCO GLI ARGOMENTI DELLA DISPOSIZIONE NELL'ALBERO
			Enumeration <String >keys=provisions[i].getKeys();
			//System.out.println(provisions[i].toString());
			String argumentValue=new String();//CONTIENE IL VALORE DELL'ARGOMENTO DA INSERIRE
			String argumentName=new String();//CONTIENE IL NOME DELL'ARGOMENTO DA INSERIRE
			while(keys.hasMoreElements()){
				argumentName=keys.nextElement();
				argumentValue=provisions[i].getArgumentValue(argumentName);
				argument=new DefaultMutableTreeNode(argumentName+": "+argumentValue);
				m.insertNodeInto(argument,toInsert,toInsert.getChildCount());	
			}
			//INSERISCO IL NODO CON IL TESTO SEPARATAMENTE DAGLI ALTRI
			argument= new DefaultMutableTreeNode("Testo:" +provisions[i].getText());
			m.insertNodeInto(argument, toInsert, toInsert.getChildCount());
		}
		
	}
	
	public void addToParagraph(final Provision [] provisions,final String [] types){
		if(numberOfArticle==0)//SE NON VI SONO ARTICOLI, ALLORA NE CREA UNO NUOVO
		{
			JOptionPane.showMessageDialog(this,"Nessun articolo presente. Creazione nuovo articolo", "Nessun Articolo", JOptionPane.INFORMATION_MESSAGE);
			addParagraph(provisions,types);//CHIAMO IL METODO DI CREAZIONE NUOVO ARTICOLO
			return; //ESCO DAL METODO
		}
		//ALTRIMENTI CREA LA FINESTRA DI SCELTA IN QUALE ARTICOLO INSERIRE
		//POI INSERISCI NELL'ALBERO FORMALE E TOGLI DALL'ALTRO
		final JDialog articleChooser=new JDialog(this);
		articleChooser.setTitle("Scegli l'articolo in cui inserire");
		Container articleContentPane=articleChooser.getContentPane();
		articleContentPane.setLayout(new BorderLayout());
		JPanel articleChooserSubPane=new JPanel();
		articleChooserSubPane.setLayout(new GridLayout(0,1));
		JLabel label=new JLabel("Scegli l'articolo in cui inserire \n da uno di quelli esistenti");
		articleContentPane.add(label,BorderLayout.NORTH);//MIGLIORABILE L'INTERFACCIA
		articleContentPane.add(articleChooserSubPane,BorderLayout.CENTER);
		//CREO LA PARTE CONTENENTE I PULSANTI OK E ANNULLA
		JPanel bottomPane=new JPanel();
		bottomPane.setLayout(new FlowLayout());
		JButton okButton=new JButton("OK");
		JButton cancelButton=new JButton("Annulla");
		bottomPane.add(okButton);
		bottomPane.add(cancelButton);
		articleContentPane.add(bottomPane,BorderLayout.SOUTH);
		
		//CREO I LISTENER ASSOCIATO AL RADIOBUTTON
		 ActionListener radioButtonListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				AbstractButton button=(AbstractButton)e.getSource();//CONVERTO L'ORIGINE DELL'EVENTO IN BUTTON
				 selectedArticle= button.getText();//RICAVO IL NOME DELLA DISPOSIZIONE DAL BUTTON
			}

		};
				
		ButtonGroup group=new ButtonGroup();//GRUPPO IL GRUPPO DI SELEZIONE DELL'ARTICOLO
		JRadioButton aRadioButton;
		for(int i=0;i<numberOfArticle;i++){
			aRadioButton=new JRadioButton(articles.get(i).toString());
			aRadioButton.addActionListener(radioButtonListener);
			articleChooserSubPane.add(aRadioButton);
			group.add(aRadioButton);
		}
				
		//CREO IL LISTENER DEL PULSANTE CANCELLA
		ActionListener cancelListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				articleChooser.dispose();
			}
		};
		cancelButton.addActionListener(cancelListener);
		
		//CREO IL LISTENER DEL PULSANTE OK
		ActionListener okListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//FINCHE' NON SELEZIONO UN ARTICOLO (O CLICCO CANCELLA) NON SI PROCEDE
				if(selectedArticle.equals("")){
					JOptionPane.showMessageDialog(articleChooser,"Nessun articolo selezionato. Selezionare articolo", "Nessun Articolo", JOptionPane.ERROR_MESSAGE);
					return;
				}
				DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)formalTree.getModel().getRoot();
				DefaultMutableTreeNode toInsert; //RAPPRESENTA LA DISPOSIZIONE DA INSERIRE
				DefaultTreeModel m= (DefaultTreeModel)formalTree.getModel();
				//DefaultMutableTreeNode articlesNode=(DefaultMutableTreeNode)rootNode.getFirstChild();//NODO DEGLI ARTICOLI DELL'ALBERO FORMALE
				DefaultMutableTreeNode chosenArticleNode=new DefaultMutableTreeNode();//RAPPRESENTA IL NODO DELL'ARTICOLO IN CUI VOGLIO INSERIRE
				/*for(int i=0;i<articlesNode.getChildCount();i++){//CERCO IL NODO DELL'ARTICOLO IN CUI VOGLIO INSERIRE
					chosenArticleNode=(DefaultMutableTreeNode)articlesNode.getChildAt(i);
					if(chosenArticleNode.toString().equals(selectedArticle)){//HO TROVATO IL NODO DELL'ARTICOLO CERCATO
						break;
					}
				}*/
				for(int i=0;i<rootNode.getChildCount();i++){//CERCO IL NODO DELL'ARTICOLO IN CUI VOGLIO INSERIRE
					chosenArticleNode=(DefaultMutableTreeNode)rootNode.getChildAt(i);
					if(chosenArticleNode.toString().equals(selectedArticle)){//HO TROVATO IL NODO DELL'ARTICOLO CERCATO
						break;
					}
				}
				DefaultMutableTreeNode node,argument;
				for(int i=0;i<provisions.length;i++){//PER OGNI DISPOSIZIONE RITORNATA
					numberOfParagraph++;
					node=new DefaultMutableTreeNode("Comma "+(chosenArticleNode.getChildCount()+1));//CREO IL NODO DEL PARAGRAFO
					m.insertNodeInto(node, chosenArticleNode, chosenArticleNode.getChildCount());//LO INSERISCO ALL'INTERNO DEL NUOVO ARTICOLO	
					toInsert=new DefaultMutableTreeNode(provisions[i].getID());
					Individual ind=null;
					//TODO TOGLI LA STRING HTTP....SOTTO E METTI L'NS
					ind=modelOutput.getIndividual("http://provisions.org/model/1.0#"+provisions[i].getID());
					ind.remove();
					searchAndDeleteFromProvisionTree(provisions[i].getID(),types[i]);//ELIMINO IL NODO DELLA DISPOSIZIONE DALL'ALBERO 
					//DELLE DISPOSIZIONI
					m.insertNodeInto(toInsert,node,node.getChildCount());//INSERISCO LA DISPOSIZIONE
					//NEL PARAGRAFO ALL'INTERNO DELL'ALBERO
					//INSERISCO GLI ARGOMENTI DELLA DISPOSIZIONE NELL'ALBERO
					Enumeration <String >keys=provisions[i].getKeys();
					//System.out.println(provisions[i].toString());
					String argumentValue=new String();//CONTIENE IL VALORE DELL'ARGOMENTO DA INSERIRE
					String argumentName=new String();//CONTIENE IL NOME DELL'ARGOMENTO DA INSERIRE
					while(keys.hasMoreElements()){
						argumentName=keys.nextElement();
						argumentValue=provisions[i].getArgumentValue(argumentName);
						argument=new DefaultMutableTreeNode(argumentName+": "+argumentValue);
						m.insertNodeInto(argument,toInsert,toInsert.getChildCount());	
					}
					//INSERISCO IL NODO CON IL TESTO SEPARATAMENTE DAGLI ALTRI
					argument= new DefaultMutableTreeNode("Testo:" +provisions[i].getText());
					m.insertNodeInto(argument, toInsert, toInsert.getChildCount());
				}
				selectedArticle="";//REIMPOSTA L'ARTICOLO SCELTO AL VALORE "NESSUN VALORE"
				articleChooser.dispose();
			}
		};
		okButton.addActionListener(okListener);
		
		
		articleChooser.setModal(true);
		articleChooser.pack();
		articleChooser.setVisible(true);
	}
	
	
	private String getSelectedArticle(){//TORNA IL VALORE DELL'ARTICOLO SCELTO
		return selectedArticle;
	}
	private void createMainPanel(final ProvisionFrame frame)
	{
		//PER CREARE PIU'ELEMENTI LEGATI "BENE" ASSIEME GUARDA SPRINGLAYOUT O JTABBEDPANE PER FARLI IN TAB DIVERSI
		//creo l'area di testo principale
		Container contentPane=frame.getContentPane();//creo il pannello posto nell'area centrale
		panel=new JPanel();//PARTE SX DEL PANNELLO
		panel.setLayout(new GridLayout(1,1));
		JPanel panel1=new JPanel();//PARTE DX DEL PANNELLO
		panel1.setLayout(new GridLayout(1,1));
		createFormalTree();
		JScrollPane textScroller=new JScrollPane(formalTree);//SCROLLER CONTENENTE L'ALBERO DEL PROFILO FORMALE
		panel.add(textScroller);
		document=text.getDocument();//IMPOSTO IL DOCUMENT DELL'APPLICAZIONE
		JScrollPane treeScroller=new JScrollPane(provisionTree);//SCROLLER CON L'ALBERO DELLE DISPOSIZIONI
		panel1.add(treeScroller);//AGGIUNGO LO SCROLLER DELL'ALBERO ALLA PARTE DX
		provisionTree.addTreeSelectionListener(new SelectionListener(frame));
		provisionTree.addTreeWillExpandListener(new TreeExpandListener());
		JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1,panel);
		Dimension frameSize=frame.getSize();
		int location=frame.getLocation().x+(int)frameSize.getWidth()/2;
		split.setDividerLocation(location);
		contentPane.add(split);

		//creo il gestore del document ,utile per vedere quando viene modificata IL MODELLO
		
		class DocumentGesture implements DocumentListener{
			public void changedUpdate(DocumentEvent e){
				init=true; //indico che il documento ha subito una qualsiasi operazione
				modified=true; //indico che il lavoro ha subito modifiche dall'ultimo salvataggio
			}
			public void insertUpdate(DocumentEvent e){
				init=true; //indico che il documento ha subito una qualsiasi operazione
				modified=true; //indico che il lavoro ha subito modifiche dall'ultimo salvataggio
				return;
			}
			public void removeUpdate(DocumentEvent e){
				init=true; //indico che il documento ha subito una qualsiasi operazione
				//modified=true; //indico che il lavoro ha subito modifiche dall'ultimo salvataggio
				//System.out.println("rimozione");
				return;
			}
		}
		document.addDocumentListener(new DocumentGesture());
		
	}

	public OntModel getModel()
	{
		return model;
	}
	
	//UTILE PER ELIMINARE DAL NOME DI UN OGGETTO APPARTENENTE ALL'ONTOLOGIA TUTTA LA PARTE PRIMA DEL #
	private String getProvisionType(OntClass ont){
		String provisionType=ont.toString();
		for(int i=provisionType.length()-1;i!=-1;i--)
			if(provisionType.charAt(i)=='#'){
				provisionType=provisionType.substring(i+1,provisionType.length());
				break;
			}
		return provisionType;
	}
	
	//scrive su area testo il modello corrente
	public void writeOnScreen(){
		ByteArrayOutputStream bout=null;
		bout=new ByteArrayOutputStream();
     	this.modelOutput.write(bout);
     	this.text.setText(new String(bout.toByteArray()));
	}
	
	private DefaultMutableTreeNode searchAndDeleteFromProvisionTree(String id,String type){//SERVE PER CERCARE UN NODO NEL'ALBERO DELLE DISPOSIZIONI
		// ED ELIMINARLO DALL'ALBERO
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)provisionTree.getModel().getRoot();
		DefaultMutableTreeNode toRemove=new DefaultMutableTreeNode();
		DefaultMutableTreeNode father=new DefaultMutableTreeNode();
		DefaultMutableTreeNode blank=new DefaultMutableTreeNode("Blank");
		Enumeration<DefaultMutableTreeNode> e=rootNode.children();
		while(e.hasMoreElements()){
			father=(DefaultMutableTreeNode)e.nextElement();
			String typeOfProvision=(String)father.toString();
			if(typeOfProvision.equals(type)){
				e=father.children();{//	ELENCO I FIGLI DEL NODO PADRE
					while(e.hasMoreElements()){
						toRemove=e.nextElement();
						if(toRemove.toString().equals(id)){//HO TROVATO IL NODO DA ELIMINARE
							DefaultTreeModel m= (DefaultTreeModel)provisionTree.getModel();
							m.removeNodeFromParent(toRemove);//LO ELIMINO
							if(father.getChildCount()==0){
								//father.add(new DefaultMutableTreeNode("Blank"));//RICONTROLLALO
								m.insertNodeInto(blank,father , father.getChildCount());
							}
							//RIMUOVO LA DISPOSIZIONE DAL VETTORE DELLE DISPOSIZIONI
							Provision temp;
							for(int i=0;i<=provisions.size();i++){
								temp=provisions.elementAt(i);
								if(temp.getID().equals(id)){
									provisions.remove(i);
									//RIMUOVO DAGLI ID USATI L'ID DELLA DISPOSIZIONE ELIMINATA
									for(int j=0;j<usedID.size();j++){
										if(usedID.elementAt(j).equals(id)){
											usedID.remove(j);
										}
									}
									break;
								}
							}
							return toRemove;
						}
					}
				}
			}
		}
		return null;
	}
	
	 //	CREA UNA NUOVA DISPOSIZIONE ASSOCIANDOGLI UN NOME UNIVOCO
	//ONT TIPO (CLASSE) DI DISPOSIZIONE, PROPERTIES NOME DEGLI ARGOMENTI, PARAM VALORI ARGOMENTI (TESTO COMPRESO)
	public Provision createProvision(OntClass ont, String []properties,String []param){ 
		Provision prov=new Provision(ont);
		prov.setID(createID(prov.getType())); //CREO IL NOME DELLA DISPOSIZIONE INTERROGANDO IL TIPO DELLA DISPOSIZIONE
		for(int i=0;i<=param.length-2;i++){//LENGTH-2 PERCHE' UNO E' IL TESTO CHE LO AGGIUNGO A PARTE, E 1 PERCHE' GLI ELEMENTI SONO LENGTH-1
			prov.createArguments(properties[i], param[i]);
		}
		prov.setText(param[param.length-1]);//IMPOSTO IL TESTO DELLA DISPOSIZIONE
		addProvision(prov);
		//writeOnScreen();
		//AGGIUNGO LA DISPOSIZIONE ALL'ALBERO (per ora come elemento figlio di un figlio della radice)
		DefaultMutableTreeNode node=new DefaultMutableTreeNode(prov.getID());
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)provisionTree.getModel().getRoot();	
		DefaultTreeModel m= (DefaultTreeModel)provisionTree.getModel();
		//CERCO IL NODO PADRE DELL'ISTANZA DI DISPOSIZIONE CHE VOGLIO INSERIRE
		DefaultMutableTreeNode father=new DefaultMutableTreeNode();
		Enumeration<DefaultMutableTreeNode> e=rootNode.children();
		while(e.hasMoreElements()){
			father=(DefaultMutableTreeNode)e.nextElement();
			String typeOfProvision=(String)father.toString();
			if(typeOfProvision.equals(prov.getType())){//HO TROVATO IL NODO PADRE, INSERISCO LA DISPOSIZIONE COME FIGLIO
				//SE IL NODO PADRE HA COME FIGLIO IL NODO ETICHETTATO COME BLANK, ALLORA LO ELIMINO
				if(father.getChildCount()==1&father.getChildAt(0).toString().equals("Blank")){
					m.removeNodeFromParent((DefaultMutableTreeNode)father.getChildAt(0));
				}
				m.insertNodeInto(node,father,father.getChildCount());
			}		
		}
		//CREO L'INDIVIDUO NEL FILE RDF
		Individual ind;
		ind=modelOutput.createIndividual("http://provisions.org/model/1.0#"+prov.getID(),prov.getOntClass());//CREO UN'ISTANZA NEL MODELLO DI OUTPUT
		//SOPRA SOSTITUISCI LA STRING HTTP..... COL METODO GETNS()
		//AGGIUNGO AL NODO CREATO I NODI RELATIVI AGLI ARGOMENTI
		Enumeration<String> keys=prov.getKeys();
		String name,value;
		while(keys.hasMoreElements()){	
			name=keys.nextElement();//NOME DELLA PROPRIETA
			value=prov.getArgumentValue(name);//VALORE DELLA PROPRIETA
			DefaultMutableTreeNode k=new DefaultMutableTreeNode(name+": "+value);
			m.insertNodeInto(k,node,node.getChildCount());
			//AGGIUNGO LA PROPRIETA' AL FILE RDF
			//Literal literal=modelOutput.createTypedLiteral("http://provisions.org/model/1.0#"+value);
			Resource res=modelOutput.createResource("http://provisions.org/model/1.0#"+value);
		//	System.out.println("Inserire la proprietà "+modelOutput.getProperty(name)+" perchè" +ind.hasProperty(modelOutput.getProperty(name)));
			ind.setPropertyValue(modelOutput.getProperty("http://provisions.org/model/1.0#"+name.toString()),res);
		}
		//AGGIUNGO A PARTE IL TESTO DELLA DISPOSIZIONE
		DefaultMutableTreeNode argument=new DefaultMutableTreeNode("Testo: "+prov.getText());
		m.insertNodeInto(argument,node,node.getChildCount());
		return prov;
	}
	
	public void reloadTree(){//AGGIORNA L'ALBERO DELL'APPLICAZIONE (NON FUNZIONA)
		int[] expandedRow=new int[ radice.getChildCount()];
		for(int i=0;i<radice.getChildCount();i++){
			if(!provisionTree.isCollapsed(i)){//1 LA RIGA è ESPANSA
				expandedRow[i]=1;
			}else{
				expandedRow[i]=0;//LA RIGA E' COLLASSATA
			}
		}
		((DefaultTreeModel)provisionTree.getModel()).reload();
		for(int i=0;i<expandedRow.length;i++){
			if(expandedRow[i]==1){//OGNI RIGA PRECEDENTEMENTE ESPANSA, VIENE NUOVAMENTE ESPANSA
				//System.out.println("Riga che espando"+i);
				provisionTree.expandRow(i);	
			}
		}
	}
	
	public void reloadTreeAfter(){//AGGIORNA L'ALBERO
		((DefaultTreeModel)provisionTree.getModel()).reload();
		for(int i=0;i<nodeExpanded.size();i++){
			//DefaultMutableTreeNode n=(DefaultMutableTreeNode)nodeExpanded.get(i).getLastPathComponent();
			provisionTree.expandPath(nodeExpanded.get(i));
		}
	}	
	
	
	//	CREA UN ID PER UNA NUOVA DISPOSIZIONE
	private String createID(String type){
		int subfix;
		String ID=null;
		boolean duplicate=true;
		while(duplicate){
			subfix=(int)(range*Math.random());
			ID=type+subfix;
			duplicate=searchID(ID);
		}
		usedID.add(ID);
		return ID;
	}
	//SERVE PER VEDERE SE UN ID E' GIA STATO USATO
	private boolean searchID(String ID){ 
		return usedID.contains(ID);
	}
	
	//AGGIUNGE UNA NUOVA DISPOSIZIONE A QUELLE INSERITE NELL'APPLICAZIONE
	private void addProvision(Provision p){
		provisions.add(p);
	}
	
	public void deleteProvision(String ID, DefaultMutableTreeNode node){ //ID DELLA DISPOSIZIONE DA ELIMINARE, NODE NODO DA TOGLIERE NELL'ALBERO
		Provision temp=null;
		if(provisions.isEmpty()){
			return;
		}
		//LA ELIMINO DAL VETTORE DELLE DISPOSIZIONI
		for(int i=0;i<=provisions.size();i++){
			temp=provisions.elementAt(i);
			if(temp.getID().equals(ID)){
				provisions.remove(i);
				//RIMUOVO DAGLI ID USATI L'ID DELLA DISPOSIZIONE ELIMINATA
				for(int j=0;j<usedID.size();j++){
					if(usedID.elementAt(j).equals(ID)){
						usedID.remove(j);
					}
				}
				break;
			}
		}

		//SE IL PADRE NON HA FIGLI AGGIUNGO IL NODO BLANK
		DefaultMutableTreeNode father=(DefaultMutableTreeNode)node.getParent();
		if(father!=null){//	DOVREBBE ESSERE SUPERFLUO QUESTO CONTROLLO
			father.remove(node);
			if(father.getChildCount()==0){//SE IL PADRE NON HA PIU' FIGLI, RIAGGIUNGO IL NODO BLANK
				father.add(new DefaultMutableTreeNode("Blank"));
			}
		}
		else{//NON DOVREBBE MAI ESSERE ESEGUITO (CANCELLO LA RADICE DELL'ALBERO, MA LA RADICE NON PUO' ESSERE UN'ISTANZA DI DISPOSIZIONE)
			JOptionPane.showMessageDialog(this,"Errore nell'applicazione", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		//LA ELIMINO DAL VETTORE DEI NODI ESPANSI
		TreePath path = provisionTree.getNextMatch(ID, 0, javax.swing.text.Position.Bias.Forward);
		nodeExpanded.remove(path);
		TreePath fatherPath=path.getParentPath();
		// father=(DefaultMutableTreeNode)fatherPath.getLastPathComponent();
		nodeExpanded.remove(fatherPath);
		reloadTreeAfter();
		if(!father.getChildAt(0).toString().equals("Blank")){//	SE CI SONO ALTRE DISPOSIZIONI DI QUEL TIPO, LASCIA ESPANSO IL PATH
			//CONTORTO, MA ALTRIMENTI NON FUNZIONA
			Object [] nodes=fatherPath.getPath();//RICAVO TUTTI I NODI DEL PATH FINO AL LIVELLO SUPERIORE
			TreePath path2=new TreePath(nodes);//COSTRUISCO IL PATH COSì OTTENUTO EX-NOVO
			nodeExpanded.add(path2);//AGGIUNGO IL PATH A QUELLI ESPANSI
			reloadTreeAfter();
		}
		//RIMUOVO DAL FILE OWL
		Individual indRemoved=null;
		indRemoved=modelOutput.getIndividual( "http://provisions.org/model/1.0#"+temp.getID());
		indRemoved.remove();
		//TODO TOGLI LA STRING HTTP....SOTTO E METTI L'NS
		//VERSIONE  FUNZIONANTE SE INDIVIDUI E DEFINIZIONE DELLE CLASSI SONO NELLO STESSO MODELLO
	/*	ExtendedIterator <Individual> iterIndividual=modelOutput.listIndividuals();
		//if(!iterIndividual.hasNext()){}
		while(iterIndividual.hasNext()){
			indRemoved=(Individual)iterIndividual.next();
			if(indRemoved.getLocalName().equals(temp.getID())){//rimuovo eventuali statement che riguardano l'elemento
				  indRemoved.remove();
				  return;
			}
		}*/
	}
	
	//CERCA UNA DISPOSIZIONE SULLA BASE DEL SUO ID
	public Provision searchProvision(String ID){
		Provision temp;
		if(provisions.isEmpty()){//SE NON CI SONO DISPOSIZIONI ESCE SUBITO
			return null;
		}
		//LA CERCO NEL VETTORE DELLE DISPOSIZIONI
		for(int i=0;i<=provisions.size();i++){
			temp=provisions.elementAt(i);
			if(temp.getID().equals(ID)){
				return temp;
			}
		}
		return null;//SE NON L'HA TROVATA (NON DOVREBBE SUCCEDERE)
	}
	
	//CERCA UNA DISPOSIZIONE SULLA BASE DEL TIPO E DEI VALORI DEGLI ARGOMENTI
	//TYPE=TIPO DISPOSIZIONE, LABEL=ARGOMENTI FIELDS=VALORI
	public Vector<Provision> searchProvision(String type,JLabel [] labels,JTextField []fields){
		Provision temp;
		boolean allNull=true;
		Vector <Provision> prov=new Vector<Provision>(5);
		boolean find=false;
		if(provisions.size()==0){//SE NON ESISTONO DISPOSIZIONI ESCE SUBITO
			return null;
		}
		for(int i=0;i<fields.length;i++){
			allNull=allNull&(!FrameUtil.stringWithCarachter(fields[i].getText())); //CONTROLLO CHE TUTTI GLI ARGOMENTI NON SIANO NULL
		}
		if(allNull){
			return null; //SE TUTTI I CAMPI ERANO NULLI, NON RITORNO NIENTE
		}
		for(int i=0;i<provisions.size();i++){//PER OGNI DISPOSIZIONE PRESENTE
			//find=true;
			temp=provisions.elementAt(i);
			if(temp.getType().equals(type)){
				for(int j=0;j<labels.length;j++){
					if((temp.getArgumentValue(labels[j].getText()).equals(fields[j].getText()))||(FrameUtil.stringWithCarachter(fields[j].getText())==false)){//&&find){//FIND SERVE PER METTERE IN AND  I VARI TEST
						find=true;//L'ARGOMENTO HA IL VALORE RICERCATO
					}else{
						find=false;//L'ARGOMENTO NON HA IL VALORE RICERCATO
						break;// PASSO ALL'ELEMENTO SUCCESSIVO
					}
				}	
				if(find==true){
					prov.add(temp);
				}
			}
		}
		return prov;
	}
	
	//ID DELLA DISP. DA MODIFICARE, PROPERYVALUE I VLORI,PROPERTYNAME NOMI DELLE PROPRIETà, NODE IL NODO DA MODIFICARE NELL'ALBERO
	public void modifyProvision(String ID, String[] propertyName,String []propertyValue,DefaultMutableTreeNode node){
		//LA MODIFICO NELL'ALBERO IN TREEWINDOW
		Provision prov=searchProvision(ID);
		if(prov==null){//NON DOVREBBE ESSER MAI VERO, SIGNIFICA CHE LA DISPOSIZIONE NON ESISTE
			return;
		}
		for(int i=0;i<=propertyName.length-1;i++){//LENGTH-2 PERCHE' UNO E' IL TESTO CHE LO AGGIUNGO A PARTE, E 1 PERCHE' GLI ELEMENTI SONO LENGTH-1
			prov.modifyArguments(propertyName[i], propertyValue[i]);
			//System.out.println(propertyName[i] +" diventa "+propertyValue[i]);
		}
		prov.setText(propertyValue[propertyValue.length-1]);
		int properties=prov.numberOfArguments()+1;
		DefaultMutableTreeNode toRemove;
		DefaultTreeModel m=(DefaultTreeModel)provisionTree.getModel();
		for(int i=node.getChildCount()-1;i>=0;i--){
			toRemove=(DefaultMutableTreeNode) node.getChildAt(i);
			m.removeNodeFromParent(toRemove);
		}
		
		DefaultMutableTreeNode child;
		String content=null;
		for(int i=1;i<properties;i++){///////
			content=propertyName[i-1]+": "+propertyValue[i-1];
			child=new DefaultMutableTreeNode(content);
			m.insertNodeInto(child,node,node.getChildCount());
		}
		content="Testo: "+propertyValue[properties-1];
		child=new DefaultMutableTreeNode(content);
		m.insertNodeInto(child, node, node.getChildCount());
		
		//MODIFICO NEL FILE RDF
		Individual ind=null;
		//TODO TOGLI LA STRING HTTP....SOTTO E METTI L'NS
		ind=modelOutput.getIndividual("http://provisions.org/model/1.0#"+prov.getID());
		
		/*ind.remove();
		ind=modelOutput.createIndividual("http://provisions.org/model/1.0#"+prov.getID(),prov.getOntClass());
		if(!(ind==null)){
			//TODO modifica o cancellazione e nuovo inserimento
			Enumeration<String> keys=prov.getKeys();
			String name,value;
			while(keys.hasMoreElements()){	
				name=keys.nextElement();
				value=prov.getArgumentValue(name);
				//node.add(new DefaultMutableTreeNode(name+": "+value));
				//AGGIUNGO LA PROPRIETA' AL FILE RDF
				//Literal literal=modelOutput.createTypedLiteral("http://provisions.org/model/1.0#"+value);
				Resource res=modelOutput.createResource("http://provisions.org/model/1.0#"+value);
				//System.out.println("La proprietà esiste??????"+ind.hasProperty(model.getProperty("http://provisions.org/model/1.0"+name)));
				//System.out.println("Inserire la proprietà "+modelOutput.getProperty(name)+" perchè" +ind.hasProperty(modelOutput.getProperty(name)));
				ind.setPropertyValue(modelOutput.getProperty("http://provisions.org/model/1.0#"+name),res);
			}	
		}*/
		Enumeration<String> keys=prov.getKeys();
		String name,value;
		while(keys.hasMoreElements()){	
			name=keys.nextElement();
			value=prov.getArgumentValue(name);
			Resource res=modelOutput.createResource("http://provisions.org/model/1.0#"+value);
			ind.setPropertyValue(modelOutput.getProperty("http://provisions.org/model/1.0#"+name),res);
		}
		
	}
	
	public void listProvision(){//  RIMUOVE GLI ELEMENTI SEMPLICEMENTE PER STAMPARLI, MI SERVE SOLO PER DEBUG
		Provision p;
		while(!provisions.isEmpty()){
			p=provisions.lastElement();
			System.out.println( p.getID());
			provisions.remove(provisions.remove(p));
		}
	}
	//classe per filtrare i file con estensione RDF GIUSTO?
	static class RDFFileFilter extends FileFilter {

		  public boolean accept(File file) {
		    if (file.isDirectory()) return true;
		    String fname = file.getName().toLowerCase();
		    return (fname.endsWith("rdf")||fname.endsWith("owl"));
		  }

		  public String getDescription() {
		    return "File RDF/OWL";
		  }
		}
	
	
	 	static class XMLFileFilter extends FileFilter {

		  public boolean accept(File file) {
		    if (file.isDirectory()) return true;
		    String fname = file.getName().toLowerCase();
		    return (fname.endsWith("xml"));
		  }

		  public String getDescription() {
		    return "File XML";
		  }
		}
	private class SelectionListener implements TreeSelectionListener{
		ProvisionFrame app;
		
		public SelectionListener(ProvisionFrame frame){
			this.app=frame;
		}
		
		public void valueChanged(TreeSelectionEvent e){
			JTree tree=(JTree)e.getSource();
			DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if(selectedNode!=null){//UTILE PER NON AVERE SITUAZIONE CON NODO NULLO QUANDO SI CREA UNA SECONDA DISPOSIZIONE
				if(selectedNode.getDepth()==1){//CONTROLLO CHE IL NODO SIA UNO DEI TIPI DI DISPOSIZIONE
					if(selectedNode.getChildCount()!=1||!selectedNode.getFirstChild().toString().equals("Blank")){//CONTROLLO CHE L'UNICO FIGLIO NON SIA IL NODO BLANK
						// IN CASO FAVOREVOLE HO UN NODO RELATIVO AD' UN ISTANZA DI UN TIPO DI DISPOSIZIONE
						new TreeWindow(selectedNode,app);
					}
				} 
				/*TreePath path[]=new TreePath[1];
				path[0]=new TreePath(provisionTree.getModel().getRoot());
				new TreeSelectionEvent(provisionTree.getModel().getRoot(), path, null, null, null); //FORSE INUTILE*/
			}
		}		
	}
	
	public class TreeExpandListener implements TreeWillExpandListener{
		@Override
		public void treeWillCollapse(TreeExpansionEvent e)
				throws ExpandVetoException {
			nodeExpanded.remove(e.getPath());
			// TODO Auto-generated method stub
			
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent e)
				throws ExpandVetoException {
			if(!pathPresente(e.getPath().toString())){//AGGIUNGO IL PATH SOLO SE NON GIA' PRESENTE NELL'ELENCO DI QUELLI ESPANSI
				nodeExpanded.add(e.getPath());	
			}			
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//UTILE PER NON AGGIUNGERE PIù DI UNA VOLTA UN PATH ESPANSO NEL RELATIVO ELENCO
	private boolean pathPresente(String path){
		boolean presente=false;
		for(int i=1;i<=nodeExpanded.size();i++){
			if(nodeExpanded.get(i-1).toString().equals(path)){
				presente=true;
			}
		}
		return presente;
	}
	//SE SI USA QUESTO AL POSTO DEL TREE SELECTIONLISTENER FUNZIONA MEGLIO SELEZIONANDO 2 VOLTE LO STESSO NODO MA 
	//SE SI VUOLE ESPANDERE O COLLASSARE I FIGLI DEL NODO APPARE LO STESSO LA FINESTRELLA DI MODIFICA
	/*private class TreeMouseListener extends MouseAdapter{
		//import javax.swing.JTree;
		ProvisionFrame app;
		TreeMouseListener(ProvisionFrame frame){
			app=frame;
		}
		public void mousePressed(MouseEvent e){
			JTree tree=(JTree)e.getSource();
			DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			if(selectedNode==null){
				return;
			}
			if(selectedNode.getDepth()==1){//CONTROLLO CHE IL NODO SIA UNO DEI TIPI DI DISPOSIZIONE
				if(selectedNode.getChildCount()!=1||!selectedNode.getFirstChild().toString().equals("Blank")){//CONTROLLO CHE L'UNICO FIGLIO NON SIA IL NODO BLANK
					// IN CASO FAVOREVOLE HO UN NODO RELATIVO AD' UN ISTANZA DI UN TIPO DI DISPOSIZIONE
					//System.out.println("Frame uguale a null "+app==null);
					new TreeWindow(selectedNode,app);
				}
			}
		}
	};*/
	
	private void writeXML(String path){
		File file=new File(path);
		Element root=new Element("ROOT");
		String name=JOptionPane.showInputDialog(this.getGlassPane(), null, "Inserire identficativo legge", 1);
		root.setAttribute("attName","Testo di legge "+name);
		root.setText("Testo di legge "+name);
		org.jdom.Document document=new org.jdom.Document(root);
		DefaultMutableTreeNode rootNode=((DefaultMutableTreeNode)(formalTree.getModel().getRoot()));
		int numArticles=rootNode.getChildCount();
		Element []articles=new Element[numArticles];
		Element []paragraphs,provision;
		Element []arguments;
		DefaultMutableTreeNode articleNode=null;
		DefaultMutableTreeNode paragraphNode=null;
		DefaultMutableTreeNode provisionNode=null;
		DefaultMutableTreeNode[] argumentsNode;
		for(int i=0;i<rootNode.getChildCount();i++){//INSERIMENTO DEGLI ARTICOLI COME FIGLI DELLA RADICE
			articles[i]=new Element("Articolo");
			articleNode=(DefaultMutableTreeNode) rootNode.getChildAt(i);
			articles[i].setAttribute("name", articleNode.toString()); //AGGIUNTO UN NAME ALL'ARTICOLO
			articles[i].setText(articleNode.toString());
			root.addContent(articles[i]);
			paragraphs=new Element[articleNode.getChildCount()];
			provision=new Element[articleNode.getChildCount()];
			for(int j=0;j<articleNode.getChildCount();j++){//INSERIMENTO DEI COMMI COME FIGLI DELL'ARTICOLO
				paragraphs[j]=new Element("Comma");
				paragraphNode=(DefaultMutableTreeNode) articleNode.getChildAt(j);
				paragraphs[j].setText(paragraphNode.toString());
				articles[i].addContent(paragraphs[j]);
				provisionNode=(DefaultMutableTreeNode) paragraphNode.getChildAt(0);//AGGIUNGO IL NODO RELATIVO ALLA DISPOSIZIONE ASSOCIATA AL COMMA
				provision[j]=new Element(provisionType(provisionNode.toString()));
				provision[j].setText(provisionType(provisionNode.toString()));
				paragraphs[j].setAttribute("type",provisionType(provisionNode.toString()));//AGGIUNGO UN TIPI AL COMMA
				paragraphs[j].addContent(provision[j]);
				//TRATTO GLI ARGOMENTI
				argumentsNode=new DefaultMutableTreeNode[provisionNode.getChildCount()];
				arguments=new Element[provisionNode.getChildCount()];
				for(int z=0;z<provisionNode.getChildCount();z++){
					arguments[z]=new Element(argumentName(provisionNode.getChildAt(z).toString()));
					argumentsNode[z]=new DefaultMutableTreeNode(provisionNode.getChildAt(z));
					//arguments[z].setText(argumentsNode[z].toString());//+":"+arguments[z].setText);
					arguments[z].setText(argumentValue(argumentsNode[z].toString()));
					provision[j].addContent(arguments[z]);
				}
			}
			
		}
		
		XMLOutputter outputter = new XMLOutputter();
		FileOutputStream out=null;
		try{
			out=new FileOutputStream(file);
		}
		catch (Exception ex){
			JOptionPane.showMessageDialog(this, "Errore nel salvataggio");
		}
		if(out!=null){
			try {
				outputter.output(document,out );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this, "Errore nel salvataggio");
			}	
		}
		
		
		/*javax.xml.transform.Transformer transformer=null;
		try {
			 transformer = TransformerFactory.newInstance()
			.newTransformer(new StreamSource("c:/Provision1.css"));
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JDOMSource source=new JDOMSource(document);
		JDOMResult result = new JDOMResult();
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.jdom.Document doc2 = result.getDocument();
		try{
			out=new FileOutputStream(file);
		}
		catch (Exception ex){
			JOptionPane.showMessageDialog(this, "Errore nel salvataggio");
		}
		if(out!=null){
			try {
				outputter.output(document,out );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this, "Errore nel salvataggio");
			}	
		}*/
		
		
		
	}
	
	private String provisionType(String name){// DATO IL NOME DI UNA DISPOSIZIONE, NE RITORNA IL TIPO
		char charAt;
		for(int i=0;i<name.length();i++){
			charAt=new Character(name.charAt(i));
			if(Character.isDigit(charAt)){
				return name.substring(0,i);
			}
		}
		return name; //NON DOVREBBE CAPITARE MAI
	}
	
	private String argumentName(String arg){
		char charAt;
		for(int i=0;i<arg.length();i++){
			charAt=arg.charAt(i);
			if(charAt==':'){
				return arg.substring(0,i);
			}
		}
		return null; //MAI ESEGUITO
	}
	
	private String argumentValue(String arg){
		char charAt;
		for(int i=0;i<arg.length();i++){
			charAt=arg.charAt(i);
			if(charAt==':'){
				return arg.substring(i+1,arg.length());
			}
		}
		return null; //MAI ESEGUITO
	}
}
