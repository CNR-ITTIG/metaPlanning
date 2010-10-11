package it.cnr.ittig.VisualProvisionManager;

import it.cnr.ittig.VisualProvisionManager.applicationFrame.RDFFileFilter;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.hp.hpl.jena.ontology.OntModel;

public class FrameUtil {
	public static void saveFile(JFrame frame, OntModel model, boolean modified, String savedPath)
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

}
