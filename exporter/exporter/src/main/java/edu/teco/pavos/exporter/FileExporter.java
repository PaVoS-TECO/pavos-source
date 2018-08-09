package edu.teco.pavos.exporter;

import java.io.File;

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
    	String output = "";
    	char rndm = (char) (int) (Math.random() * 90 + 33);
    	int i = 0;
    	while (i++ < 20) {
    		output += rndm;
    	}
    	return output;
    }

    /**
     * Generates the File with the desired Data.
     */
    public void createFile() {
    	//TODO get Stream
    	ExportStreamGenerator streamer = new ExportStreamGenerator(this.properties);
    	//KStream stream = streamer.createExportStream();
    	FileType fileType = new FileType(this.properties.getFileExtension());
    	try {
			FileWriterStrategy fileWriter = fileType.getFileWriter();
			//TODO determine Path for data
	    	String path = "";
	    	//fileWriter.saveToFile(stream, new File(path));
	    	this.ads.setFilePath(new File(path));
	    	this.ads.setFileReadyForDownload();
	    	this.ads.savePersistent();
		} catch (IllegalFileExtensionException e) {
			// TODO Auto-generated catch block
			// write error in DownloadState
		}
    }

}
