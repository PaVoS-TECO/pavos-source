package edu.teco.pavos.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Implementation of the FileReaderStrategy interface for CSV files.
 */
public class CSVReaderStrategy implements FileReaderStrategy {
	
	ArrayList<String> sensors = new ArrayList<String>();
	ArrayList<String> observedProperties = new ArrayList<String>();
	ArrayList<String> locations = new ArrayList<String>();
	ArrayList<String> things = new ArrayList<String>();
	ArrayList<String> datastreams = new ArrayList<String>();
	ArrayList<String> observations = new ArrayList<String>();
	//ArrayList<String> featureOfInterest = new ArrayList<String>();
	//FoI is one row in the observation directly saved as json
	int errorlines = 0;

    /**
     * Default constructor
     */
    public CSVReaderStrategy() {
    }

    /**
     * Reads from a File as specified by the FilePath and sends the information in
     * it to the FROST-Server using the FrostSender that was provided.
     * @param file Is the File to Import.
     */
    public void sendFileData(File file) {
    	 
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] separated = line.split(",");
				if (separated.length >= 2) {
					//TODO handle data
					
				} else {
					this.errorlines++;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

}
