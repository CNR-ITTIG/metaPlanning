package it.cnr.ittig.VisualProvisionManager;

import it.cnr.ittig.VisualProvisionManager.Provision.Provision;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;


public class TreeWindow extends JDialog{
	JLabel [] labels; //ELENCA TUTTE LE LABEL ASSOCIATE AGLI ARGOMENTI DELLA DISPOSIZIONE
	JTextField [] fields; //ELENCA TUTTI CAMPI DI TESTO ASSOCIATI AGLI ARGOMENTI DELLA DISPOSIZIONE
	JTextArea area;
	JPanel screen=new JPanel(); //pannello di sfondo della finestra
	JPanel panel=new JPanel();//PANNELLO CONTENENTE LABEL ARGOMENTI E RELATIVI CAMPI DI TESTO
	JPanel down=new JPanel();//pannello che andrà a contenere l'area del testo di legge e la relativa label
	final JButton button=new JButton("Modifica");
	final JButton button1=new JButton("Cancella disposizione");
	final JButton button2=new JButton("Annulla");
	
	public TreeWindow(final DefaultMutableTreeNode node, final ProvisionFrame frame){
		setTitle(node.toString());
		setModal(true);
		setLocation(MouseInfo.getPointerInfo().getLocation().x,+MouseInfo.getPointerInfo().getLocation().y);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		int properties=node.getChildCount();//VI E' GIA' ANCHE IL CAMPO DEL TESTO
		panel.setLayout(new GridLayout((properties)*2,1)); //IMPOSTO LE DIMENSIONI DEL PANNELLO INSERIMENTO ARGOMENTI. PER OGNI ARGOMENTO UNA LABEL E UN JTextField
		down.setLayout(new GridLayout(2,1));
		labels=new JLabel[properties];
		fields=new JTextField[properties-1];
		for(int i=0;i<=properties-1;i++){
			labels[i]=new JLabel(argumentsName(node.getChildAt(i).toString()));			
			if(i<properties-1){
				fields[i]=new JTextField();
				fields[i].setText((argumentsValue(node.getChildAt(i).toString())));	
				panel.add(labels[i]);
				panel.add(fields[i]);
			}else{
				area=new JTextArea();
				area.setText((argumentsValue(node.getChildAt(i).toString())));
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				JScrollPane scroller=new JScrollPane(area);
				panel.add(labels[i]);
				panel.add(scroller);
			}
		}
		JScrollPane scroller1=new JScrollPane(screen);//SCROLLER CHE CONTIENE L'AREA DEI CAMPI DI TESTO		
		JPanel pan=new JPanel(new FlowLayout());
		pan.add(button);
		pan.add(button1);
		pan.add(button2);
		final int num=properties-1; //UTILE PERCHE' IL LISTENER DEVE OPERARE SU VARIABILI FINAL
		ActionListener actionListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean cond=true;	//REIMPOSTO A TRUE PERCHE ALTRIMENTI SE LASCIO UN CAMPO VUOTO DURANTE IL PRIMO INSERIMENTO L'OPERAZIONE ENTRA IL LOOP
				if(e.getSource()==button){
					for(int i=1;i<=num;i++){
						cond=cond&FrameUtil.stringWithCarachter(fields[i-1].getText());//CONTROLLO CHE NESSUN ARGOMENTO  SIA VUOTO
					}
					cond=cond&FrameUtil.stringWithCarachter(area.getText());
					while(!cond)
					{
						//setTitle("Inserimento nuovo "+type);
						JOptionPane.showMessageDialog(null,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				if (cond&e.getSource()==button) //SE HO CORRETTAMENTE INSERITO GLI ARGOMENTI E PREMO OK
				{
					
					System.out.println("Modificare");
					String []arguments=new String[num+1]; //PASSO I VALORI DEGLI ARGOMENTI IN QUESTO ARRAY
					for(int i=1;i<=num;i++){
						arguments[i-1]=fields[i-1].getText();
					}
					arguments[arguments.length-1]=area.getText();//AGGIUNGO IL VALORE DEL TESTO DELLA DISPOSIZIONE ALL'ARRAY
					//node.removeAllChildren();
					String content;
					//DefaultMutableTreeNode child;
					String propertyName[]=new String[num+1];//ARRAY COL NOME DELLE PROPRIETà, UTILE PER LA LORO MODIFICA
					for(int i=0;i<num;i++){
						propertyName[i]=labels[i].getText();
						content=labels[i].getText()+": "+fields[i].getText();
						System.out.println(content);
						//child=new DefaultMutableTreeNode(content);
						//node.add(child);
					}
					propertyName[propertyName.length-1]=labels[labels.length-1].getText();
					content=labels[labels.length-1].getText()+": "+area.getText();
					System.out.println(content);
					//child=new DefaultMutableTreeNode(content);
				//	node.add(child);
				//	frame.reloadTreeAfter();
					frame.modifyProvision(node.toString(),propertyName,arguments,node);
					//Provision prov=frame.createProvision(ont,properties,arguments);
				}
				if(e.getSource()==button1){
					frame.deleteProvision(node.toString(),node);
					/*DefaultMutableTreeNode father=(DefaultMutableTreeNode)node.getParent();
					if(father!=null){//	DOVREBBE ESSERE SUPERFLUO QUESTO CONTROLLO
						father.remove(node);
						if(father.getChildCount()==0){//SE IL PADRE NON HA PIU' FIGLI, RIAGGIUNGO IL NODO BLANK
							father.add(new DefaultMutableTreeNode("Blank"));
						}
					}
					else{//NON DOVREBBE MAI ESSERE ESEGUITO (CANCELLO LA RADICE DELL'ALBERO, MA LA RADICE NON PUO' ESSERE UN'ISTANZA DI DISPOSIZIONE)
						JOptionPane.showMessageDialog(frame,"Errore nell'applicazione", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}*/
					//frame.reloadTreeAfter();
				}
				dispose();
			}
		};
		button.addActionListener(actionListener);
		button1.addActionListener(actionListener);
		button2.addActionListener(actionListener);
		screen.add(panel,BorderLayout.NORTH);
		Container container=getContentPane();
		container.setLayout(new BorderLayout());
		container.add(scroller1,BorderLayout.CENTER);
		container.add(pan,BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	private String argumentsName(String arg){
		char charAt;
		String name=arg;
		for(int i=0;i<=arg.length()-1;i++){
			charAt=arg.charAt(i);
			if(charAt==':'){
				name=arg.substring(0,i);
				break;
			}
		}
		return name;
	}
	
	private String argumentsValue(String arg){
		char charAt;
		String value=arg;
		for(int i=0;i<=arg.length()-1;i++){
			charAt=arg.charAt(i);
			if(charAt==':'){
				value=arg.substring(i+2,arg.length());//AGGIUNGO 2 COSì SALTO IL : E LO SPAZIO BIANCO SUCCESSIVO
				break;
			}
		}
		return value;
	}
}
