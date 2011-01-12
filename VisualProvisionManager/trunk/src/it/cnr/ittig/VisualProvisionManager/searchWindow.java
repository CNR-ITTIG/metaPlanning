package it.cnr.ittig.VisualProvisionManager;

import it.cnr.ittig.ProvisionModel.OntUtils;
import it.cnr.ittig.VisualProvisionManager.Provision.Provision;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.acl.Group;

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
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class searchWindow extends JDialog{
	JDialog dialog;
	JLabel [] labels; //ELENCA TUTTE LE LABEL ASSOCIATE AGLI ARGOMENTI DELLA DISPOSIZIONE
	JTextField [] fields;
	int numOfProperties;
	int maxParameter=5; //CONTA IL MASSIMO NUMERO DI PARAMETRI DI UNA DISPOSIZIONE. AGGIUNGO POI 1 PER IL CAMPO TESTO (AUTOMATIZZA?)
	String [] properties=new String[maxParameter];//RIGUARDA GRANDEZZA ARRAY
	OntModel model;
	final String [] provisionTypeToFind=new String[1];//ARRAY DI LUNGHEZZA 1 PERCHE' LA STRINGA NON PUO'ESSERE MODIFICATA DAL LISTENER
														//CONTERRA' IL TIPO DI DISPOSIZIONE DA RICERCARE
	final JPanel mainPane=new JPanel(); //AREA PRINCIPALE DELLA FINESTRA DI RICERCA
	
	public searchWindow(final OntModel m, final ProvisionFrame frame){
		model=m;
		setTitle("Inserire parametri di ricerca");
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		
		
		//CREO LA PARTE PRINCIPALE CON I FORM DI RIEMPIMENTO
		Container contentPane = this.getContentPane(); 
		contentPane.setLayout(new BorderLayout());
		//final JPanel mainPane=new JPanel();//FINAL PER POTERCI ACCEDERE DAL LISTENER
		//mainPane.setLayout(new GridLayout(0,1));
		mainPane.setLayout(new GridLayout(0,5));
		contentPane.add(mainPane,BorderLayout.CENTER);
		
		//CREO I PULSANTI 
		JPanel bottomPane=new JPanel();
		bottomPane.setLayout(new FlowLayout());
		
		final JButton addButton=new JButton("Aggiungi");
		JButton okButton=new JButton("OK");
		JButton cancelButton=new JButton("Annulla");
		bottomPane.add(addButton);
		bottomPane.add(okButton);
		bottomPane.add(cancelButton);
		contentPane.add(bottomPane,BorderLayout.SOUTH);
		
		createTypeDialog();
			
		/**/
				
		ActionListener addListener=new ActionListener(){
			public void actionPerformed(ActionEvent e){
				createProvisionTypeChooser();
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
			public void actionPerformed(ActionEvent e) {
				Provision p=frame.searchProvision(provisionTypeToFind[0],labels,fields);
				if(p==null){
					System.out.println("NON PRESENTE");
				}
				else{
					System.out.println(p.toString());
				}
				dispose();			
				
			}
		};
		okButton.addActionListener(okListener);
		
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
		JButton okDialogButton=new JButton("OK");
		JButton cancelDialogButton=new JButton("Annulla");
		JPanel dialogBottomPane=new JPanel();
		dialogBottomPane.setLayout(new FlowLayout());
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
		
		ButtonGroup group=new ButtonGroup();
		JRadioButton aRadioButton;
		
		OntClass ont;
		ExtendedIterator <OntClass>iter=model.listClasses();
		while(iter.hasNext()){
			ont=iter.next();
			if(ont.isUnionClass()||getProvisionType(ont).equals("ImplicitRight")||getProvisionType(ont).equals("RuleOnRule")||ont.hasSubClass()){//NON SONO TIPI DI DISPOSIZIONI
				//NON INSERIRE
			}
			else{
				aRadioButton=new JRadioButton(getProvisionType(ont));
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
				numOfProperties=0;//REIMPOSTO A ZERO IL NUMERO DI PARAMETRI DELLA DISPOSIZIONE
				String provisionType="http://provisions.org/model/1.0#"+provisionTypeToFind[0];//AGGIUNGO L'URI (AUTOMATIZZA)
				//RICERCO NEL MODELLO I PARAMETRI DELLA DISPOSIZIONE
				OntClass ont=model.getOntClass(provisionType);
				ExtendedIterator iter=ont.listDeclaredProperties(); // SAREBBE PIU' EFFICIENTE, MA DEVO TROVARE UN REASONER COL QUALE FUNZIONA
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
				mainPane.add(new JLabel("Tipo: "+provisionTypeToFind[0]));
				int j=0; //CONTA QUANTI INSERIMENTI DI LABEL EFFETTIVAMENTE SI FANNO
				labels=new JLabel[numOfProperties];
				for(int i=0;i<numOfProperties;i++){
					labels[i]=new JLabel(properties[i]);
					mainPane.add(labels[i]);
					j++;
				}
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
					mainPane.add(fields[i]);	
				}
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
		}
		ExtendedIterator iter=ont.listDeclaredProperties(); // SAREBBE PIU' EFFICIENTE, MA DEVO TROVARE UN REASONER COL QUALE FUNZIONA
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

		this.pack();//FORZO IL RIDISEGNARSI DELLA FINESTRA RICERCA, PER AVERLA CORRETTAMENTE AGGIORNATA
		//provisionTypeToFind[0]=null;
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
