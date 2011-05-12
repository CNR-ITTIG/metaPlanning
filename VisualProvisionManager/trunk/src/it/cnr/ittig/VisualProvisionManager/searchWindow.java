package it.cnr.ittig.VisualProvisionManager;

import it.cnr.ittig.VisualProvisionManager.Provision.Provision;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class searchWindow extends JDialog{
	private JDialog dialog;		
	private JLabel [] labels; //ELENCA TUTTE LE LABEL (OVVERO NOME DEGLI ARGOMENTI) ASSOCIATE AGLI ARGOMENTI DI UNA DISPOSIZIONE
	private JTextField [] fields;//ELENCA TUTTI I VALORI DEGLI ARGOMENTI DI UNA DISPOSIZIONE
	private Vector <String> types; //CONTIENE,PER OGNI DISPOSIZIONE DA RICERCARE, IL SUO TIPO
	private Vector <JLabel []> allLabels;//CONTIENE,PER OGNI DISPOSIZIONE DA RICERCARE, L'ARRAY CON IL NOME DEGLI ARGOMENTI
	private Vector <JTextField []> allFields;//CONTIENE,PER OGNI DISPOSIZIONE DA RICERCARE, L'ARRAY COL VALORE DEGLI ARGOMENTI
	private int numberOfProvision=0; // CONTA QUANTE DISPOSIZIONI SI VOGLIONO TROVARE
	private int numOfProperties;
	private int maxParameter=5; //CONTA IL MASSIMO NUMERO DI PARAMETRI DI UNA DISPOSIZIONE. AGGIUNGO POI 1 PER IL CAMPO TESTO (AUTOMATIZZA?)
	private String [] properties=new String[maxParameter-1];//RIGUARDA GRANDEZZA ARRAY
	private OntModel model;
	private final String [] provisionTypeToFind=new String[1];//ARRAY DI LUNGHEZZA 1 PERCHE' LA STRINGA NON PUO'ESSERE MODIFICATA DAL LISTENER
														//CONTERRA' IL TIPO DI DISPOSIZIONE DA RICERCARE
	private final JPanel mainPane=new JPanel(); //AREA PRINCIPALE DELLA FINESTRA DI RICERCA
	
	public searchWindow(final OntModel m, final ProvisionFrame frame){
		//INIZIALIZZO I VETTORI
		types=new Vector<String>();
		allLabels=new Vector<JLabel []>();
		allFields=new Vector<JTextField[]>();
		model=m;
		setTitle("Inserire parametri di ricerca");
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				
		//CREO LA PARTE PRINCIPALE CON I FORM DI RIEMPIMENTO
		Container contentPane = this.getContentPane(); 
		contentPane.setLayout(new BorderLayout());
		//final JPanel mainPane=new JPanel();//FINAL PER POTERCI ACCEDERE DAL LISTENER
		//mainPane.setLayout(new GridLayout(0,1));
		mainPane.setLayout(new GridLayout(0,maxParameter,5,5));
		contentPane.add(mainPane,BorderLayout.CENTER);
		
		//CREO LA PARTE INFERIORE DELLA FINESTRA ED I PULSANTI 
		JPanel bottomPane=new JPanel();
		bottomPane.setLayout(new FlowLayout());	
		final JButton addButton=new JButton("Aggiungi");
		final JButton okButton=new JButton("Crea Nuovo Articolo"); //FINAL PERCHE' VI ACCEDO DA UN LISTENER
		final JButton okButton2=new JButton("Aggiungi ad un Articolo");//FINAL PERCHE' VI ACCEDO DA UN LISTENER
		JButton cancelButton=new JButton("Annulla");
		bottomPane.add(addButton);
		bottomPane.add(okButton);
		bottomPane.add(okButton2);
		bottomPane.add(cancelButton);
		contentPane.add(bottomPane,BorderLayout.SOUTH);
		//CREO LA FINESTRA INIZIALE DI SCELTA DEL TIPO DI DISPOSIZIONE
		createTypeDialog();
		
		//LISTENER DEL PULSANTE AGGIUNGI NUOVA RICERCA
		ActionListener addListener=new ActionListener(){//ASSOCIATA ALL'AGGIUNGERE ULTERIORI TIPI DA RICERCARE
			public void actionPerformed(ActionEvent e){
				createProvisionTypeChooser();	//CREA LA FINESTRA DI SCELTA DEL TIPO DI DISPOSIZIONE
			}
		};
		addButton.addActionListener(addListener);
		
		//LISTENER DEL PULSANTE CANCELLA DELLA FINESTRA DI RICERCA. CHIUDE LA FINESTRA E TORNA AL PROVISION FRAME
		ActionListener cancelListener=new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();			
			}
		};
		cancelButton.addActionListener(cancelListener);
		
		//LISTENER DEL PULSANTE OK DELLA FINESTRA DI RICERCA. AVVIA LA RICERCA
		ActionListener okListener=new ActionListener(){
			//POSSIBILE PRESENZA DI ALCUNI POSTI VUOTI ALLA FINE DELL'ARRAY
			int find=0; //CONTA LE DISPOSIZIONI CORRETTAMENTE TROVATE
			public void actionPerformed(ActionEvent e) {
				Vector<Provision> provFounded=new Vector<Provision>(numberOfProvision); //CONTIENE TUTTE LE DISPOSIZIONI TROVATE
				//NE PUO' CONTENERE PIU' DEI TIPI RICERCATI
				Vector<Provision> prov=new Vector<Provision>(5); //VETTORE D'APPOGGIO, CONTIENE LE DISPOSIZIONI RITORNATE DA UN'UNICA ESECUZIONE
				//DI SEARCHPROVISION (POSSONO ESSERCI PIU' DISPOSIZIONI RITORNATE DA UN'UNICA RICERCA)
				Vector <String>typesToFind=new Vector<String>(5);//CONTIENE I TIPI DI DISPOSIZIONI. HA LA PROPRIETA' DI AVERE LE STESSE
				//DIMENSIONI DI PROVFOUNDED, UTILE PER EVITARE SUCCESSIVI ERRORI
				//System.out.println("Cerco" +numberOfProvision+" disposizioni");
				for(int i=0;i<numberOfProvision;i++){
						//System.out.println("Cerco un tipo"+types.get(i)+ allLabels.get(i)[0].getText()+" con valore "+allFields.get(i)[0].getText());
						//Provision p=frame.searchProvision(provisionTypeToFind[0],labels,fields);
						prov=frame.searchProvision(types.get(i), allLabels.get(i), allFields.get(i));//PROV CONTIENE TUTTE LE DISPOSIZIONI TROVATE
						if(prov==null){// NESSUNA DISPOSIZIONE TROVATA(FORSE INUTILE)
							dispose();
							return;
						}
						for(int j=0;j<prov.size();j++){
						Provision p=prov.get(j);
						/*if(p==null){//NON HA TROVATO UNA DISPOSIZIONE CON TALI ARGOMENTI
							System.out.println("NON PRESENTE");
						}*/
						//else{//HA TROVATO UNA DISPOSIZIONE SODDISFACENTE LA RICERCA
							//provisions[i]=p;//AGGIUNGO LA DISPOSIZIONE TROVATA ALL'ARRAY
						provFounded.add(p);
						typesToFind.add(types.get(i));
						find++;//INCREMENTO IL CONTATORE DELLE DISPOSIZIONI TROVATE
					//	}
						}
					}

				Provision[] prov1=new Provision[find];//PASSO QUESTO AL METODO DI CREAZIONE/AGGIUNTA AD UN PARAGRAFO
				String []typesToFind1=new String[find];//PASSO QUESTO AL METODO DI CREAZIONE/AGGIUNTA AD UN PARAGRAFO
				for(int i=0;i<find;i++){
					prov1[i]=provFounded.get(i);
					typesToFind1[i]=typesToFind.get(i);
				}
				if(e.getSource()==okButton){
					frame.addParagraph(prov1,typesToFind1);//AGGIUNGO UN PARAGRAFO ALL'ALBERO FORMALE
					dispose();	
				}
				else{//IL PULSANTE CHE HA GENERATO L'EVENTO SARA' OKBUTTON2
					frame.addToParagraph(prov1,typesToFind1);
					dispose();
				}
				
			}
		};
		okButton.addActionListener(okListener);
		okButton2.addActionListener(okListener);
		
		//SI VISUALIZZA LA FINESTRA PRINCIPALE
		pack();
		setVisible(true);
	}
	
	private void createTypeDialog(){//CREA LA FINESTRELLA DI SCELTA TIPO E LA GESTISCE NEL MODIFICARE LA FINESTRA PRINCIPALE
		
		//LISTENER ASSOCIATO AL JRADIOBUTTON, UTILE PER SAPERE IL TIPO DI PROVISION CHE SI VUOL RICERCARE
		final ActionListener radioButtonListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				AbstractButton button=(AbstractButton)e.getSource();//CONVERTO L'ORIGINE DELL'EVENTO IN BUTTON
				String toFind= button.getText();//RICAVO IL NOME DELLA DISPOSIZIONE DAL BUTTON
				provisionTypeToFind[0]=toFind;//LO INSERISCO NELLA STRINGA DI INTERESSE
			}

		};
		
		//provisionTypeToFind[0]=null;
		//CREO LA FINESTRELLA PER INSERIRE IL TIPO DI DISPOSIZIONE DA RICERCARE
		//SUCCESSIVAMENTE, SE SCELGO DI INSERIRE NUOVI TIPI LA FINESTRELLA VIENE RESA NUOVAMENTE VISIBILE MA NON RICREATA
		dialog=new JDialog(this);
		dialog.setTitle("Scegli il tipo di disposizione da ricercare");
		final Container dialogPane=dialog.getContentPane();
		dialogPane.setLayout(new BorderLayout());
		//CREO LA PARTE CONTENENTE IL RADIOBUTTON DI SCELTA DEL TIPO
		JPanel dialogSubPane=new JPanel();
		dialogSubPane.setLayout(new GridLayout(0,1));
		dialogPane.add(dialogSubPane,BorderLayout.CENTER);
		//CREO LA PARTE CONTENENTE I PULSANTI OK E ANNULLA
		JPanel dialogBottomPane=new JPanel();
		dialogBottomPane.setLayout(new FlowLayout());
		JButton okDialogButton=new JButton("OK");
		JButton cancelDialogButton=new JButton("Annulla");
		dialogBottomPane.add(okDialogButton);
		dialogBottomPane.add(cancelDialogButton);
		dialogPane.add(dialogBottomPane,BorderLayout.SOUTH);
		
		//LISTENER CHE GESTISCE L'ANNULLA, CHIUDE LA FINESTRELLA MA PER ORA NON LA FINESTRA DI RICERCA VERA E PROPRIA
		ActionListener cancelDialogListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.dispose();
			}
		};
		cancelDialogButton.addActionListener(cancelDialogListener);
		
		ButtonGroup group=new ButtonGroup();//UTILI PER CREARE LA SCELTA DEL TIPO DI DISPOSIZIONE DA RICERCARE
		JRadioButton aRadioButton;
		
		OntClass ont;
		ExtendedIterator <OntClass>iter=model.listClasses();
		String type;
		while(iter.hasNext()){
			ont=iter.next();
			type=getProvisionType(ont);
			if(type.equals("Duty")||type.equals("Right")){//AGGIUNGO DUTY E RIGHT (HANNO SOTTOCLASSI)
				aRadioButton=new JRadioButton(type);
				aRadioButton.addActionListener(radioButtonListener);
				dialogSubPane.add(aRadioButton);
				group.add(aRadioButton);
			}
			else if(ont.isUnionClass()||type.equals("ImplicitRight")||type.equals("ExplicitRight")||type.equals("RuleOnRule")||
					type.equals("ImplicitDuty")||type.equals("ExplicitDuty")||type.endsWith("Amendment")||ont.hasSubClass()){//NON SONO TIPI DI DISPOSIZIONI
				//NON INSERIRE
			}
			else{//AGGIUNGO TUTTE LE ALTRE DISPOSIZIONI
				aRadioButton=new JRadioButton(type);
				aRadioButton.addActionListener(radioButtonListener);
				dialogSubPane.add(aRadioButton);
				group.add(aRadioButton);
			}
		}
		
		//LISTENER CHE AGGIUNGE I CAMPI DELLA DISPOSIZIONI AL MAINPANEL SE CLICCO SU OK
		ActionListener addDialogListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				while(provisionTypeToFind[0]==null){//SE NON HO SELEZIONATO ALCUN TIPO DI DISPOSIZIONE
					JOptionPane.showMessageDialog(dialog,"Specificare un tipo di disposizione", "Errore nella scelta del tipo",JOptionPane.ERROR_MESSAGE);
					return;
				}
				types.add(provisionTypeToFind[0]);//AGGIUNGO AL VETTORE DEI TIPI DA RICERCARE, IL TIPO DELLA DISPOSIZIONE
				numberOfProvision++;//RICERCO UNA NUOVA DISPOSIZIONE, AUMENTO IL CONTATORE
				numOfProperties=0;//REIMPOSTO A ZERO IL NUMERO DI PARAMETRI DELLA DISPOSIZIONE
				String provisionType="http://provisions.org/model/1.0#"+provisionTypeToFind[0];//AGGIUNGO L'URI (AUTOMATIZZA)
				//RICERCO NEL MODELLO I PARAMETRI DELLA DISPOSIZIONE
				OntClass ont=model.getOntClass(provisionType);
				ExtendedIterator <OntProperty>iter=ont.listDeclaredProperties(); // SAREBBE PIU' EFFICIENTE, MA DEVO TROVARE UN REASONER COL QUALE FUNZIONA
				while(iter.hasNext()){								//PROPRIETA' CONDIVISE NON SONO RITORNATE CORRETTAMENTE
					properties[numOfProperties]=getPropertyName(iter.next().toString());
					numOfProperties++; //HO TROVATO UN PARAMETRO, QUINDI AUMENTO DI UNO IL TOTALE
				}
				ExtendedIterator <UnionClass> iterUnion=model.listUnionClasses();
				while(iterUnion.hasNext()){
					UnionClass ont2=(UnionClass)iterUnion.next(); //CLASSE UNIONE 
					if(ont2.hasOperand(ont)){
						iter=ont2.listDeclaredProperties();//ITER CONTIENE ORA LE PROPRIETà DELLE UNION CLASS CUI LA DISPOSIZIONE APPARTIENE
						properties[numOfProperties]=getPropertyName(iter.next().toString());
						numOfProperties++;
					}
				}
				//AGGIUNGO LABEL E CAMPI DI TESTO AL PANNELLO PRINCIPALE
				mainPane.add(new JLabel("Tipo: "+provisionTypeToFind[0]));
				int j=0; //CONTA QUANTI INSERIMENTI DI LABEL EFFETTIVAMENTE SI FANNO
				labels=new JLabel[numOfProperties];
				for(int i=0;i<numOfProperties;i++){
					labels[i]=new JLabel(properties[i]);
					mainPane.add(labels[i]);
					j++;
				}
				allLabels.add(labels);//AGGIUNGO AL VETTORE DEGLI ARGOMENTI L'ARRAY RELATIVO ALLA DISPOSIZIONE
				if(j<maxParameter-1)//SERVE PER INCOLONNARE CORRETTAMENTE ED ANDARE A CAPO DOPO MAXPARAMETER LABEL
				{
					for(int i=j;i<maxParameter-1;i++){
						mainPane.add(new JLabel());
					}
				}
				mainPane.add(new JLabel()); //SERVE PER INCOLONNARE CORRETTAMENTE LABEL E FIELD
				fields=new JTextField[numOfProperties];
				for(int i=0;i<numOfProperties;i++){
					fields[i]=new JTextField(10);
					mainPane.add(fields[i]);//AGGIUNGO AL VETTORE DEI VALORI DEGLI ARGOMENTI L'ARRAY RELATIVO ALLA DISPOSIZIONE	
				}
				allFields.add(fields);
				if(j<maxParameter-1){//SERVE PER INCOLONNARE CORRETTAMENTE ED ANDARE A CAPO DOPO MAXPARAMETER FIELDS
					for(int i=j;i<maxParameter-1;i++){
						mainPane.add(new JLabel());
					}
				}			
				//provisionTypeToFind[0]=null;
				dialog.dispose();
			}
		};
		okDialogButton.addActionListener(addDialogListener);
		provisionTypeToFind[0]=null;
		//RENDO VISIBILE LA FINESTRA
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
		
	}
	private void createProvisionTypeChooser(){
		createTypeDialog();//RICREO LA FINESTRA DI SCELTA TIPO
		numOfProperties=0;//REIMPOSTO A ZERO IL NUMERO DI PARAMETRI DELLA DISPOSIZIONE
		String provisionType="http://provisions.org/model/1.0#"+provisionTypeToFind[0];//AGGIUNGO L'URI (AUTOMATIZZA)
		//RICERCO NEL MODELLO I PARAMETRI DELLA DISPOSIZIONE
		OntClass ont=model.getOntClass(provisionType);
		if(ont==null){//SE NON HO SCELTO IL TIPO DI DISPOSIZIONE E POI CLICCO ANNULLA, NON AGGIUNGE NIENTE ALLA FINESTRA PRINCIPALE
			return;
		}//ALTRIMENTI
		ExtendedIterator <OntProperty>iter=ont.listDeclaredProperties(); // TROVO LE PROPRIETA' UNICHE DELLA CLASSE
		while(iter.hasNext()){
			properties[numOfProperties]=getPropertyName(iter.next().toString());
			numOfProperties++; //HO TROVATO UN PARAMETRO, QUINDI AUMENTO DI UNO IL TOTALE
		}
		ExtendedIterator <UnionClass> iterUnion=model.listUnionClasses();//TROVO LE PROPRIETA' CONDIVISA DELLA CLASSE
		while(iterUnion.hasNext()){
			UnionClass ont2=(UnionClass)iterUnion.next(); //CLASSE UNIONE 
			if(ont2.hasOperand(ont)){
				iter=ont2.listDeclaredProperties();//ITER CONTIENE ORA LE PROPRIETà DELLE UNION CLASS CUI LA DISPOSIZIONE APPARTIENE
				properties[numOfProperties]=getPropertyName(iter.next().toString());
				numOfProperties++;
			}	
		}
		//provisionTypeToFind[0]=null;
		this.pack();//FORZO IL RIDISEGNARSI DELLA FINESTRA RICERCA, PER AVERLA CORRETTAMENTE AGGIORNATA
		dialog.dispose();
	}

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
