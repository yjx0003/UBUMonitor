package export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import controllers.Controller;
import model.DataBase;
import model.EnrolledUser;

/**
 * Abstract builder.
 * 
 * @author Raúl Marticorena
 *  * @since 2.4.0.0
 */
public abstract class CSVBuilderAbstract implements CSVBuilder {
	
	/** Export path. **/
	static final String PATH;
	
	// Build the path if not exists.
	static {
		PATH = "./export/" + Controller.getInstance().getUser().getFullName() + "/"
				+ Controller.getInstance().getActualCourse().getFullName();
		File directory = new File(PATH);
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}
	}
	
	/** Comparator for enrolleduser by fullname. */
	protected static Comparator<EnrolledUser> compareByFullName = (EnrolledUser o1, EnrolledUser o2) -> o1.getFullName()
			.compareTo(o2.getFullName());
	
	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVBuilderAbstract.class);

	/** File name. */
	private String name;
	
	/** Header. */
	private String[] header;
	
	/** Body data. */
	private List<String[]> data;	

	/** Database. */
	private DataBase dataBase;
	
	/** 
	 * Gets header.
	 * 
	 * @return header
	 */
	public String[] getHeader() {
		return header;
	}

	/**
	 * Sets header.
	 * 
	 * @param header header
	 */
	protected void setHeader(String[] header) {
		this.header = header;
	}

	/**
	 * Gets data.
	 * 
	 * @return data body
	 */
	public List<String[]> getData() {
		return data;
	}

	/**
	 * Sets datata.
	 * 
	 * @param data data
	 */
	protected void setData(List<String[]> data) {
		this.data = data;
	}

	
	/** 
	 * Gets database.
	 * 
	 * @return database
	 */
	public DataBase getDataBase() {
		return dataBase;
	}

	/**
	 * Sets database. 
	 * 
	 * @param dataBase database
	 */
	private void setDataBase(DataBase dataBase) {
		this.dataBase = dataBase;
	}

	/**
	 * Constructor.
	 * 
	 * @param name name
	 * @param dataBase dataBase
	 * @param header header
	 */
	public CSVBuilderAbstract(String name, DataBase dataBase, String[] header) {
		setName(name);
		setDataBase(dataBase);
		setHeader(header);
		setData(new ArrayList<>());
	}

	/**
	 * Gets file name.
	 * 
	 * @return file name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets file name.
	 * 
	 * @param name file name
	 */
	private void setName(String name) {
		this.name = name;
	}	
	
	public String getFileName() {
		return name + EXTENSION;
	}

	/**
	 * Writes the data to a csv file. Using OpenCSV (see http://http://opencsv.sourceforge.net/).
	 */
	@Override
	public void writeCSV() {
		final CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(PATH + "/" +  getFileName()));
			writer.writeAll(getData());
			writer.close();
		} catch (IOException e) {
			LOGGER.error("Error writing csv file: {}.csv", getFileName());
			throw new RuntimeException("Error exporting CSV file" + getFileName(), e);
		}
	}
	
	/**
	 * Builds header. Add the header to the data as first row.
	 */
	@Override
	public void buildHeader() {
		getData().add(getHeader());
	}

}
