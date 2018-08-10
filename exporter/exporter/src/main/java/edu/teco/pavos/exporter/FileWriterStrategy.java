package edu.teco.pavos.exporter;

import java.io.BufferedInputStream;
import java.io.File;

/**
 * Interface for the FileWriterStrategy classes. Realization of a Strategy to be able to swap out the way a
 * File has to be saved.
 */
public interface FileWriterStrategy {

    /**
     * Creates a File as specified by the FilePath and saves the Data from the provided KafkaStream into it.
     * @param stream is the KStream, that should be exported to a File.
     * @param file Is the FilePath, where the new File should be created.
     */
    void saveToFile(BufferedInputStream stream, File file);

}
