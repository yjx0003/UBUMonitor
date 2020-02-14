package es.ubu.lsi.ubumonitor.export;

import es.ubu.lsi.ubumonitor.model.DataBase;

/**
 * CSV Builder.
 * 
 * @author Raúl Marticorena
 *  * @since 2.4.0.0
 */
public interface CSVBuilder {
	


	/**
	 * Builds the header.
	 */
	public void buildHeader();
	
	/**
	 * Builds the body.
	 */
	public void buildBody();
	
	/**
	 * Gets file name.
	 * 
	 * @return file name
	 */
	public String getFileName();
	
	/**
	 * Gets current database.
	 * 
	 * @return database
	 */
	DataBase getDataBase();
	
	/**
	 * Writes the data to csv file.
	 */
	public void writeCSV();
	
}
