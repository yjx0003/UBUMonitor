package export;

import model.DataBase;

/**
 * CSV Builder.
 * 
 * @author Raúl Marticorena
 *  * @since 2.4.0.0
 */
public interface CSVBuilder {
	
	/** File extensio. */
	static final String EXTENSION = ".csv";

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
