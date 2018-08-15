package edu.teco.pavos.exporter;

import java.io.File;
import java.util.UUID;

/**
 * Exporter of Data from Kafka to a File.
 */
public class FileExporter extends AbstractExporter {
	
	private ExportProperties properties;
	private AlterableDownloadState ads;

    /**
     * Default constructor
     * @param properties for the export
     */
    public FileExporter(ExportProperties properties) {
    	this.properties = properties;
    }

    /**
     * Creates Information for that Export. These Information will be used to identify a
     * File for the WebGUI, that gets the created DownloadID.
     * @return Is the DownloadID for the started Export.
     */
    public String createFileInformation() {
    	String id = createRandomDownloadID();
    	this.ads =  new AlterableDownloadState(id);
    	this.ads.savePersistent();
        return id;
    }
    
    private static String createRandomDownloadID() {
    	String output = "pavos";
    	output += UUID.randomUUID().toString().replace("-", "");
    	return output;
    }

    /**
     * Generates the File with the desired Data.
     */
    public void createFile() {
    	//TODO get Stream
    	ExportStreamGenerator streamer = new ExportStreamGenerator(this.properties);
    	//KStream stream = streamer.createExportStream();
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
	    	//fileWriter.saveToFile(stream, new File(path));
	    	this.ads.setFilePath(new File(path));
	    	this.ads.setFileReadyForDownload();
	    	this.ads.savePersistent();
		} catch (IllegalFileExtensionException e) {
			this.ads.setFileHadError();
			this.ads.savePersistent();
		}
    }

}
