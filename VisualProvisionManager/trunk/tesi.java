package it.cnr.ittig.VisualProvisionManager;

import java.awt.Container;
import java.awt.EventQueue;

import java.awt.event.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import it.cnr.ittig.ProvisionModel.*;




public class tesi {
	private static JFrame frame;
	private static JPanel panel;
	private static OntModel model=ModelFactory.createOntologyModel();
	private static String savedPath;//UTILE PER VEDERE SE UN DATO FILE E' GIA' STATO SALVATO ED IN CHE PATH 
	private static Document document=null;
	private static JTextArea text;
	private static JPanel subPanel,subPanel1;
	private static boolean modified=false; //indica se il lavoro ha subito modifiche dall'ultimo salvataggio
	private static boolean init=false; //indica se il documento ha subito una qualsiasi operazione o se non � mai stato usato. Utile per quando 
	//si apre un nuovo documento con il documento iniziale mai modificato
	public static void main(String [] args){
		Runnable runner= new Runnable(){
			public void run(){
				frame= createInterface(); 
			};
		};
		EventQueue.invokeLater(runner);
		};
	
	public static JFrame createInterface(){
		frame= new JFrame("Applicazione");
		//TODO non chiede conferma o salvataggio del lavoro
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//imposto le dimensioni della finesta basate sulle dimensioni dello schermo/2
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Dimension screen=toolkit.getScreenSize();
		int larghezza=(int) screen.getWidth()/2;
		int altezza=(int) screen.getHeight()/2;
		frame.setSize(larghezza,altezza);
		frame.setLocation(larghezza/2,altezza/2);
		
		//Creo la barra dei menu
		JMenuBar menubar= new JMenuBar();
		
		//Creo le voci dei men� e relativi mnemonici
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
		JMenuItem itemA=new JMenuItem("Copia",KeyEvent.VK_C);
		KeyStroke ctrlCKeyStroke = KeyStroke.getKeyStroke("control C");
		itemA.setAccelerator(ctrlCKeyStroke);
		JMenuItem itemA1=new JMenuItem("Taglia",KeyEvent.VK_T);
		KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control X");
		itemA1.setAccelerator(ctrlXKeyStroke);
		JMenuItem itemA2=new JMenuItem("Incolla",KeyEvent.VK_I);
		KeyStroke ctrlVKeyStroke = KeyStroke.getKeyStroke("control V");
		itemA2.setAccelerator(ctrlVKeyStroke);
		JMenu insertMenu=new JMenu("Inserisci"); //menu per l'inserimento dei vari tipi di disposizioni
		insertMenu.setMnemonic(KeyEvent.VK_N);
		JMenu constitutiveMenu=new JMenu("Constituive rule");
		JMenu regulativeMenu=new JMenu("Regulative rule");
		JMenu definition=new JMenu("Definition");
		JMenu creation=new JMenu("Creation");
		JMenu attribution=new JMenu("Attribution");
		JMenu action=new JMenu("Action");
		JMenu remedy=new JMenu("Remedy");
		JMenuItem termItem=new JMenuItem("Term",KeyEvent.VK_T);
		JMenuItem procedureItem=new JMenuItem("Procedure",KeyEvent.VK_P);
		JMenuItem establishmentItem=new JMenuItem("Establishment",KeyEvent.VK_E);
		JMenuItem organizationItem=new JMenuItem("Organization",KeyEvent.VK_O);
		JMenuItem powerItem=new JMenuItem("Power",KeyEvent.VK_P);
		JMenuItem liabilityItem=new JMenuItem("Liability",KeyEvent.VK_L);
		JMenuItem statusItem=new JMenuItem("Status",KeyEvent.VK_S);
		JMenuItem rightItem=new JMenuItem("Right",KeyEvent.VK_R);
		JMenuItem dutyItem=new JMenuItem("Duty",KeyEvent.VK_D);
		JMenuItem prohibitionItem=new JMenuItem("Prohibition",KeyEvent.VK_P);
		JMenuItem permissionItem=new JMenuItem("Permission",KeyEvent.VK_E);
		JMenuItem redressItem=new JMenuItem("Redress",KeyEvent.VK_R);
		JMenuItem violationItem=new JMenuItem("Violation",KeyEvent.VK_V);
		
		
		//Listener per chiudere l'applicazione (non fa salvare i dati)
		ActionListener actionExit=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int conferma = JOptionPane.showConfirmDialog(frame,"Sei sicuro di voler uscire?","Termina l'applicazione",JOptionPane.YES_NO_OPTION);
				if(conferma==0){
					if(modified)
					{
						conferma=JOptionPane.showConfirmDialog(frame,"Vuoi salvare il lavoro?","Salva",JOptionPane.YES_NO_OPTION);
						if(conferma==0)
						{
							saveFileWithName();
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
						System.out.println("ffff");
					}
					else {if(modified)
					{
						conferma=JOptionPane.showConfirmDialog(frame,"Vuoi salvare il lavoro?","Salva",JOptionPane.YES_NO_OPTION);
					}
					if(conferma==0)
					{
						saveFileWithName();
					}
				}
				System.out.println(text.getText());document=null;
				text.setText("");
				init=false;
				modified=false;
				savedPath=null;
				//text=null;
				//text=new JTextArea();
				//subPanel.add(text);
				//System.out.println("Nuovo documento creato");
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
						System.out.println("ffff");
					}
					else {if(modified)
					{
						conferma=JOptionPane.showConfirmDialog(frame,"Vuoi salvare il lavoro?","Salva",JOptionPane.YES_NO_OPTION);
					}
					if(conferma==0)
					{
						saveFileWithName();
					}
				}
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new RDFFileFilter());
				int status=chooser.showOpenDialog(frame);
				if (status == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					String path=selectedFile.getPath();
					System.out.println("Selezionato " + path);
					InputStream in = FileManager.get().open(path);
			        if (in == null) {
			        	JOptionPane.showMessageDialog(frame,"Errore nell'apertura del file", "Errore", JOptionPane.ERROR_MESSAGE);
			            throw new IllegalArgumentException( "File: " + path + " not found");
			        }else{
			        	// lettura del file aperto
			        	//model = ModelFactory.createDefaultModel();
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
		
		
		
		//Listener per salvare un File con nome//TODO salvare realmente un file adesso salvo solo l'intestazione rdf, far partire il file chooser da una directory particolare
		ActionListener actionSaveWithName=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				saveFileWithName();
			}
		};
		//Listener per salvare un File//TODO salvare realmente un file adesso salvo solo l'intestazione rdf, far partire il file chooser da una directory particolare
		ActionListener actionSave=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				saveFile();
			}
		};
		
		
		item2.addActionListener(actionSave); 
		item3.addActionListener(actionSaveWithName);
		
		//creo la toolbar
		JToolBar toolbar=new JToolBar("Toolbar");
		JSeparator toolBarSeparator = new JToolBar.Separator(new Dimension(10,10));
		toolbar.add(toolBarSeparator,JToolBar.HORIZONTAL);
		JButton newButton=new JButton("Nuovo");
		toolbar.add(newButton);
		newButton.addActionListener(actionNew);
		JButton openButton=new JButton("Apri");
		openButton.addActionListener(actionOpen);
		toolbar.add(openButton);
		JButton saveButton=new JButton("Salva");
		saveButton.addActionListener(actionSave);
		toolbar.add(saveButton);
		
		//creazione del menu inserimento
		
				
		//aggiungo gli item al men�
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
		insertMenu.add(regulativeMenu);
		insertMenu.add(constitutiveMenu);
		constitutiveMenu.add(definition);
		constitutiveMenu.add(creation);
		constitutiveMenu.add(attribution);
		regulativeMenu.add(action);
		regulativeMenu.add(remedy);
		definition.add(termItem);
		definition.add(procedureItem);
		creation.add(establishmentItem);
		creation.add(organizationItem);
		attribution.add(powerItem);
		attribution.add(liabilityItem);
		attribution.add(statusItem);
		action.add(rightItem);
		action.add(dutyItem);
		action.add(prohibitionItem);
		action.add(permissionItem);
		remedy.add(redressItem);
		remedy.add(violationItem);
		
		//imposto la menubar ed aggiungo i menu alla menu bar 
		menubar.add(menu);
		menubar.add(menu1);
		frame.setJMenuBar(menubar);
		JSeparator separator=new JSeparator();
		//frame.add(separator);
		frame.add(toolbar,BorderLayout.NORTH);
		
		//creo l'area di testo principale
		Container contentPane=frame.getContentPane();//creo il pannello posto nell'area centrale
		panel=new JPanel();
		//panel.setPreferredSize(contentPane.getSize());
		text=new JTextArea(30,30); //TODO CAMBIA, FALLA DI DIMENSIONI "AUTOIMPOSTANTI"
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		JScrollPane scroll=new JScrollPane(text);
		System.out.println ("dimensione "+contentPane.getSize());
		contentPane.add(panel,BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		subPanel=new JPanel();
		Border border=BorderFactory.createLineBorder(Color.black, 5);
		subPanel.setBorder(border);
		subPanel.add(scroll);
		panel.add(subPanel);
		System.out.println("Dimensione subpanel"+subPanel.getSize());
		document=text.getDocument();//Imposto la variabile di classe document ad essere il document della textarea
		
		
		
		//creo il gestore del document //TODO farli fare qualcosa, per ora non fa nulla
		
		class DocumentGesture implements DocumentListener{
			public void changedUpdate(DocumentEvent e){
				init=true; //indico che il documento ha subito una qualsiasi operazione
				modified=true; //indico che il lavoro ha subito modifiche dall'ultimo salvataggio
				System.out.println("CAmbiamento");
			}
			public void insertUpdate(DocumentEvent e){
				init=true; //indico che il documento ha subito una qualsiasi operazione
				modified=true; //indico che il lavoro ha subito modifiche dall'ultimo salvataggio
				System.out.println("Inserimento");
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
		//creo l'albero dei raggruppamenti TODO gestirlo
		JTree tree=new JTree();
		tree.setEditable(true);
		subPanel1=new JPanel();
		Border border1=BorderFactory.createLineBorder(Color.black, 2);
		subPanel.setBorder(border);
		subPanel1.add(tree);
		//panel.add(subPanel1);
		 
		frame.setVisible(true);
		return frame;
		
	}
	private static void saveFile()
	{
		File selectedFile;
		FileWriter writer=null;
		if(savedPath!=null){
			System.out.println("Risalvataggio");
			selectedFile=new File(savedPath);
			try{
				 writer=new FileWriter(selectedFile);
			}
			catch (Exception ex){
				JOptionPane.showMessageDialog(frame,"Errore nel salvataggio del file", "Errore", JOptionPane.ERROR_MESSAGE);
			}
			if(writer != null){
				model.write(writer);
				modified=false; //indico che il lavoro non ha subito modifiche dall'ultimo salvataggio
				System.out.println(savedPath);
			}
			
		}
		else
		{
			saveFileWithName();
		}
	}

	
	private static void saveFileWithName() //salva un documento su un nuovo file TODO salvare per bene il documento
	{
		final JFileChooser chooser1 = new JFileChooser();
		chooser1.setFileFilter(new RDFFileFilter());
		File selectedFile;

		int status=chooser1.showSaveDialog(frame);
		if (status == JFileChooser.APPROVE_OPTION) {
			 selectedFile = chooser1.getSelectedFile();
			String path=selectedFile.getPath();
			//SE IL FILE HA ESTENSIONE RDF, LA TRASFORMO IN rdf
			if(path.endsWith(".RDF"))
			{
				//System.out.println("path ends with RDF");
				int length=path.length()-4;
				path=path.substring(0,length);
				System.out.print(path);
				path=path+".rdf";
				selectedFile=new File(path);
			}
			//SE IL FILE NON HA ESTENSIONE rdf, la aggiungo TODO possibili casi con due estensioni, come gestirli?
			if(!path.endsWith(".rdf")){
				path=path+".rdf";
				selectedFile=new File(path);
				//System.out.print("path not ends with rdf");
			}
			System.out.println("Selezionato " + path );
			FileWriter writer=null;
			try{
				 writer=new FileWriter(selectedFile);
			}
			catch (Exception ex){
				JOptionPane.showMessageDialog(frame,"Errore nel salvataggio del file", "Errore", JOptionPane.ERROR_MESSAGE);
			}
			if(writer != null){
				// model= ModelFactory.createDefaultModel();
				model.write(writer);
				modified=false; //indico che il lavoro non ha subito modifiche dall'ultimo salvataggio
				//AGGIORNO IL SAVED PATH 
				savedPath=path;
				System.out.println(savedPath);
			}
		}if(status == JFileChooser.ERROR_OPTION){
		// TO DO GESTIRE LA SITUAZIONE DI ERRORE
		}
		else{
			//SCELGO ANNULLA, NON FARE NIENTE
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
}
