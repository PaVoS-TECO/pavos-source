package edu.teco.pavos.exporter;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class that provides static methods to get all supported FileExtensions and one to get a new Instance
 * of the FileWriter associated with a given FileExtension. If a new FileWriter is added to PaVoS, this class
 * needs some changed to be able to return the new FileWriter.
 */
public class FileTypesUtility {

    /**
     * Default constructor
     */
    private FileTypesUtility() { }

    /**
     * Gives all supported FileExtensions in an ArrayList.
     * @return Is an Array of the possible FileExtensions for an Export.
     */
    public static Set<String> getAllPossibleFileExtensions() {
        Set<String> extensions = new HashSet<String>();
        extensions.add("csv");
        extensions.add("netcdf");
        return extensions;
    }

    /**
     * Gives a new Instance of the FileWriter associated with a given FileExtension.
     * @param extension Is the FileExtension for which a new instance of an Implementation of the FileWriterStrategy
     * is wanted.
     * @return Is the instance of the implementation of a FileWriterStrategy.
     * @throws IllegalFileExtensionException 
     */
    public static FileWriterStrategy getFileWriterForFileExtension(String extension)
    		throws IllegalFileExtensionException {
        if (extension.equals("csv")) {
        	return new CSVWriterStrategy();
        } else if (extension.equals("netcdf")) {
        	return new NetCDFWriterStrategy();
        } else {
        	throw new IllegalFileExtensionException("Error");
        }
    }

}