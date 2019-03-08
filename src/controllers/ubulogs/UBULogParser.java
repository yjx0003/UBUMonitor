package controllers.ubulogs;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UBULogParser {

	static final Logger logger = LoggerFactory.getLogger(UBULogParser.class);
	

	

	private UBULogParser() {

	}

	public static List<Map<String, String>> parse(Reader reader) {

		try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
			
			List<Map<String, String>> logs = new ArrayList<Map<String, String>>();
			Set<String> headers= csvParser.getHeaderMap().keySet();
			
			logger.info("Las columnas el parser del csv son: ",headers);
			
			for (CSVRecord csvRecord : csvParser) {
				
				Map<String,String> log = new HashMap<String, String>();
				logs.add(log);
				
				for (String header :headers) {
					log.put(header, csvRecord.get(header));
				}

			}
		return logs;
			

		} catch (Exception e) {
			logger.error("No se ha podido parsear el contenido", e);
		}
		return null;
	}
}
