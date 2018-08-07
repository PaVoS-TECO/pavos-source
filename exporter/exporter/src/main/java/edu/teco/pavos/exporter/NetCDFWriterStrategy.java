package edu.teco.pavos.exporter;

import java.io.File;

/**
 * Implementation of the FileWriterStrategy interface for NetCDF files.
 */
public class NetCDFWriterStrategy implements FileWriterStrategy {

    /**
     * Default constructor
     */
    public NetCDFWriterStrategy() {
    }

    /**
     * Creates a File as specified by the FilePath and saves the Data from the provided KafkaStream into it.
     * @param stream is the KStream, that should be exported to a File.
     * @param path Is the FilePath, where the new File should be created.
     */
	public void saveToFile(KStream stream, File file) {
		// TODO Auto-generated method stub
		
	}

}
