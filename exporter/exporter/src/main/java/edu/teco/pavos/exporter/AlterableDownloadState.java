package edu.teco.pavos.exporter;

import java.io.File;

/**
 * Verifies for the State of a Download. Can also change it.
 */
public class AlterableDownloadState extends DownloadState {

    /**
     * Default constructor
     * @param id is the ID of that download
     */
    public AlterableDownloadState(String id) {
        super(id);
    }

    /**
     * Defines the FilePath for the DownloadID.
     * @param path Is the FilePath to be set.
     */
    public void setFilePath(File path) {
        super.filePath = path;
    }

    /**
     * Validate, that the File is ready to be downloaded.
     */
    public void setFileReadyForDownload() {
        super.ready = true;
    }

    /**
     * Save the changed Data persistently.
     */
    public void savePersistent() {
        // TODO implement here
    }

}
