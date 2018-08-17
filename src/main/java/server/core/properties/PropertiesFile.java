package com.github.masterries.kafka.properties;

public class PropertiesFile implements PropertiesFileInterface {

    /**
     * Default constructor
     */
    public PropertiesFile() {
    }

    /**
     * This is the Properties File.
     */
    private String propertiesFileName;


    /**
     * This Methodes returns the requestet propertie Value
     * @param propertyName propertyName is the name of the Requested Property
     *
     * @return Return the Value to the Requested Property
     */
    public String getPropValues(String propertyName) {
        // TODO implement here
        return "";
    }

    /**
     * The Method adds a key-value pair to the Properties object. To get back to the value later,  is called with the key and then return
     * @param propertyName propertyName is the Name of the Property which you want to edit
     * @param propertyValue propertyValue is the Value of the Property which you want to edit
     * @return true wenn the property got set false otherwise
     */
    public boolean putProperty(String propertyName, String propertyValue) {
        // TODO implement here
        return false;
    }

    /**
     * This Method saves the PropertiesFile with the Option to do a Backup of the File
     * @param makeBackup true if you want to make a Bachup
     * @return true when the file got saved, false otherwise
     */
    public boolean save(boolean makeBackup) {
        // TODO implement here
        return false;
    }

}


