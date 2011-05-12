package it.cnr.ittig.VisualProvisionManager;

import it.cnr.ittig.VisualProvisionManager.applicationFrame.RDFFileFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.hp.hpl.jena.ontology.OntModel;

public class FrameUtil {
	public static void saveFile(JFrame frame, OntModel model, boolean modified, String savedPath)
	{
		File selectedFile;
		FileOutputStream stream=null;
		if(savedPath!=null){
			selectedFile=new File(savedPath);
			try{
				stream=new FileOutputStream(selectedFile);
			}
			catch (Exception ex){
				JOptionPane.showMessageDialog(frame,"Errore nel salvataggio del file", "Errore", JOptionPane.ERROR_MESSAGE);
			}
			if(stream != null){
				model.write(stream);
				modified=false; //indico che il lavoro non ha subito modifiche dall'ultimo salvataggio
				try{
					stream.close();
				}catch(IOException ex){
					System.out.println("Errore nel chiusura dello stream");
					ex.printStackTrace();
				}
			}
			
		}
		else
		{
			saveFileWithName( frame,  model,  modified,  savedPath);
		}
	}

	
	public static void saveFileWithName(JFrame frame, OntModel model, boolean modified, String savedPath) //salva un documento su un nuovo file TODO salvare per bene il documento
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
				path=path+".rdf";
				selectedFile=new File(path);
			}
			//SE IL FILE NON HA ESTENSIONE rdf, la aggiungo TODO possibili casi con due estensioni, come gestirli?
			if(!path.endsWith(".rdf")){
				path=path+".rdf";
				selectedFile=new File(path);
				//System.out.print("path not ends with rdf");
			}
			//FileWriter writer=null;
			OutputStream stream=null;
			try{
				// writer=new FileWriter(selectedFile);
				 stream=new FileOutputStream(selectedFile);
			}
			catch (Exception ex){
				JOptionPane.showMessageDialog(frame,"Errore nel salvataggio del file", "Errore", JOptionPane.ERROR_MESSAGE);
			}
			if(stream != null){
				// model= ModelFactory.createDefaultModel();
				model.write(stream);
				modified=false; //indico che il lavoro non ha subito modifiche dall'ultimo salvataggio
				//AGGIORNO IL SAVED PATH 
				savedPath=path;
				try{
					stream.close();
				}catch(IOException ex){
					System.out.println("Errore nel chiusura dello stream");
					ex.printStackTrace();
				}
			}
		}if(status == JFileChooser.ERROR_OPTION){
		// TO DO GESTIRE LA SITUAZIONE DI ERRORE
		}
		else{
			//SCELGO ANNULLA, NON FARE NIENTE
		}
	}
	
	public static boolean stringWithCarachter(String str)
	{
		boolean ret=false;
		if(str.length()==0){
			return false;
		}
		Character c=null;
		for(int i=0; i<str.length();i++){
			c=str.charAt(i);
			if(Character.getNumericValue(c)!=-1){
				ret=true;;
			}
		}		
		return ret;
	}
	
	public static Action findAction(Action actions[], String key) { //POTREI MIGLIORARLO; QUI RICREA L'HASTHTABLE OGNI VOLTA
		Hashtable<Object, Action> commands = new Hashtable<Object, Action>();
		for (int i = 0; i < actions.length; i++) {
		Action action = actions[i];
		commands.put(action.getValue(Action.NAME), action);
		}
		return commands.get(key);
		}


	public static String clearNS(String arg){// ELIMINA LA PARTE RELATIVA AL NAMESPACE, UTILE PER NOME ARGOMENTI O VALORE
		char charAt;
		String value=arg;
		for(int i=0;i<=arg.length()-1;i++){
			charAt=arg.charAt(i);
			if(charAt=='#'){
				value=arg.substring(i+1,arg.length());
				break;
			}
		}
		return value;
	}
}
