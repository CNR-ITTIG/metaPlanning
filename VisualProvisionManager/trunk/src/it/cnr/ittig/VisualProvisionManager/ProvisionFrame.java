package it.cnr.ittig.VisualProvisionManager;

import javax.swing.JFrame;
import com.hp.hpl.jena.rdf.model.*;
import it.cnr.ittig.ProvisionModel.OntUtils;
import it.cnr.ittig.ProvisionModel.ProvisionModelFactory;
import it.cnr.ittig.VisualProvisionManager.Provision.Provision;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class ProvisionFrame extends JFrame{
	private JPanel panel;
	private ProvisionModelFactory provisionModelFactory=new ProvisionModelFactory();
	private OntModel model; //TOGLIERE? PROBLEMA CON SOTTOCLASSI (USA TRANSITIVITA') E PROPRIETA' DELLE CLASSI(PROPRIETA' CONDIVISE NON RESTITUITE)
	private OntModel modelBase; //MODEL SENZA REASONER
	private OntModel modelOutput;	//MODEL DI OUTPUT DOVE VERRANNO SALVATE LE ISTANZE CREATE
	private String savedPath;//UTILE PER VEDERE SE UN DATO FILE E' GIA' STATO SALVATO ED IN CHE PATH 
	private Document document=null;
	private JTextArea text;
	private JPanel subPanel;
	private JPanel provisionPanel; //PANNELLO DOVE SI VISUALIZZANO LE DISPOSIZIONI (RIFLETTI SE LASCIAR QUI LA DICHIARAZIONE)
	private JTree tree; //ALBERO DELLE DISPOSIZIONI
	private JTree provisionTree;//ALBERO DI PROVA
	private DefaultMutableTreeNode radice=new DefaultMutableTreeNode("Disposizioni");
	//private JDesktopPane desktop = new JDesktopPane();
	private boolean modified=false; //indica se il lavoro ha subito modifiche dall'ultimo salvataggio
	private boolean init=false; //indica se il documento ha subito una qualsiasi operazione o se non è mai stato usato. Utile per quando
		//si apre un nuovo documento con il documento iniziale mai modificato
	private String dest=null;
	private String counter=null;
	private String actionS=null;
	private String object=null;
	private String textLaw=null;
	private String effect=null;
	private String penalty=null;
	private String definiendum=null;
	private String definiens=null;
	private String activity=null;
	//private String[] vec={"A","B"};
	private Vector <Provision> provisions=new Vector<Provision>();
	private int range=551; // INDICA QUANTE DISPOSIZIONI DI OGNI TIPO SONO GESTIBILI DAL PROGRAMMA (range-1)
	private Vector <String> usedID=new Vector<String>(); //TIENE TRACCIA DEGLI ID USATI PER LE DISPOSIZIONI, MEGLIO COME VECTOR?
	//private Vector <OntClass> rootVector;//CONTIENE LE RADICI DELL'ALBERO DELLE DISPOSIZIONI
	private Vector <OntClass> rootVector;//CONTIENE LE RADICI DELL'ALBERO DELLE DISPOSIZIONI
	//private Vector <String>  root;//CONTIENE TUTTI I TIPI DI DISPOSIZIONI DEL MODELLO
	
	
	public static void main(String[] args){
		Runnable runner=new Runnable(){
		public void run(){
		final ProvisionFrame frame=new ProvisionFrame();
		frame.setTitle("Applicazione");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		//add(desktop,BorderLayout.CENTER);
		frame.text=new JTextArea(30,30); //TODO CAMBIA, FALLA DI DIMENSIONI "AUTOIMPOSTANTI"
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
		frame.createMenu(frame);
		frame.createMainPanel(frame);
		frame.setVisible(true);
		}	
	};
	EventQueue.invokeLater(runner);
	}
	
	
	//CARICA IL MODELLO CONTENUTO NEL PROVISIONMODEL
	private void loadModel(){
		model=provisionModelFactory.getProvisionModel();
		modelBase=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
		InputStream in = FileManager.get().open("C:/ProvisionModel.rdf");
		modelBase.read(in, "");
		modelOutput=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM,null);
		//modelOutput.addSubModel(modelBase);
		//model.addLoadedImport("C:/ProvisionModel.rdf");
		/*OntDocumentManager manager=new OntDocumentManager();
		manager.loadImport(modelOutput,"C:/ProvisionModel.rdf")*/;
		in = FileManager.get().open("C:/ProvisionModel.rdf");
		modelOutput.read(in,"RDF/XML-ABBREV");
		ExtendedIterator r=modelOutput.listSubModels();
		if(!r.hasNext())System.out.println("No import");
		while (r.hasNext()){
		System.out.println("Importo"+r.next());
		}
		/*in = FileManager.get().open("C:/ProvisionModel.rdf");
		OntModelSpec withReasoner=new OntModelSpec(OntModelSpec.OWL_MEM_RULE_INF); //PROBLEMA TROVARE UN REASONER FUNZIONANTE CON LE PROPRIETA' CONDIVISE
		//withReasoner.setReasoner(new OWLFBRuleReasoner(new OWLFBRuleReasonerFactory()));
		modelReasoner=ModelFactory.createOntologyModel(withReasoner);
		modelReasoner.read(in, "");
		System.out.println("Specifiche "+model.getSpecification().getReasoner());
		model.getSpecification().setReasoner((new OWLMicroReasoner(new OWLMicroReasonerFactory())));
		System.out.println("Specifiche di base  "+modelBase.getSpecification().getReasoner());
		System.out.println("Specifiche avanzate  "+modelReasoner.getSpecification().getReasoner());*/

	}
	
	private void initializeDimension(){
		//imposto le dimensioni della finesta basate sulle dimensioni dello schermo/2
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Dimension screen=toolkit.getScreenSize();
		int larghezza=(int) screen.getWidth()/2;
		int altezza=(int) screen.getHeight()/2;
		setSize(larghezza,altezza);
		setLocation(larghezza/2,altezza/2);
	}
	
	//CREA IL MENU DI INSERIMENTO DELLE DISPOSIZIONI LEGGENDO DALL'ONTMODEL SENZA REASONER
	//frame e l'applicativo principale (utile perchè il listener deve lavorare su una variabile final), ont la classe padre in cui inserire
	//(come menu o menuItem), menuOnt il padre in cui inserire ont
	//INOLTRE CREO ANCHE L'ALBERO DELLE DISPOSIZIONI
	/*private void insertMenu(final ProvisionFrame frame,OntClass ont, JMenu menuOnt,DefaultMutableTreeNode node){
		ExtendedIterator<OntClass> iter1=ont.listSubClasses();
		OntClass figlio;
		while(iter1.hasNext()){
			figlio=iter1.next(); 
			if(figlio.hasSubClass()){
				JMenu menuFiglio=new JMenu(getProvisionType(figlio));
				menuOnt.add(menuFiglio);
				//root.add(figlio);
				insertMenu(frame,figlio,menuFiglio,node);				
			}else{
				final OntClass figlio1=figlio;//UTILE PERCHE' IL LISTENER SUCCESSIVO DEVE LAVORARE SU UNA VARIABILE FINAL
				JMenuItem menuItemFiglio=new JMenuItem(getProvisionType(figlio));
				root.add(getProvisionType(figlio1));
				DefaultMutableTreeNode child=new DefaultMutableTreeNode(figlio);
				node.insert(child, 0);
				menuOnt.add(menuItemFiglio);
				menuItemFiglio.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						InsertWindow win=new InsertWindow(frame,modelBase,figlio1);
					}
				});
			}
		}
	}*/
	
	private void insertMenu(final ProvisionFrame frame,OntClass ont, JMenu menuOnt){
		ExtendedIterator<OntClass> iter1=ont.listSubClasses();
		OntClass figlio;
		while(iter1.hasNext()){
			figlio=iter1.next(); 
			if(figlio.hasSubClass()){
				JMenu menuFiglio=new JMenu(getProvisionType(figlio));
				menuOnt.add(menuFiglio);
				//root.add(figlio);
				insertMenu(frame,figlio,menuFiglio);				
			}else{
				final OntClass figlio1=figlio;//UTILE PERCHE' IL LISTENER SUCCESSIVO DEVE LAVORARE SU UNA VARIABILE FINAL
				JMenuItem menuItemFiglio=new JMenuItem(getProvisionType(figlio));
				//root.add(getProvisionType(figlio1));
				//INSERISCO IL NODO DEL TIPO DI DISPOSIZIONE NELL'ALBERO
				DefaultMutableTreeNode child=new DefaultMutableTreeNode(getProvisionType(figlio));
				menuOnt.add(menuItemFiglio);
				radice.add(child);// AGGIUNGO IL NODO DELLA DISPOSIZIONI COME FIGLIO DELLA RADICE
			//	radice.insert(child,radice.getChildCount());
				//AGGIUNGO UN NODO VUOTO COME FIGLIO DEL TIPO DI DISPOSIZIONE
				child.add(new DefaultMutableTreeNode("Blank"));
				menuItemFiglio.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						InsertWindow win=new InsertWindow(frame,modelBase,figlio1);
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
		Action cutAction =FrameUtil.findAction(actions, DefaultEditorKit.cutAction);
		Action copyAction =
		FrameUtil.findAction(actions, DefaultEditorKit.copyAction);
		Action pasteAction =
		FrameUtil.findAction(actions, DefaultEditorKit.pasteAction);
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
		
		//PROCEDO CON LA CREAZIONE DEL MENU DI INSERIMENTO DELLE DISPOSIZIONI
		OntClass ont;
		ExtendedIterator <OntClass>  iter=OntUtils.getTopClasses(modelBase);//CERCO LA CLASSE DI GERARCHIA PIU' ALTA
		//rootVector=new Vector<OntClass>(); //CONTIENE LE RADICI DELL'ALBERO DELLE DISPOSIZIONI
		//root=new Vector();
		while(iter.hasNext()){
			ont=iter.next();
			if(ont.isUnionClass()){
				//NON INSERIRE
			}
			else if(getProvisionType(ont).equals("ImplicitRight")){
				//NON INSERIRE
				}
			//insertMenu(frame, ont, insertMenu );
			/*menus[count]=new JMenu(getProvisionType(ont));
			insertMenu.add(menus[count]);
			ExtendedIterator<OntClass> iter1=ont.listSubClasses();
			insertMenu(frame,ont,menus[count]);*/
				else if(ont.hasSubClass()){
					//rootVector.add(ont);
					//DefaultMutableTreeNode root=new DefaultMutableTreeNode(ont);
					JMenu menuRoot=new JMenu(getProvisionType(ont));
					insertMenu.add(menuRoot);
					insertMenu(frame,ont,menuRoot);
				}
				else{
					JMenuItem menuItemRoot=new JMenuItem(getProvisionType(ont));
					insertMenu.add(menuItemRoot);
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
				System.out.println(text.getText());
				document=null; //PER ORA INUTILE
				text.setText("");
				init=false;
				modified=false;
				savedPath=null;
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
			        	model.read(in, "");
			        	ByteArrayOutputStream bout=new ByteArrayOutputStream();
			           //riscrittura del file su standard output (togliere, per ora controllo almeno gli errori)
			        	model.write(bout);
			        	text.setText(new String(bout.toByteArray()));
						init=false;
						modified=false;
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
				FrameUtil.saveFileWithName( frame,  model,  modified,  savedPath);
			}
		};
		item3.addActionListener(actionSaveWithName);
		//Listener per salvare un File//TODO far partire il file chooser da una directory particolare
		ActionListener actionSave=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FrameUtil.saveFile(frame,  model,  modified,  savedPath);
			}
		};
		item2.addActionListener(actionSave); 
		
		ActionListener findListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				frame.listProvision();
				JOptionPane.showMessageDialog(frame,"TODO", "TODO", JOptionPane.WARNING_MESSAGE);
			}
		};
		itemCerca.addActionListener(findListener);
			
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
		
		menubar.add(menu);
		menubar.add(menu1);
		frame.setJMenuBar(menubar);
		frame.add(toolbar,BorderLayout.NORTH);
	}
	
	private void createMainPanel(final ProvisionFrame frame)
	{
		//PER CREARE PIU'ELEMENTI LEGATI "BENE" ASSIEME GUARDA SPRINGLAYOUT O JTABBEDPANE PER FARLI IN TAB DIVERSI
		//creo l'area di testo principale TODO riguarda ogni cosa
		Container contentPane=frame.getContentPane();//creo il pannello posto nell'area centrale
		panel=new JPanel();
		JScrollPane mainPaneLeft=new JScrollPane(panel);//creo lo scroller contenente la'rea di testo
		contentPane.add(mainPaneLeft,BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		subPanel=new JPanel();
		Border border=BorderFactory.createEmptyBorder();
		subPanel.setBorder(border);
		JScrollPane scroll=new JScrollPane(frame.text);
		subPanel.add(scroll);
		panel.add(subPanel);
		document=text.getDocument();//Imposto la variabile di classe document ad essere il document della textarea*/
		
		
		
		//creo il gestore del document ,utile per vedere quando viene modificata l'area di testo principale
		
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
				modified=true; //indico che il lavoro ha subito modifiche dall'ultimo salvataggio
				System.out.println("rimozione");
				return;
			}
		}
		document.addDocumentListener(new DocumentGesture());
		
		provisionPanel=new JPanel();
		provisionPanel.setLayout(new FlowLayout());
		//creo l'albero dei raggruppamenti TODO gestirlo
		//tree=new JTree();
	//	tree=new JTree(root);
	//	tree.setEditable(true);
		//tree.setRootVisible(true);
		provisionTree.addTreeSelectionListener(new SelectionListener());
		JScrollPane scroller1=new JScrollPane(provisionTree); //SCROLLER CONTENENTE L'ALBERO
		scroller1.setSize(provisionPanel.getSize());
		//Border border1=BorderFactory.createLineBorder(Color.black, 2);
		//subPanel.setBorder(border);
		provisionPanel.add(scroller1);
		panel.add(provisionPanel);
	
	}

	//TODO CREARE METODI PER CREARE NUOVE DISPOSIZIONI
	//METODI PER SETTARE GLI ARGOMENTI
	/*protected void setDest(String result){
		dest=result;
	}
	protected void setCounter(String result){
		counter=result;
	}
	protected void setAction(String result){
		actionS=result;
	}
	protected void setObject(String result){
		object=result;
	}
	protected void setTextLaw(String result){
		textLaw=result;
	}
	protected void setEffect(String result){
		effect=result;
	}
	protected void setPenalty(String result){
		penalty=result;
	}
	protected void setDefiniendum(String result){
		definiendum=result;
	}
	protected void setDefiniens(String result){
		definiens=result;
	}
	protected void setActivity(String result){
		activity=result;
	}*/
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
     	this.model.write(bout);
     	this.text.setText(new String(bout.toByteArray()));
	}
	
	 //	CREA UNA NUOVA DISPOSIZIONE ASSOCIANDOGLI UN NOME UNIVOCO
	//ONT TIPO (CLASSE) DI DISPOSIZIONE, PROPERTIES NOME DEGLI ARGOMENTI, PARAM VALORI ARGOMENTI (TESTO COMPRESO)
	public Provision createProvision(OntClass ont, String []properties,String []param){
		//PENSACI 
		Provision prov=new Provision(ont);
		prov.setID(createID(prov.getType())); //CREO IL NOME DELLA DISPOSIZIONE INTERROGANDO IL TIPO DELLA DISPOSIZIONE
		for(int i=0;i<=param.length-2;i++){//LENGTH-2 PERCHE' UNO E' IL TESTO CHE LO AGGIUNGO A PARTE, E 1 PERCHE' GLI ELEMENTI SONO LENGTH-1
			prov.createArguments(properties[i], param[i]);
		}
		prov.setText(param[param.length-1]);//IMPOSTO IL TESTO DELLA DISPOSIZIONE
		addProvision(prov);
		//subPanel.add(tree);
		model.createIndividual("http://provisions.org/model/1.0#"+prov.getID(),prov.getOntClass());//CREO UN'ISTANZA NEL MODELLO DI OUTPUT
		writeOnScreen();
		//AGGIUNGO LA DISPOSIZIONE ALL'ALBERO (per ora come elemento figlio della radice)
		DefaultMutableTreeNode node=new DefaultMutableTreeNode(prov.getID());
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)provisionTree.getModel().getRoot();		
		//CERCO IL NODO PADRE DELL'ISTANZA DI DISPOSIZIONE CHE VOGLIO INSERIRE
		DefaultMutableTreeNode father=new DefaultMutableTreeNode();
		Enumeration<DefaultMutableTreeNode> e=rootNode.children();
		while(e.hasMoreElements()){
			father=(DefaultMutableTreeNode)e.nextElement();
			String typeOfProvision=(String)father.toString();//getUserObject();
			if(typeOfProvision.equals(prov.getType())){//HO TROVATO IL NODO PADRE, INSERISCO LA DISPOSIZIONE COME FIGLIO
				//SE IL NODO PADRE HA COME FIGLIO IL NODO ETICHETTATO COME BLANK, ALLORA LO ELIMINO
				if(father.getChildCount()==1&father.getChildAt(0).toString().equals("Blank")){
					father.removeAllChildren();
				}
				father.add(node);
				//((DefaultTreeModel)provisionTree.getModel()).reload();
			}		
		}
		//AGGIUNGO AL NODO CREATO I NODI RELATIVI AGLI ARGOMENTI
		java.util.Enumeration<String> keys=prov.getKeys();
		String name,value;
		while(keys.hasMoreElements()){	
			name=keys.nextElement();
			value=prov.getArgumentValue(name);
			node.add(new DefaultMutableTreeNode(name+": "+value));
		}
		//AGGIUNGO A PARTE IL TESTO DELLA DISPOSIZIONE
		String content=new String("Testo: "+prov.getText());
		DefaultMutableTreeNode argument=new DefaultMutableTreeNode(content);
		node.add(argument);
		//AGGIORNO IL DISEGNO DELL'ALBERO
		((DefaultTreeModel)provisionTree.getModel()).reload();
		//subPanel.repaint();
		return prov;
	}
	
	//	CREA UN ID PER UNA NUOVA DISPOSIZIONE
	private String createID(String type){
		int subfix;
		String ID=null;
		boolean duplicate=true;
		while(duplicate){
			subfix=(int)(range*Math.random());
			ID=type+subfix;
			System.out.println(ID);
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
	
	public void deleteProvision(Provision p){
		//TODO CANCELLA DALL'ALBERO
		String ID=p.getID();
		Provision temp;
		for(int i=0;i<=provisions.capacity();i++){
			temp=provisions.elementAt(i);
			if(temp.getID().equals(ID)){
				provisions.remove(i);
				break;
			}
		}
	}
	public void deleteProvision(String ID){ //ID DELLA DISPOSIZIONE DA ELIMINARE
		//TODO CANCELLA DALL'ALBERO
		Provision temp;
		//LA ELIMINO DAL VETTORE DELLE DISPOSIZIONI
		for(int i=0;i<=provisions.capacity();i++){
			temp=provisions.elementAt(i);
			if(temp.getID().equals(ID)){
				provisions.remove(i);
				break;
			}
		}
		//LA ELIMINO DALL'ALBERO
		
	}
	
	public void listProvision(){// CAMBIA TUTTO, RIMUOVE GLI ELEMENTI SEMPLICEMENTE PER STAMPARLI
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
		    return fname.endsWith("rdf");
		  }

		  public String getDescription() {
		    return "File RDF";
		  }
		}
	private class SelectionListener implements TreeSelectionListener{
		javax.swing.JPopupMenu pop;
		ProvisionFrame app;
		public void valueChanged(TreeSelectionEvent e,ProvisionFrame frame){
			app=frame;
			valueChanged(e);
		}
		public void valueChanged(TreeSelectionEvent e){
			JTree tree=(JTree)e.getSource();
			DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if(selectedNode!=null){//UTILE PER NON AVERE SITUAZIONE CON NODO NULLO QUANDO SI CREA UNA SECONDA DISPOSIZIONE
				if(selectedNode.getDepth()==1){//CONTROLLO CHE IL NODO SIA UNO DEI TIPI DI DISPOSIZIONE
					if(selectedNode.getChildCount()!=1||!selectedNode.getFirstChild().toString().equals("Blank")){//CONTROLLO CHE L'UNICO FIGLIO NON SIA IL NODO BLANK
						// IN CASO FAVOREVOLE HO UN NODO RELATIVO AD' UN ISTANZA DI UN TIPO DI DISPOSIZIONE
						new TreeWindow(selectedNode,app);
					/*pop=new JPopupMenu();
					JMenuItem delete=new JMenuItem("Cancella");
					JMenuItem modify=new JMenuItem("Modifica");
					pop.add(delete);
					pop.add(modify);
					provisionPanel.setComponentPopupMenu(pop);
					
					pop.show(provisionTree,MouseInfo.getPointerInfo().getLocation().x,MouseInfo.getPointerInfo().getLocation().y);*/
					//pop.setVisible(true);
					//((DefaultTreeModel)provisionTree.getModel()).reload();
					}
				}
			}
		}		
	}

}
