package edu.teco.pavos.exporter;

import java.io.File;

/**
 * Exporter of Data from Kafka to a File.
 */
public class FileExporter {
	
	private ExportProperties properties;
	private AlterableDownloadState ads;

    /**
     * Default constructor
     * @param properties for the export
     * @param downloadID of the download
     */
    public FileExporter(ExportProperties properties, String downloadID) {
    	this.properties = properties;
    	this.ads =  new AlterableDownloadState(downloadID);
    }

    /**
     * Generates the File with the desired Data.
     */
    public void createFile() {
    	String extension = this.properties.getFileExtension();
    	FileType fileType = new FileType(extension);
    	try {
			FileWriterStrategy fileWriter = fileType.getFileWriter();
			String filename = this.ads.getID() + "." + extension;
			String dirPath = System.getProperty("user.dir") + File.separator + "exports";
	    	String path = dirPath + File.separator + filename;
	    	File directory = new File(dirPath);
	    	if (!directory.exists()) {
	    		directory.mkdir();
	    	}
	    	fileWriter.saveToFile(this.properties, new File(path));
	    	this.ads.setFilePath(new File(path));
	    	this.ads.setFileReadyForDownload();
	    	this.ads.savePersistent();
		} catch (IllegalFileExtensionException e) {
			this.ads.setFileHadError();
			this.ads.savePersistent();
		}
    }

}
