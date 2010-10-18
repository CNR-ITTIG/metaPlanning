package it.cnr.ittig.VisualProvisionManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.hp.hpl.jena.rdf.model.Container;

public class InsertForm extends JDialog{
	String dest,counter,action,object,textLaw;
	boolean cond=false;
	JTextField field=new JTextField();//4 campi di testo per l'immissione degli argomenti (disposizioni hanno al max 4 argomenti)
	JTextField field1=new JTextField();
	JTextField field2=new JTextField();
	JTextField field3=new JTextField();//area di testo per l'immissione del testo di legge
	JTextArea area=new JTextArea(8,25);
	JLabel label4=new JLabel("Inserire testo");
	JPanel screen= new JPanel();//pannello che fa da sfondo ad ogni form di immissione. Andrà aggiunto al contentPane della JDialog che si creerà
	JPanel down=new JPanel();//pannello che andrà a contenere l'area del testo di legge e la relativa label
	JLabel label=new JLabel("Inserire destinatario");//4 label di base perchè al max vi sono disposizioni con 4 argomenti
	JLabel label1=new JLabel("Inserire controparte");
	JLabel label2=new JLabel("Inserire azione");
	JLabel label3=new JLabel("Inserire oggetto");
	JLabel empty=new JLabel("");// 4 label vuote per allineare in modo corretto gli elementi della finestra
	JLabel empty1=new JLabel("");
	JLabel empty2=new JLabel("");
	JLabel empty3=new JLabel("");
	JScrollPane scroller1; //scroller utile per visualizzare correttamente l'area di immissione dati. Inizializzato nel costruttore
	String vec[]=new String[]{"ronni","lore","ricc"};//per ora sono prove
	JComboBox box=new JComboBox(vec);
	JComboBox box1=new JComboBox(vec);
	JComboBox box2=new JComboBox(vec);
	JComboBox box3=new JComboBox(vec);
	public InsertForm(final applicationFrame applicationFrame,String r[],final String type) //r serve se implemento database di destinatari, type specifica il tipo di disposizione
	{
		//IMPOSTO LA FINESTRA A MODALE E CREO GLI ELEMENTI COMUNI A TUTTI I FORM
		//setLocation(500,50);//da migliorare l'apparizione su schermo
		setModal(true);
		java.awt.Container cont=getContentPane();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		screen.setLayout(new BorderLayout());
		JPanel panel=new JPanel();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		label4.setLabelFor(area);
		label4.setDisplayedMnemonic(KeyEvent.VK_T);
		final JButton button=new JButton("OK");
		final JButton button1=new JButton("Cancella");
		setTitle("Inserimento nuovo " +type);
		//GESTISCO I FORM CON 4 ARGOMENTI PARI A DESTINATARIO,CONTROPARTE,AZIONE,OGGETTO
		if(type=="procedure"||type=="duty"||type=="right"||type=="prohibition"||type=="permission"){
			panel.setLayout(new GridLayout(8,2));
			down.setLayout(new GridLayout(2,1));
			JPanel pan=new JPanel(new FlowLayout());
		
			ActionListener action=new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource()==button){
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field1.getText())
						&FrameUtil.stringWithCarachter(field2.getText())&FrameUtil.stringWithCarachter(field3.getText())
						&FrameUtil.stringWithCarachter(area.getText());
						while(!cond)
						{
							setTitle("Inserimento nuovo "+type+" : Impossibile lasciare campi vuoti");
							JOptionPane.showMessageDialog(applicationFrame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDest(field.getText());
						applicationFrame.setCounter(field1.getText());
						applicationFrame.setAction(field2.getText());
						applicationFrame.setObject(field3.getText());
						applicationFrame.setTextLaw(area.getText());
					}
					dispose();
				}
			};
		
			button.addActionListener(action);
			button1.addActionListener(action);
			JScrollPane scroller=new JScrollPane(area); //scroller per contenere l'area di testo per l'immissione del testo di legge
			panel.add(label);
			label.setLabelFor(field);
			//field.setFont(new Font("Courier",25,25));
			label.setDisplayedMnemonic(KeyEvent.VK_D);
			//panel.add(empty);
			panel.add(field);
			//field.setMinimumSize(new Dimension(1,5));
			//panel.add(box);
			panel.add(label1);
			label1.setLabelFor(field1);
			label1.setDisplayedMnemonic(KeyEvent.VK_C);
			//panel.add(empty1);
			panel.add(field1);
			//panel.add(box1);
			panel.add(label2);
			label2.setLabelFor(field2);
			label2.setDisplayedMnemonic(KeyEvent.VK_A);
			//panel.add(empty2);
			panel.add(field2);
			//panel.add(box2);
			panel.add(label3);
			label3.setLabelFor(field3);
			label3.setDisplayedMnemonic(KeyEvent.VK_O);
			//panel.add(empty3);
			panel.add(field3);
			//panel.add(box3);
			//down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			//screen.add(empty,BorderLayout.EAST);
			//screen.add(empty,BorderLayout.WEST);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			//cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
			cont.add(scroller1,BorderLayout.CENTER);
			cont.add(pan,BorderLayout.SOUTH);
		}//GESTISCO I FORM CON 4 ARGOMENTI PARI A DESTINATARIO,CONTROPARTE,EFFETTO,OGGETTO
		else if(type=="redress"){
			label2=new JLabel("Inserire effetto");
			panel.setLayout(new GridLayout(8,2));
			down.setLayout(new GridLayout(2,1));
			JPanel pan=new JPanel(new FlowLayout());
		
			ActionListener action=new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource()==button){
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field1.getText())
						&FrameUtil.stringWithCarachter(field2.getText())&FrameUtil.stringWithCarachter(field3.getText())
						&FrameUtil.stringWithCarachter(area.getText());
						while(!cond)
						{
							setTitle("Inserimento nuovo "+type+" : Impossibile lasciare campi vuoti");
							JOptionPane.showMessageDialog(applicationFrame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDest(field.getText());//CREARE QUI LA NUOVA DISPOSIZIONE SENNO LA CREA ANCHE QUANDO PIGIO ANNULLA
						applicationFrame.setCounter(field1.getText());
						applicationFrame.setEffect(field2.getText());
						applicationFrame.setObject(field3.getText());
						applicationFrame.setTextLaw(area.getText());
						}
					dispose();
				}
			};
		
			button.addActionListener(action);
			button1.addActionListener(action);
			JScrollPane scroller=new JScrollPane(area);
			panel.add(label);
			label.setLabelFor(field);
			label.setDisplayedMnemonic(KeyEvent.VK_D);
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			panel.add(label1);
			label1.setLabelFor(field1);
			label1.setDisplayedMnemonic(KeyEvent.VK_C);
			panel.add(empty1);
			panel.add(field1);
			panel.add(box1);
			panel.add(label2);
			label2.setLabelFor(field2);
			label2.setDisplayedMnemonic(KeyEvent.VK_E);
			panel.add(empty2);
			panel.add(field2);
			panel.add(box2);
			panel.add(label3);
			label3.setLabelFor(field3);
			label3.setDisplayedMnemonic(KeyEvent.VK_O);
			panel.add(empty3);
			panel.add(field3);
			panel.add(box3);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			//cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
			cont.add(scroller1,BorderLayout.CENTER);
			cont.add(pan,BorderLayout.SOUTH);
		}//GESTISCO I FORM CON 4 ARGOMENTI PARI A DESTINATARIO,CONTROPARTE,PENA,OGGETTO
		else if(type=="violation"){
			label2=new JLabel("Inserire pena");
			panel.setLayout(new GridLayout(8,1));
			down.setLayout(new GridLayout(2,1));
			JPanel pan=new JPanel(new FlowLayout());
		
			ActionListener action=new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource()==button){
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field1.getText())
						&FrameUtil.stringWithCarachter(field2.getText())&FrameUtil.stringWithCarachter(field3.getText())
						&FrameUtil.stringWithCarachter(area.getText());
						while(!cond)
						{
							setTitle("Inserimento nuovo "+type+" : Impossibile lasciare campi vuoti");
							JOptionPane.showMessageDialog(applicationFrame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDest(field.getText());
						applicationFrame.setCounter(field1.getText());
						applicationFrame.setPenalty(field2.getText());
						applicationFrame.setObject(field3.getText());
						applicationFrame.setTextLaw(area.getText());
						}
					dispose();
				}
			};
		
			button.addActionListener(action);
			button1.addActionListener(action);
			JScrollPane scroller=new JScrollPane(area);
			panel.add(label);
			label.setLabelFor(field);
			label.setDisplayedMnemonic(KeyEvent.VK_D);
			//panel.add(empty);
			panel.add(field);
			//panel.add(box);
			panel.add(label1);
			label1.setLabelFor(field1);
			label1.setDisplayedMnemonic(KeyEvent.VK_C);
			//panel.add(empty1);
			panel.add(field1);
			//panel.add(box1);
			panel.add(label2);
			label2.setLabelFor(field2);
			label2.setDisplayedMnemonic(KeyEvent.VK_P);
			//panel.add(empty2);
			panel.add(field2);
			//panel.add(box2);
			panel.add(label3);
			label3.setLabelFor(field3);
			label3.setDisplayedMnemonic(KeyEvent.VK_O);
			//panel.add(empty3);
			panel.add(field3);
			//panel.add(box3);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			//cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
			cont.add(scroller1,BorderLayout.CENTER);
			cont.add(pan,BorderLayout.SOUTH);
		}//GESTISCO I FORM CON 2 ARGOMENTI PARI A DEFINIENDUM E DEFINIENS
		else if(type=="term"){
			label.setText("Inserire definiendum");
			label1=new JLabel("Inserire definiens");
			panel.setLayout(new GridLayout(4,2));
			down.setLayout(new GridLayout(2,1));
			JPanel pan=new JPanel(new FlowLayout());
		
			ActionListener action=new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource()==button){
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field1.getText())
						&FrameUtil.stringWithCarachter(area.getText());
						while(!cond)
						{
							setTitle("Inserimento nuovo "+type+" : Impossibile lasciare campi vuoti");
							JOptionPane.showMessageDialog(applicationFrame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDefiniendum(field.getText());
						applicationFrame.setDefiniens(field1.getText());
						applicationFrame.setTextLaw(area.getText());
						}
					dispose();
				}
			};
		
			button.addActionListener(action);
			button1.addActionListener(action);
			JScrollPane scroller=new JScrollPane(area);
			panel.add(label);
			label.setLabelFor(field);
			label.setDisplayedMnemonic(KeyEvent.VK_D);
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			panel.add(label1);
			label1.setLabelFor(field);
			label1.setDisplayedMnemonic(KeyEvent.VK_E);
			panel.add(empty1);
			panel.add(field1);
			panel.add(box1);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			//cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
			cont.add(scroller1,BorderLayout.CENTER);
			cont.add(pan,BorderLayout.SOUTH);
		}//GESTISCO I FORM CON 1 ARGOMENTO PARI A DESTINATARIO
		else if(type=="establishment"||type=="organization"){
			panel.setLayout(new GridLayout(2,2));
			down.setLayout(new GridLayout(2,1));
			JPanel pan=new JPanel(new FlowLayout());
		
			ActionListener action=new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource()==button){
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(area.getText());
						while(!cond)
						{
							setTitle("Inserimento nuovo "+type+" : Impossibile lasciare campi vuoti");
							JOptionPane.showMessageDialog(applicationFrame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDest(field.getText());
						applicationFrame.setTextLaw(area.getText());
						}
					dispose();
				}
			};
		
			button.addActionListener(action);
			button1.addActionListener(action);
			JScrollPane scroller=new JScrollPane(area);
			panel.add(label);
			label.setLabelFor(field);
			label.setDisplayedMnemonic(KeyEvent.VK_D);
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			//cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
			cont.add(scroller1,BorderLayout.CENTER);
			cont.add(pan,BorderLayout.SOUTH);
		}//GESTISCO I FORM CON 4 ARGOMENTI PARI A DESTINATARIO,CONTROPARTE,ATTIVITA',OGGETTO
		else if(type=="power"||type=="liability"){
			label3.setText("Inserire attività");
			panel.setLayout(new GridLayout(8,2));
			down.setLayout(new GridLayout(2,1));
			JPanel pan=new JPanel(new FlowLayout());
		
			ActionListener action=new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource()==button){
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field1.getText())&
						FrameUtil.stringWithCarachter(field2.getText())&FrameUtil.stringWithCarachter(field3.getText())
						&FrameUtil.stringWithCarachter(area.getText());
						while(!cond)
						{
							setTitle("Inserimento nuovo "+type+" : Impossibile lasciare campi vuoti");
							JOptionPane.showMessageDialog(applicationFrame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDest(field.getText());
						applicationFrame.setCounter(field1.getText());
						applicationFrame.setActivity(field2.getText());
						applicationFrame.setObject(field3.getText());
						applicationFrame.setTextLaw(area.getText());
						}
					dispose();
				}
			};
		
			button.addActionListener(action);
			button1.addActionListener(action);
			JScrollPane scroller=new JScrollPane(area);
			panel.add(label);
			label.setLabelFor(field);
			label.setDisplayedMnemonic(KeyEvent.VK_D);
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			panel.add(label1);
			label1.setLabelFor(field1);
			label1.setDisplayedMnemonic(KeyEvent.VK_C);
			panel.add(empty1);
			panel.add(field1);
			panel.add(box1);
			panel.add(label2);
			label2.setLabelFor(field2);
			label2.setDisplayedMnemonic(KeyEvent.VK_A);
			panel.add(empty2);
			panel.add(field2);
			panel.add(box2);
			panel.add(label3);
			label3.setLabelFor(field3);
			label3.setDisplayedMnemonic(KeyEvent.VK_O);
			panel.add(empty3);
			panel.add(field3);
			panel.add(box3);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			//cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
			cont.add(scroller1,BorderLayout.CENTER);
			cont.add(pan,BorderLayout.SOUTH);
		}//GESTISCO I FORM CON 2 ARGOMENTI PARI A DESTINATARIO,OGGETTO
		else if(type=="status"){
			panel.setLayout(new GridLayout(4,2));
			down.setLayout(new GridLayout(2,1));
			JPanel pan=new JPanel(new FlowLayout());
		
			ActionListener action=new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource()==button){
						cond=FrameUtil.stringWithCarachter(field.getText())&FrameUtil.stringWithCarachter(field3.getText())&FrameUtil.stringWithCarachter(area.getText());
						while(!cond)
						{
							setTitle("Inserimento nuovo "+type+" : Impossibile lasciare campi vuoti");
							JOptionPane.showMessageDialog(applicationFrame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDest(field.getText());
						applicationFrame.setObject(field.getText());
						applicationFrame.setTextLaw(area.getText());
						}
					dispose();
				}
			};
		
			button.addActionListener(action);
			button1.addActionListener(action);
			JScrollPane scroller=new JScrollPane(area);
			panel.add(label);
			label.setLabelFor(field);
			label.setDisplayedMnemonic(KeyEvent.VK_D);
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			panel.add(label3);
			label3.setLabelFor(field3);
			label3.setDisplayedMnemonic(KeyEvent.VK_O);
			panel.add(empty3);
			panel.add(field3);
			panel.add(box3);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			//cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
			cont.add(scroller1,BorderLayout.CENTER);
			cont.add(pan,BorderLayout.SOUTH);
		}
		pack();
		this.setVisible(true);
	
	}
}
