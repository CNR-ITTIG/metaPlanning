package it.cnr.ittig.VisualProvisionManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.hp.hpl.jena.rdf.model.Container;

public class InsertForm extends JDialog{
	String dest,counter,action,object,textLaw;
	boolean cond=false;
	JTextField field=new JTextField();
	JTextField field1=new JTextField();
	JTextField field2=new JTextField();
	JTextField field3=new JTextField();
	JTextArea area=new JTextArea(5,10);
	JLabel label4=new JLabel("Inserire testo");
	JPanel screen= new JPanel();
	JPanel down=new JPanel();
	JLabel label=new JLabel("Inserire destinatario");
	JLabel label1=new JLabel("Inserire controparte");
	JLabel label2=new JLabel("Inserire azione");
	JLabel label3=new JLabel("Inserire oggetto");
	JLabel empty=new JLabel("");
	JLabel empty1=new JLabel("");
	JLabel empty2=new JLabel("");
	JLabel empty3=new JLabel("");
	String vec[]=new String[]{"ronni","lore","ricc"};
	JComboBox box=new JComboBox(vec);
	JComboBox box1=new JComboBox(vec);
	JComboBox box2=new JComboBox(vec);
	JComboBox box3=new JComboBox(vec);
	public InsertForm(String r[],final String type,final String param[])
	{
		//IMPOSTO LA FINESTRA A MODALE E CREO GLI ELEMENTI COMUNI A TUTTI I FORM
		setModal(true);
		java.awt.Container cont=getContentPane();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		screen.setLayout(new BorderLayout());
		JPanel panel=new JPanel();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
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
							JOptionPane.showMessageDialog(null, "Errore nell'inserimento","Impossibile lasciare campi vuoti",JOptionPane.ERROR_MESSAGE);
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
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			cont.add(pan,BorderLayout.SOUTH);
		}
		else if(type=="redress"){
			label2=new JLabel("effettto");
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
							JOptionPane.showMessageDialog(null, "Errore nell'inserimento","Impossibile lasciare campi vuoti",JOptionPane.ERROR_MESSAGE);
							return;
						}
						applicationFrame.setDest(field.getText());
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
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			cont.add(pan,BorderLayout.SOUTH);
		}
		else if(type=="violation"){
			label2=new JLabel("pena");
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
							JOptionPane.showMessageDialog(null, "Errore nell'inserimento","Impossibile lasciare campi vuoti",JOptionPane.ERROR_MESSAGE);
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
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			cont.add(pan,BorderLayout.SOUTH);
		}
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
							JOptionPane.showMessageDialog(null, "Errore nell'inserimento","Impossibile lasciare campi vuoti",JOptionPane.ERROR_MESSAGE);
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
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			panel.add(label1);
			panel.add(empty1);
			panel.add(field1);
			panel.add(box1);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			cont.add(pan,BorderLayout.SOUTH);
		}
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
							JOptionPane.showMessageDialog(null, "Errore nell'inserimento","Impossibile lasciare campi vuoti",JOptionPane.ERROR_MESSAGE);
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
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			cont.add(pan,BorderLayout.SOUTH);
		}
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
							JOptionPane.showMessageDialog(null, "Errore nell'inserimento","Impossibile lasciare campi vuoti",JOptionPane.ERROR_MESSAGE);
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
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			cont.add(pan,BorderLayout.SOUTH);
		}
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
							JOptionPane.showMessageDialog(null, "Errore nell'inserimento","Impossibile lasciare campi vuoti",JOptionPane.ERROR_MESSAGE);
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
			panel.add(empty);
			panel.add(field);
			panel.add(box);
			panel.add(label3);
			panel.add(empty3);
			panel.add(field3);
			panel.add(box3);
			down.add(label4);
			down.add(scroller);
			screen.add(panel,BorderLayout.NORTH);
			screen.add(label4,BorderLayout.CENTER);
			screen.add(down,BorderLayout.SOUTH);
			cont.setLayout(new BorderLayout());
			cont.add(screen,BorderLayout.CENTER);
			pan.add(button);
			pan.add(button1);
			cont.add(pan,BorderLayout.SOUTH);
		}
		pack();
		this.setVisible(true);
	
	}
}
