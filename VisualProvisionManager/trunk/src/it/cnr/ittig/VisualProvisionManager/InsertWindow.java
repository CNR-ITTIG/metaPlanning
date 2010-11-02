package it.cnr.ittig.VisualProvisionManager;

import it.cnr.ittig.VisualProvisionManager.Provision.Provision;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class InsertWindow extends JDialog{
	String type;//INDICA IL TIPO DI DISPOSIZIONE CHE SI VUOL CREARE (DUTY,RIGHT....)
	boolean cond=true; //UTILE PER VEDERE SE TUTTI I CAMPI SONO RIEMPITI. NECESSARIO INIZIALIZZARLA A TRUE
	JTextArea area=new JTextArea(8,25);//area di testo per l'immissione del testo di legge
	JLabel label=new JLabel("Inserire testo");
	JPanel screen= new JPanel();//pannello che fa da sfondo ad ogni form di immissione. Andrà aggiunto al contentPane della JDialog che si creerà
	JPanel down=new JPanel();//pannello che andrà a contenere l'area del testo di legge e la relativa label
	JLabel [] labels; //ELENCA TUTTE LE LABEL ASSOCIATE AGLI ARGOMENTI DELLA DISPOSIZIONE
	JTextField [] fields; //ELENCA TUTTI CAMPI DI TESTO ASSOCIATI AGLI ARGOMENTI DELLA DISPOSIZIONE
	JLabel empty=new JLabel("");// 4 label vuote per allineare in modo corretto gli elementi della finestra
	JScrollPane scroller1; 
	
	public InsertWindow(final ProvisionFrame frame,final OntModel model,final OntClass ont){//FRAME E ONTMODEL DEFINITO FINAL PER POTER FUNZIONARE COL LISTENER INTERNO
		type=getProvisionType(ont);//RICAVO IL TIPO DI DISPOSIZIONE ED IMPOSTO IL TITOLO DELLA FINESTRA
		if(type.equals("RuleOnRule")){//UTILE PER STAMPARE MESSAGGIO INFORMATIVO SE SI VUOL CREARE UNA RULEONRULE
			JOptionPane.showMessageDialog(frame,"Non Implementato","Non Implementato",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		setTitle("Inserimento nuovo " +type);
		Point p=frame.getLocation();
		this.setLocation(p.x,p.y);
		setModal(true); 
		java.awt.Container cont=getContentPane();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		screen.setLayout(new BorderLayout());
		JPanel panel=new JPanel();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		label.setLabelFor(area);
		label.setDisplayedMnemonic(KeyEvent.VK_T);
		final JButton button=new JButton("OK");
		final JButton button1=new JButton("Cancella");
		int numOfProperties=0;
		final String[] properties=new String[5]; //RIGUARDA LA GRANDEZZA DELL'ARRAY
		
	//	OntModel modelSearch=frame.getModelReasoner();
		//RECUPERO LE PROPRIETA' SPECIFICHE DELLA CLASSE
		OntClass ont1=model.getOntClass(ont.toString());// CLASSE DELLA DISPOSIZIONE CHE VOGLIO INSERIRE
		ExtendedIterator iter=ont1.listDeclaredProperties(); // SAREBBE PIU' EFFICIENTE, MA DEVO TROVARE UN REASONER COL QUALE FUNZIONA
		while(iter.hasNext()){								//PROPRIETA' CONDIVISE NON SONO RITORNATE CORRETTAMENTE
			properties[numOfProperties]=getPropertyName(iter.next().toString());
			numOfProperties++;
		}
		
		//RECUPERO LE PROPRIETA' CONDIVISE APPARTENENTI ALLA CLASSE
		ExtendedIterator <UnionClass> iterUnion=model.listUnionClasses();
		while(iterUnion.hasNext()){
			UnionClass ont2=(UnionClass)iterUnion.next(); //CLASSE UNIONE 
			if(ont2.hasOperand(ont1)){
				iter=ont2.listDeclaredProperties();//ITER CONTIENE ORA LE PROPRIETà DELLE UNION CLASS CUI LA DISPOSIZIONE APPARTIENE
				properties[numOfProperties]=getPropertyName(iter.next().toString());
				numOfProperties++;
			}
		}
		
		
		//VERSIONE ALTERNATIVA, PROBLEMA CHE L'ORDINE IN CUI SONO PRESENTATI GLI ARGOMENTI NON SEMBRA OTTIMO 
		/*ExtendedIterator iter2=model.listOntProperties();//INTERROGO IL MODELLO ELENCANDO TUTTE LE PROPRIETA' PRESENTI
		OntProperty prop=null;
		while(iter2.hasNext()){
			prop=(OntProperty)iter2.next();
			//System.out.println("Dominio di "+ ont.toString()+" è "+prop.getDomain());
			if(prop.hasDomain(ont)){//SE LA PROPRIETA' APPARTIENE ALLA DISPOSIZIONE LA AGGIUNGO
				properties[numOfProperties]=getPropertyName(prop.toString());
				numOfProperties++;
			}
		}*/
		panel.setLayout(new GridLayout(numOfProperties*2,1)); //IMPOSTO LE DIMENSIONI DEL PANNELLO INSERIMENTO ARGOMENTI. PER OGNI ARGOMENTO UNA LABEL E UN JTextField
		down.setLayout(new GridLayout(2,1));
		labels=new JLabel[numOfProperties]; //CREO LE LABEL E I JTextField PER OGNI ARGOMENTO E LE AGGIUNGO AL PANEL
		fields=new JTextField[numOfProperties];
		for(int i=1;i<=numOfProperties;i++){
			labels[i-1]=new JLabel("Inserire "+properties[i-1]);
			fields[i-1]=new JTextField();
			panel.add(labels[i-1]);
			panel.add(fields[i-1]);
		}
		JPanel pan=new JPanel(new FlowLayout());
		final int num=numOfProperties; //NECESSARIO PERCHE' IL LISTENER DEVE OPERARE SU UNA VARIABILE DICHIARATA FINAL
		
		ActionListener actionListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cond=true;	//REIMPOSTO A TRUE PERCHE ALTRIMENTI SE LASCIO UN CAMPO VUOTO DURANTE IL PRIMO INSERIMENTO L'OPERAZIONE ENTRA IL LOOP
				if(e.getSource()==button){
					for(int i=1;i<=num;i++){
						cond=cond&FrameUtil.stringWithCarachter(fields[i-1].getText());//CONTROLLO CHE NESSUN ARGOMENTO  SIA VUOTO
					}
					cond=cond&FrameUtil.stringWithCarachter(area.getText());
					while(!cond)
					{
						setTitle("Inserimento nuovo "+type);
						JOptionPane.showMessageDialog(frame,"Impossibile lasciare campi vuoti", "Errore nell'inserimento",JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				if (cond&e.getSource()==button) //SE HO CORETTAMENTE INSERITO GLI ARGOMENTI E PREMO OK
				{
					String []arguments=new String[num+1]; //PASSO I VALORI DEGLI ARGOMENTI IN QUESTO ARRAY
					for(int i=1;i<=num;i++){
						arguments[i-1]=fields[i-1].getText();
					}
					arguments[arguments.length-1]=area.getText();//AGGIUNGO IL VALORE DEL TESTO DELLA DISPOSIZIONE ALL'ARRAY
					Provision prov=frame.createProvision(ont,properties,arguments);
				}
				dispose();
			}
		};
		button.addActionListener(actionListener);
		button1.addActionListener(actionListener);
		JScrollPane scroller=new JScrollPane(area); //scroller per contenere l'area di testo per l'immissione del testo di legge		
		down.add(scroller);
		screen.add(panel,BorderLayout.NORTH);
		screen.add(label,BorderLayout.CENTER);
		screen.add(down,BorderLayout.SOUTH);
		cont.setLayout(new BorderLayout());
		pan.add(button);
		pan.add(button1);
		scroller1=new JScrollPane(screen); //Scroller per contenere e visualizzare correttamente l'area di immissione dati
		cont.add(scroller1,BorderLayout.CENTER);
		cont.add(pan,BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}
	
	
	/*public Term createTerm(OntModel model,String def1,String def2,String law){
		return new Term(model,def1,def2,law);
	}
	
	public Organization createOrganization(OntModel model,String def1,String law){
		return new Organization(model,def1,law);
	}*/
	
	private String getProvisionType(OntClass ont){
		String provisionType=ont.toString();
		for(int i=provisionType.length()-1;i!=-1;i--)
			if(provisionType.charAt(i)=='#'){
				provisionType=provisionType.substring(i+1,provisionType.length());
				break;
			}
		return provisionType;
	}
	
	private String getPropertyName(String prop){
		for(int i=prop.length()-1;i!=-1;i--)
			if(prop.charAt(i)=='#'){
				prop=prop.substring(i+1,prop.length());
				break;
			}
		return prop;
	}
}
