package it.cnr.ittig.ProvisionModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.hp.hpl.jena.ontology.OntModel;

public class ProvisionModel {
	

	OntModel ProvisionModel;
	
	public void setProvisionModel(OntModel ProvModel)
	{
		this.ProvisionModel = ProvModel;
	}
	
	
	
	public void writeProvisionModelOnFile(OntModel ProvisionModel, String RdfFilename)
	{
		File file = new File(RdfFilename);
	    FileOutputStream f = null;
		try {
			f = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    ProvisionModel.write(f);
		
	}
	

}
