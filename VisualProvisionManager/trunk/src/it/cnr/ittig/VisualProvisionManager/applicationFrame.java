package it.cnr.ittig.VisualProvisionManager;

import it.cnr.ittig.ProvisionModel.ProvisionModelFactory;
import it.cnr.ittig.VisualProvisionManager.tesi.RDFFileFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class applicationFrame {
	private  static JFrame frame;
	private static JPanel panel;
	private static ProvisionModelFactory provisionModelFactory=new ProvisionModelFactory();
	private static OntModel model=provisionModelFactory.getProvisionModel();
	private static String savedPath;//UTILE PER VEDERE SE UN DATO FILE E' GIA' STATO SALVATO ED IN CHE PATH 
	private static Document document=null;
	private static JTextArea text;
	private static JPanel subPanel,subPanel1;
	private static JDesktopPane desktop = new JDesktopPane();
	private static boolean modified=false; //indica se il lavoro ha subito modifiche dall'ultimo salvataggio
	private static boolean init=false; 
	private static String dest=null;
	private static String counter=null;
	private static String actionS=null;
	private static String object=null;
	private static String textLaw=null;
	private static String effect=null;
	private static String penalty=null;
	private static String definiendum=null;
	private static String definiens=null;
	private static String activity=null;
	private static String[] vec=new String[] {"Pippo","Cluto"};//indica se il documento ha subito una qualsiasi operazione o se non è mai stato usato. Utile per quando 
	//si apre un nuovo documento con il documento iniziale mai modificato
	
	public applicationFrame(){
		
	}
	public static void main(String [] args){
		Runnable runner= new Runnable(){
			public void run(){
				//applicationFrame app=new applicationFrame();
				frame= createInterface(); 
			};
		};
		EventQueue.invokeLater(runner);
		};
	
	public static JFrame createInterface(){
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame= new JFrame("Applicazione");
		//TODO non chiede conferma o salvataggio del lavoro
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		frame.add(desktop,BorderLayout.CENTER);
		text=new JTextArea(30,30); //TODO CAMBIA, FALLA DI DIMENSIONI "AUTOIMPOSTANTI"
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		// NON FUNZIONA PROBLEMA NEL SETTARE L'URIByteArrayOutputStream 
		/*ByteArrayOutputStream bout=null;
		bout=new ByteArrayOutputStream();
        //riscrittura del file su standard output (togliere, per ora controllo almeno gli errori)
     	model.write(bout);
     	text.setText(new String(bout.toByteArray()));*/
		JScrollPane scroll=new JScrollPane(text);
		
		//imposto le dimensioni della finesta basate sulle dimensioni dello schermo/2
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Dimension screen=toolkit.getScreenSize();
		int larghezza=(int) screen.getWidth()/2;
		int altezza=(int) screen.getHeight()/2;
		frame.setSize(larghezza,altezza);
		frame.setLocation(larghezza/2,altezza/2);
		
		//Creo la barra dei menu
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
		constitutiveMenu.setMnemonic(KeyEvent.VK_C);
		regulativeMenu.setMnemonic(KeyEvent.VK_R);
		JMenu definition=new JMenu("Definition");
		JMenu creation=new JMenu("Creation");
		JMenu attribution=new JMenu("Attribution");
		definition.setMnemonic(KeyEvent.VK_D);
		creation.setMnemonic(KeyEvent.VK_C);
		attribution.setMnemonic(KeyEvent.VK_A);
		JMenu action=new JMenu("Action");
		JMenu remedy=new JMenu("Remedy");
		action.setMnemonic(KeyEvent.VK_A);
		remedy.setMnemonic(KeyEvent.VK_R);
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
							FrameUtil.saveFileWithName( frame,  model,  modified,  savedPath);
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
						FrameUtil.saveFileWithName( frame,  model,  modified,  savedPath);
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
						FrameUtil.saveFileWithName( frame,  model,  modified,  savedPath);
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
				FrameUtil.saveFileWithName( frame,  model,  modified,  savedPath);
			}
		};
		//Listener per salvare un File//TODO salvare realmente un file adesso salvo solo l'intestazione rdf, far partire il file chooser da una directory particolare
		ActionListener actionSave=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FrameUtil.saveFile(frame,  model,  modified,  savedPath);
			}
		};
		
		// FUNZIONA ORA PROVO A RISCRIVERLO DIVERSO
		/*ActionListener rightListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,action,object;
				boolean cond=false;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel screen= new JPanel();
				JPanel down=new JPanel();
				screen.setLayout(new BorderLayout());
				//screen.setLayout(new GridLayout(2,1,1,1));
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,2));
				down.setLayout(new GridLayout(2,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire azione");
				JLabel label3=new JLabel("Inserire oggetto");
				JLabel label4=new JLabel("Inserire testo");
				JLabel empty=new JLabel("");
				JLabel empty1=new JLabel("");
				JLabel empty2=new JLabel("");
				JLabel empty3=new JLabel("");
				JTextArea area=new JTextArea(5,10);
				JComboBox box=new JComboBox(vec);
				JComboBox box1=new JComboBox(vec);
				JComboBox box2=new JComboBox(vec);
				JComboBox box3=new JComboBox(vec);
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				JScrollPane scroller=new JScrollPane(area);
				panel.add(label);
				panel.add(empty);
				panel.add(field);
				panel.add(box);
				panel.add(label1);
				panel.add(empty1);
				panel.add(field1);
				panel.add(box1);
				panel.add(label2);
				panel.add(empty2);
				panel.add(field2);
				panel.add(box2);
				panel.add(label3);
				panel.add(empty3);
				panel.add(field3);
				panel.add(box3);
				down.add(label4);
				down.add(scroller);
				//down.setPreferredSize(panel.getSize());
				Dimension size=panel.getSize();
				size.setSize(size.getHeight()/2,size.getHeight()/2);
				//down.setSize(size);
				screen.add(panel,BorderLayout.NORTH);
				//screen.add(panel);
				//screen.add(down);
				//screen.setSize(1, 1);
				//screen.add(new JSeparator(),BorderLayout.CENTER);
				screen.add(label4,BorderLayout.CENTER);
				screen.add(down,BorderLayout.SOUTH);
				int confirm=JOptionPane.showConfirmDialog(frame, screen, "Inserimento nuovo right",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field1.getText())&FrameUtil.stringWithCarachter(field2.getText())&FrameUtil.stringWithCarachter(field3.getText());
					while(!cond)
					{
						confirm=JOptionPane.showConfirmDialog(frame, screen, "Impossibile lasciare alcuni campi vuoti",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
						if(confirm!=JOptionPane.OK_OPTION)
						{
							return;
						}
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field1.getText())&FrameUtil.stringWithCarachter(field2.getText())&FrameUtil.stringWithCarachter(field3.getText());
					}
					dest=field.getText();
					counter=field1.getText();
					action=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+action+" "+ object);
					text.setText("<dest>"+dest+ "<\\dest> \n"+ " "+counter+ " "+action+" "+ object);
				}
			}
		};*/
		
		ActionListener rightListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,actionS,object,textLaw};
				InsertForm ins=new InsertForm(vec,"right");
				System.out.println("Valore destinatario pari a "+dest);//TOGLIERE QUESTE STAMPE
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore azione pari a "+actionS);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		rightItem.addActionListener(rightListener);
		//LIstener per creare un nuovo Duty
		
	/*	ActionListener dutyListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,action,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire azione");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo duty",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					action=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+action+" "+ object);
				}
			}
		};*/
		ActionListener dutyListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,actionS,object,textLaw};
				InsertForm ins=new InsertForm(vec,"duty");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore azione pari a "+actionS);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		dutyItem.addActionListener(dutyListener);
		//Listener per inserire un nuovo prohibition
		/*ActionListener prohibitionListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,action,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire azione");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo prohibition",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					action=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+action+" "+ object);
				}
			}
		};*/
		ActionListener prohibitionListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,actionS,object,textLaw};
				InsertForm ins=new InsertForm(vec,"prohibition");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore azione pari a "+actionS);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		prohibitionItem.addActionListener(prohibitionListener);
		//Listener per inserire un nuovo permission
		/*ActionListener permissionListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,action,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire azione");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo permission",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					action=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+action+" "+ object);
				}
			}
		};*/
		
		ActionListener permissionListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,actionS,object,textLaw};
				InsertForm ins=new InsertForm(vec,"permission");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore azione pari a "+actionS);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		permissionItem.addActionListener(permissionListener);
		
		//Listener per inserire nuovo redress
		/*ActionListener redressListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,effect,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire effetto");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo redress",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					effect=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+effect+" "+ object);
				}
			}
		};*/
		
		ActionListener redressListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,effect,object,textLaw};
				InsertForm ins=new InsertForm(vec,"redress");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore effect pari a "+effect);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		redressItem.addActionListener(redressListener);
		
		//Listener per inserire nuova violation
		/*ActionListener violationListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,penalty,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire pena");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo violation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					penalty=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+penalty+" "+ object);
				}
			}
		};*/
		
		ActionListener violationListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,penalty,object,textLaw};
				InsertForm ins=new InsertForm(vec,"violation");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore pena pari a "+penalty);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		violationItem.addActionListener(violationListener);
		
		//Listener per inserire nuovo term
		/*ActionListener termListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String definiendum, definiens;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(4,1));
				JLabel label=new JLabel("Inserire definiendum");
				JLabel label1=new JLabel("Inserire definiens");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo duty",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					definiendum=field.getText();
					definiens=field1.getText();
					System.out.println(definiendum+ " "+ " "+definiens);
				}
			}
		};*/
		ActionListener termListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{definiendum,definiens,textLaw};
				InsertForm ins=new InsertForm(vec,"term");
				System.out.println("Valore definiendum pari a "+definiendum);
				System.out.println("Valore definiens pari a "+definiens);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		termItem.addActionListener(termListener);
		
		//Listener per inserire nuovo procedure
		/*ActionListener procedureListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String counter,action,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire azione");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo procedure",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					action=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+action+" "+ object);
					
				}
			}
		};*/
		ActionListener procedureListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,actionS,object,textLaw};
				InsertForm ins=new InsertForm(vec,"procedure");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore azione pari a "+actionS);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		procedureItem.addActionListener(procedureListener);
		
		//Listener per inserire nuovo establishment
		
		/*ActionListener establishmentListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest;
				JTextField field=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(2,1));
				JLabel label=new JLabel("Inserire destinatario");
				panel.add(label);
				panel.add(field);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo duty",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					System.out.println(dest);
				}
			}
		};*/
		
		ActionListener establishmentListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,textLaw};
				InsertForm ins=new InsertForm(vec,"establishment");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		establishmentItem.addActionListener(establishmentListener);
		
		//Listener per inserire nuovo organization
		/*ActionListener organizationListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest;
				JTextField field=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(2,1));
				JLabel label=new JLabel("Inserire destinatario");
				panel.add(label);
				panel.add(field);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo organization",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					System.out.println(dest);
				}
			}
		};*/
		
		ActionListener organizationListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,textLaw};
				InsertForm ins=new InsertForm(vec,"organization");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		organizationItem.addActionListener(organizationListener);
		
		
		//Listener per inserire nuovo power
		
		/*ActionListener powerListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,activity,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire attività");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo power",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					activity=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+activity+" "+ object);
				}
			}
		};*/
		
		ActionListener powerListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,activity,object,textLaw};
				InsertForm ins=new InsertForm(vec,"power");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore attività pari a "+activity);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		powerItem.addActionListener(powerListener);
		
		//Listener per aggiungere nuovo liability
		
		/*ActionListener liabilityListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,counter,activity,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire attività");
				JLabel label3=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo liability",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					counter=field1.getText();
					activity=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+activity+" "+ object);
				}
			}
		};*/
		
		ActionListener liabilityListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,counter,activity,object,textLaw};
				InsertForm ins=new InsertForm(vec,"liability");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore controparte pari a "+counter);
				System.out.println("Valore attività pari a "+activity);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		liabilityItem.addActionListener(liabilityListener);
		
		//Listener per inserire nuovo status
		
		/*ActionListener statusListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String dest,object;
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(4,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire oggetto");
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				int confirm=JOptionPane.showConfirmDialog(frame, panel, "Inserimento nuovo duty",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(confirm==JOptionPane.OK_OPTION){
					dest=field.getText();
					object=field1.getText();
					System.out.println(dest+ " "+ object);
				}
			}
		};*/
		
		ActionListener statusListener=new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//String dest=null;
				String [] result=new String[]{dest,object,textLaw};
				InsertForm ins=new InsertForm(vec,"status");
				System.out.println("Valore destinatario pari a "+dest);
				System.out.println("Valore oggetto pari a "+object);
				System.out.println("Valore legge pari a "+textLaw);
			}
		};
		
		statusItem.addActionListener(statusListener);
		
		item2.addActionListener(actionSave); 
		item3.addActionListener(actionSaveWithName);
		dutyItem.addActionListener(dutyListener);
		
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
		
				
		//aggiungo gli item al menù
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
		
		//PER CREARE PIU'ELEMENTI LEGATI "BENE" ASSIEME GUARDA SPRINGLAYOUT O JTABBEDPANE PER FARLI IN TAB DIVERSI
		//creo l'area di testo principale TODO riguarda ogni cosa
		Container contentPane=frame.getContentPane();//creo il pannello posto nell'area centrale
		panel=new JPanel();
		JScrollPane mainPaneLeft=new JScrollPane(panel);//creo lo scroller contenente la'rea di testo
		//panel.setPreferredSize(contentPane.getSize());
		/*text=new JTextArea(30,30); //TODO CAMBIA, FALLA DI DIMENSIONI "AUTOIMPOSTANTI"
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		JScrollPane scroll=new JScrollPane(text);*/
		System.out.println ("dimensione "+contentPane.getSize());
		contentPane.add(mainPaneLeft,BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		subPanel=new JPanel();
		//Border border=BorderFactory.createLineBorder(Color.black, 5);
		Border border=BorderFactory.createEmptyBorder();
		subPanel.setBorder(border);
		subPanel.add(scroll);
		panel.add(subPanel);
		System.out.println("Dimensione subpanel"+subPanel.getSize());
		document=text.getDocument();//Imposto la variabile di classe document ad essere il document della textarea*/
		
		
		
		//creo il gestore del document ,utile per vedere quando viene modificata l'area di testo principale
		
		class DocumentGesture implements DocumentListener{
			public void changedUpdate(DocumentEvent e){
				init=true; //indico che il documento ha subito una qualsiasi operazione
				modified=true; //indico che il lavoro ha subito modifiche dall'ultimo salvataggio
				System.out.println("Cambiamento");
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
	/*	JTree tree=new JTree();
		tree.setEditable(true);
		subPanel1=new JPanel();
		Border border1=BorderFactory.createLineBorder(Color.black, 2);
		subPanel.setBorder(border);
		subPanel1.add(tree);
		//panel.add(subPanel1);
		 */
		frame.setVisible(true);
		return frame;
		
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
	//TODO CREARE METODI PER CREARE NUOVE DISPOSIZIONI
	//METODI PER SETTARE GLI ARGOMENTI
	protected static void setDest(String result){
		dest=result;
	}
	protected static void setCounter(String result){
		counter=result;
	}
	protected static void setAction(String result){
		actionS=result;
	}
	protected static void setObject(String result){
		object=result;
	}
	protected static void setTextLaw(String result){
		textLaw=result;
	}
	protected static void setEffect(String result){
		effect=result;
	}
	protected static void setPenalty(String result){
		penalty=result;
	}
	protected static void setDefiniendum(String result){
		definiendum=result;
	}
	protected static void setDefiniens(String result){
		definiens=result;
	}
	protected static void setActivity(String result){
		activity=result;
	}
}
