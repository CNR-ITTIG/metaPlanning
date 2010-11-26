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
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
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
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class ProvisionFrame extends JFrame{
	private JPanel panel;
	private ProvisionModelFactory provisionModelFactory=new ProvisionModelFactory();
	private OntModel model; //TOGLIERE? PROBLEMA CON SOTTOCLASSI (USA TRANSITIVITA') E PROPRIETA' DELLE CLASSI(PROPRIETA' CONDIVISE NON RESTITUITE)
	private OntModel modelOutput;	//MODEL DI OUTPUT DOVE VERRANNO SALVATE LE ISTANZE CREATE
	private String savedPath;//UTILE PER VEDERE SE UN DATO FILE E' GIA' STATO SALVATO ED IN CHE PATH 
	private Document document=null;
	private JTextArea text;
	private JTree provisionTree;//ALBERO DELLE DISPOSIONI
	private Vector <TreePath> nodeExpanded=new Vector<TreePath>(); //CONTIENE TUTTI I NODI ESPANSI
	private DefaultMutableTreeNode radice=new DefaultMutableTreeNode("");
	private boolean modified=false; //indica se il lavoro ha subito modifiche dall'ultimo salvataggio
	private boolean init=false; //indica se il documento ha subito una qualsiasi operazione o se non è mai stato usato. Utile per quando
		//si apre un nuovo documento con il documento iniziale mai modificato
	private Vector <Provision> provisions=new Vector<Provision>();
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
		InputStream in = FileManager.get().open("C:/ProvisionModel.rdf"); 
		if (in == null) {
		    	throw new IllegalArgumentException("File non trovato");		                                 
		}
		model=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
		model.read(in,"RDF/XML-ABBREV");
		//model=provisionModelFactory.getProvisionModel(); //MEGLIO DI NO, ALMENO E' PIU' GENERALE
		modelOutput=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM,null);//CREO IL MODELLO DI OUTPUT
		//modelOutput.addSubModel(model);//AGGIUNGO AL MODELLO DI OUTPUT LE DEFINIZIONI DEL MODELLO BASE
		modelOutput=model; //CON QUELLO SOPRA DA ALCUNI PROBLEMI
		//model.addLoadedImport("C:/ProvisionModel.rdf");
		/*OntDocumentManager manager=new OntDocumentManager();
		manager.loadImport(modelOutput,"C:/ProvisionModel.rdf")*/;
		/*InputStream in = FileManager.get().open("C:/ProvisionModel.rdf"); //VECCHIA VERSIONE SBAGLIATA
		modelOutput.read(in,"RDF/XML-ABBREV");*/
		ExtendedIterator r=modelOutput.listSubModels();
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
		//setPreferredSize(preferredDimension);
		//setLocation(larghezza/2,altezza/2);
			setLocation(0,0);
			provisionTree.setPreferredSize(dimension);
		}
		//text.setPreferredSize(halfMainPanelDimension);
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
				insertMenu(frame,figlio,menuFiglio);				
			}else{
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
						InsertWindow win=new InsertWindow(frame,model,figlio1); //SE NON TORNA MODELBASE
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
		ExtendedIterator <OntClass>  iter=OntUtils.getTopClasses(model);//CERCO LA CLASSE DI GERARCHIA PIU' ALTA(FUNZIONA SE NE ESISTE SOLO 1 INSERIMENTO NELL'ALBERO)
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
					radice.setUserObject(getProvisionType(ont));
					reloadTree();
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
		panel=new JPanel();//PARTE SX DEL PANNELLO
		panel.setLayout(new GridLayout(1,1));
		//panel.setLayout(new FlowLayout());
		JPanel panel1=new JPanel();//PARTE DX DEL PANNELLO
		panel1.setLayout(new GridLayout(1,1));
		//panel1.setLayout(new FlowLayout());
	//	JPanel subPanel=new JPanel();
		//subPanel.setLayout(new FlowLayout());
		JScrollPane textScroller=new JScrollPane(frame.text);//SCROLLER CONTENENTE AREA DI TESTO
		//subPanel.add(textScroller);
		panel.add(textScroller);
		//JScrollPane scrollerLeft=new JScrollPane(subPanel);//SCROLLER DELLA PARTE SX DEL PANNELLO PRINCIPALE
		//JScrollPane scroller=new JScrollPane(frame.text);
		document=text.getDocument();//AGGIUNGO LO SCROLLER DELL'AREA DI TESTO ALLA PARTE SX
	//	panel.add(scrollerLeft);//AGGIUNGO LO SCROLLER DELL'AREA DI TESTO ALLA PARTE SX
		JScrollPane treeScroller=new JScrollPane(provisionTree);//SCROLLER CON L'ALBERO DELLE DISPOSIZIONI
		//JPanel subPanel1=new JPanel();
		//subPanel1.setLayout(new FlowLayout());
		//subPanel1.add(treeScroller);
		panel1.add(treeScroller);//AGGIUNGO LO SCROLLER DELL'ALBERO ALLA PARTE DX
		//panel1.add(subPanel1);//AGGIUNGO LO SCROLLER DELL'ALBERO ALLA PARTE DX
		//JScrollPane scrollerRight=new JScrollPane(panel1);
		provisionTree.addTreeSelectionListener(new SelectionListener(frame));
		provisionTree.addTreeWillExpandListener(new TreeExpandListener());
		//provisionTree.addMouseListener(new TreeMouseListener(frame));
		JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1,panel);
		Dimension frameSize=frame.getSize();
		int location=frame.getLocation().x+(int)frameSize.getWidth()/2;
		split.setDividerLocation(location);
		contentPane.add(split);
		
		

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
		writeOnScreen();
		//AGGIUNGO LA DISPOSIZIONE ALL'ALBERO (per ora come elemento figlio di un figlio della radice)
		DefaultMutableTreeNode node=new DefaultMutableTreeNode(prov.getID());
		DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)provisionTree.getModel().getRoot();		
		//CERCO IL NODO PADRE DELL'ISTANZA DI DISPOSIZIONE CHE VOGLIO INSERIRE
		DefaultMutableTreeNode father=new DefaultMutableTreeNode();
		Enumeration<DefaultMutableTreeNode> e=rootNode.children();
		while(e.hasMoreElements()){
			father=(DefaultMutableTreeNode)e.nextElement();
			String typeOfProvision=(String)father.toString();
			if(typeOfProvision.equals(prov.getType())){//HO TROVATO IL NODO PADRE, INSERISCO LA DISPOSIZIONE COME FIGLIO
				//SE IL NODO PADRE HA COME FIGLIO IL NODO ETICHETTATO COME BLANK, ALLORA LO ELIMINO
				if(father.getChildCount()==1&father.getChildAt(0).toString().equals("Blank")){
					father.removeAllChildren();
				}
				father.add(node);
				//((DefaultTreeModel)provisionTree.getModel()).reload();
			}		
		}
		//CREO L'INDIVIDUO NEL FILE RDF
		Individual ind;
		ind=modelOutput.createIndividual("http://provisions.org/model/1.0#"+prov.getID(),prov.getOntClass());//CREO UN'ISTANZA NEL MODELLO DI OUTPUT
		System.out.println("Nome    "+ind.getLocalName());
		System.out.println(ind.getURI()+"   "+ind.getClass());
		//SOPRA SOSTITUISCI LA STRING HTTP..... COL METODO GETNS()
		//AGGIUNGO AL NODO CREATO I NODI RELATIVI AGLI ARGOMENTI
		java.util.Enumeration<String> keys=prov.getKeys();
		String name,value;
		while(keys.hasMoreElements()){	
			name=keys.nextElement();
			value=prov.getArgumentValue(name);
			node.add(new DefaultMutableTreeNode(name+": "+value));
			//AGGIUNGO LA PROPRIETA' AL FILE RDF
			Literal literal=modelOutput.createTypedLiteral("http://provisions.org/model/1.0#"+value);
			Resource res=modelOutput.createResource("http://provisions.org/model/1.0#"+value);
			System.out.println("La proprietà esiste??????"+ind.hasProperty(model.getProperty("http://provisions.org/model/1.0"+name)));
			System.out.println("Inserire la proprietà "+modelOutput.getProperty(name)+" perchè" +ind.hasProperty(modelOutput.getProperty(name)));
			ind.setPropertyValue(modelOutput.getProperty("http://provisions.org/model/1.0#"+name),res);
		//ind.addLiteral(modelOutput.getProperty(name),"http://provisions.org/model/1.0"+value);//"http://provisions.org/model/1.0"+value);//ERRORE BADURIEXCEPTION
			System.out.println("fsdjkfsdj"+modelOutput.getProperty("http://provisions.org/model/1.0sbuibbo"));
			//ind.addProperty(modelOutput.getProperty("http://provisions.org/model/1.0"+name),literal);//FUNZIONA (O QUASI)
			System.out.println("La proprietà risulta"+ind.getPropertyValue(model.getProperty("http://provisions.org/model/1.0"+name)));
			StmtIterator prop=ind.listProperties();
			while(prop.hasNext()){
				System.out.println("Proprietà"+prop.next().toString());
				//System.out.println("Letterale "+modelOutput.createTypedLiteral(value));
				//ind.setPropertyValue((OntProperty)prop.next(),literal);
				
			}
			writeOnScreen();
		}
		//AGGIUNGO A PARTE IL TESTO DELLA DISPOSIZIONE
		DefaultMutableTreeNode argument=new DefaultMutableTreeNode("Testo: "+prov.getText());
		node.add(argument);
		//AGGIORNO IL DISEGNO DELL'ALBERO
		//((DefaultTreeModel)provisionTree.getModel()).reload();
		reloadTreeAfter();
		return prov;
	}
	
	public void reloadTree(){//AGGIORNA L'ALBERO DELL'APPLICAZIONE
		int[] expandedRow=new int[ radice.getChildCount()];
		System.out.println("Nodi	"+radice.getChildCount());
		for(int i=0;i<radice.getChildCount();i++){
			if(!provisionTree.isCollapsed(i)){//1 LA RIGA è ESPANSA
				System.out.println("Riga"+i);
				expandedRow[i]=1;
			}else{
				expandedRow[i]=0;//LA RIGA E' COLLASSATA
			}
		}
		((DefaultTreeModel)provisionTree.getModel()).reload();
		for(int i=0;i<expandedRow.length;i++){
			if(expandedRow[i]==1){//OGNI RIGA PRECEDENTEMENTE ESPANSA, VIENE NUOVAMENTE ESPANSA
				System.out.println("Riga che espando"+i);
				provisionTree.expandRow(i);	
			}
		}
	}
	
	public void reloadTreeAfter(){
		((DefaultTreeModel)provisionTree.getModel()).reload();
		for(int i=0;i<nodeExpanded.size();i++){
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
				System.out.println("Elimino la"+provisions.remove(i).toString());
				//RIMUOVO DAGLI ID USATI L'ID DELLA DISPOSIZIONE ELIMINATA
				for(int j=0;j<usedID.size();j++){
					if(usedID.elementAt(j).equals(ID)){
						usedID.remove(j);
						System.out.println("Rimuovo");
					}
				}
				break;
			}
		}
		//	CONTROLLO QUELLE CHE CI SON RIMASTE (INUTILE LE STAMPO PER VEDERE CHE TORNI TUTTO, POSSO TOGLIERLO)
		for(int i=1;i<=provisions.size();i++){
			if(provisions.isEmpty()){//DOVREBBE ESSERE INUTILE
				break;
			}else{
				Provision p=provisions.elementAt(i-1);
				System.out.println(p.getID());
			}
		}//SE IL PADRE NON HA FIGLI AGGIUNGO IL NODO BLANK
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
		System.out.println(path.toString());
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
		//SE ELENCO TUTTI GLI INDIVIDUI DEL MODELLO NON FA, PERCHE' LA LORO CLASSE E NELL'ALTRO MODELLO
		Individual indRemoved=null;
		//TODO TOGLI LA STRING HTTP....SOTTO E METTI L'NS
	/*	indRemoved=modelOutput.getIndividual("http://provisions.org/model/1.0#"+temp.getID());
		if(!(indRemoved==null)){
			indRemoved.remove();
			writeOnScreen();
		}*/
		//VERSIONE  FUNZIONANTE SE INDIVIDUI E DEFINIZIONE DELLE CLASSI SONO NELLO STESSO MODELLO
		ExtendedIterator <Individual> iterIndividual=modelOutput.listIndividuals();
		if(!iterIndividual.hasNext()){System.out.println("Vuoto");}
		while(iterIndividual.hasNext()){
			indRemoved=(Individual)iterIndividual.next();
			if(indRemoved.getLocalName().equals(temp.getID())){//rimuovo eventuali statement che riguardano l'elemento
				  indRemoved.remove();  
				/*for(ExtendedIterator si = modelOutput.listAllOntProperties(); si.hasNext(); ){
					   //Statement stmt = (Statement)si.next();
					   //System.out.println("Adesso rimuovo"+stmt.toString());
					   //modelOutput.remove(stmt);
					  }*/
			}
			//modelOutput.removeAll(indRemoved,null,null);// rimuovo anche l'elemento
		}
		writeOnScreen();
	}
	
	public Provision searchProvision(String ID){
		Provision temp;
		if(provisions.isEmpty()){
			return null;
		}
		//LA CERCO NEL VETTORE DELLE DISPOSIZIONI
		for(int i=0;i<=provisions.size();i++){
			temp=provisions.elementAt(i);
			if(temp.getID().equals(ID)){
				return temp;
			}
		}
		return null;
	}
	
	//ID DELLA DISP. DA MODIFICARE, PROPERYVALUE I VLORI,PROPERTYNAME NOMI DELLE PROPRIETà, NODE IL NODO DA MODIFICARE NELL'ALBERO
	public void modifyProvision(String ID, String[] propertyName,String []propertyValue,DefaultMutableTreeNode node){
		//LA MODIFICO NELL'ALBERO IN TREEWINDOW
		Provision prov=searchProvision(ID);
		if(prov==null){//NON DOVREBBE ESSER MAI VERO, SIGNIFICA CHE LA DISPOSIZIONE NON ESISTE
			return;
		}
		//System.out.println("Abbiamo la bellezza di "+propertyName.length+" argomenti");
		for(int i=0;i<=propertyName.length-2;i++){//LENGTH-2 PERCHE' UNO E' IL TESTO CHE LO AGGIUNGO A PARTE, E 1 PERCHE' GLI ELEMENTI SONO LENGTH-1
			prov.modifyArguments(propertyName[i], propertyValue[i]);
		}
		prov.setText(propertyValue[propertyValue.length-1]);
		System.out.println(prov.toString());//CONTROLLO VISIVO CHE TUTTO è OK
		//reloadTreeAfterModification(node);
		//int properties=node.getChildCount();
		int properties=prov.numberOfArguments()+1;
		node.removeAllChildren();
		DefaultMutableTreeNode child;
		String content=null;
		for(int i=1;i<=properties;i++){
			System.out.println("Ciclo numero"+i);
			content=propertyName[i-1]+": "+propertyValue[i-1];
			child=new DefaultMutableTreeNode(content);
			node.add(child);
		}
		reloadTreeAfter();
		//MODIFICO NEL FILE RDF
		Individual ind=null;
		//TODO TOGLI LA STRING HTTP....SOTTO E METTI L'NS
		ind=modelOutput.getIndividual("http://provisions.org/model/1.0#"+prov.getID());
		ind.remove();
		ind=modelOutput.createIndividual("http://provisions.org/model/1.0#"+prov.getID(),prov.getOntClass());
		if(!(ind==null)){
			//TODO modifica o cancellazione e nuovo inserimento
			java.util.Enumeration<String> keys=prov.getKeys();
			String name,value;
			while(keys.hasMoreElements()){	
				name=keys.nextElement();
				value=prov.getArgumentValue(name);
				node.add(new DefaultMutableTreeNode(name+": "+value));
				//AGGIUNGO LA PROPRIETA' AL FILE RDF
				Literal literal=modelOutput.createTypedLiteral("http://provisions.org/model/1.0#"+value);
				Resource res=modelOutput.createResource("http://provisions.org/model/1.0#"+value);
				System.out.println("La proprietà esiste??????"+ind.hasProperty(model.getProperty("http://provisions.org/model/1.0"+name)));
				System.out.println("Inserire la proprietà "+modelOutput.getProperty(name)+" perchè" +ind.hasProperty(modelOutput.getProperty(name)));
				ind.setPropertyValue(modelOutput.getProperty("http://provisions.org/model/1.0#"+name),res);
				System.out.println("La proprietà risulta"+ind.getPropertyValue(model.getProperty("http://provisions.org/model/1.0"+name)));
				StmtIterator prop=ind.listProperties();
				while(prop.hasNext()){
					System.out.println("Proprietà"+prop.next().toString());
					//System.out.println("Letterale "+modelOutput.createTypedLiteral(value));
					//ind.setPropertyValue((OntProperty)prop.next(),literal);
					
				}
				writeOnScreen();
			}	
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
		    return fname.endsWith("rdf");
		  }

		  public String getDescription() {
		    return "File RDF";
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
			}
		}		
	}
	
	public class TreeExpandListener implements TreeWillExpandListener{
		@Override
		public void treeWillCollapse(TreeExpansionEvent e)
				throws ExpandVetoException {
			nodeExpanded.remove(e.getPath());
			System.out.println("Path tolto"+e.getPath());
			// TODO Auto-generated method stub
			
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent e)
				throws ExpandVetoException {
			if(!pathPresente(e.getPath().toString())){//AGGIUNGO IL PATH SOLO SE NON GIA' PRESENTE NELL'ELENCO DI QUELLI ESPANSI
				nodeExpanded.add(e.getPath());	
				System.out.println("Path aggiunto"+e.getPath());
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
}
