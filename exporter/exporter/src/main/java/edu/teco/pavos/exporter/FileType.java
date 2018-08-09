package edu.teco.pavos.exporter;

/**
 * Is used to store a FileExtension information and give  the right  FileWriter for this FileExtension.
 */
public class FileType {

    /**
     * The FileExtension is defining the FileType.
     */
    private String extension;

    /**
     * Default constructor
     * @param extension that defines this file type
     */
    public FileType(String extension) {
    	this.extension = extension;
    }
    
    /**
     * Gives an instance of the implemented FileWriter that is associated with this FileType, thus this
     * FileExtension. To do so it uses the static method getFileWriterForFileExtension from the FileTypesUtility class.
     * @return Is a new instance of an implementation of a FilWriterStrategy.
     * @throws IllegalFileExtensionException 
     */
    public FileWriterStrategy getFileWriter() throws IllegalFileExtensionException {
        return FileTypesUtility.getFileWriterForFileExtension(this.extension);
    }

}
