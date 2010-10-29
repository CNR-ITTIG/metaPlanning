package it.cnr.ittig.VisualProvisionManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class prova {

	/*public static void main(String [] args){
		Runnable runner= new Runnable(){
			public void run(){
				String dest,counter,action,object;
				JFrame frame=new JFrame();
				boolean cond=false;
				JTextArea area=new JTextArea(15,15);
				JTextField field=new JTextField();
				JTextField field1=new JTextField();
				JTextField field2=new JTextField();
				JTextField field3=new JTextField();
				JPanel screen= new JPanel();
				JPanel down=new JPanel();
				//screen.setLayout(new BorderLayout());
				screen.setLayout(new GridLayout(2,1,1,1));
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(8,1));
				down.setLayout(new GridLayout(2,1));
				JLabel label=new JLabel("Inserire destinatario");
				JLabel label1=new JLabel("Inserire controparte");
				JLabel label2=new JLabel("Inserire azione");
				JLabel label3=new JLabel("Inserire oggetto");
				JLabel label4=new JLabel("Inserire testo");
				JLabel empty=new JLabel("");
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				JScrollPane scroller=new JScrollPane(area);
				panel.add(label);
				panel.add(field);
				panel.add(label1);
				panel.add(field1);
				panel.add(label2);
				panel.add(field2);
				panel.add(label3);
				panel.add(field3);
			/*	down.add(empty);
				down.add(empty);
				down.add(empty);
				down.add(empty);
				down.add(empty);
				down.add(empty);*/
				/*down.add(label4);
				down.add(scroller);
				Dimension size=panel.getSize();
				size.setSize(size.getHeight()/2,size.getHeight()/2);
				//down.setSize(size);
				//screen.add(panel,BorderLayout.NORTH);
				screen.add(panel);
				screen.add(down);
				screen.setSize(1, 1);
				frame.add(panel,BorderLayout.NORTH);
				frame.add(screen,BorderLayout.CENTER);
				frame.setSize(500,500);
				frame.setVisible(true);
				//screen.add(new JSeparator(),BorderLayout.CENTER);
				//screen.add(down,BorderLayout.SOUTH);
				
					dest=field.getText();
					counter=field1.getText();
					action=field2.getText();
					object=field3.getText();
					System.out.println(dest+ " "+ " "+counter+ " "+action+" "+ object);
					


		};
			};

		EventQueue.invokeLater(runner);
		};*/
		public static void main(String args[]) {
			Runnable runner = new Runnable() {
			public void run() {
			JFrame frame = new JFrame("SpringLayout");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container contentPane = frame.getContentPane();
			SpringLayout layout = new SpringLayout();
			contentPane.setLayout(layout);
			Component left = new JLabel("Left");
			Component right = new JTextField(15);
			contentPane.add(left);
			contentPane.add(right);
			layout.putConstraint(SpringLayout.WEST, left, 10, SpringLayout.WEST,
					contentPane);
					layout.putConstraint(SpringLayout.NORTH, left, 25, SpringLayout.NORTH,
					contentPane);
					layout.putConstraint(SpringLayout.NORTH, right, 225, SpringLayout.NORTH,
					contentPane);
					layout.putConstraint(SpringLayout.WEST, right, 20, SpringLayout.EAST, left);
					frame.setSize(300, 100);
					frame.setVisible(true);
					}
					};
					EventQueue.invokeLater(runner);
					}

}
