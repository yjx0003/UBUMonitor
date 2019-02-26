package controllers.UBULogs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.UBULogs.logCreator.LogCreator;
import model.Log;
import model.UBUGrades;

public class UBULogController {
	static final Logger logger = LoggerFactory.getLogger(UBULogController.class);
	private UBUGrades ubuGrades;
	public static void main(String[] args) throws IOException {
		String host = "http://localhost";
		String username = "profesor1";
		String password = "Yi123456-";
		int idUser = 7;
		int idCourse = 2;
		String usertimezone = "99"; //no cambiar si tienes puesto que la zona horaria sea del servidor

		//DownloadLogController dlc = new DownloadLogController(host, username, password, idUser, idCourse, usertimezone);
		//String log=dlc.downloadLog();
		//System.out.println(log);
		Reader log=new InputStreamReader(new FileInputStream("C:/Users/PC/Downloads/logs_MEPRO_20190216-1940.csv"),"UTF-8");
		List<Map<String,String>>a=UBULogParser.parse(log);
		System.out.println(a);
		log.close();
		LogCreator creator = null;
		creator.setZoneId(ZoneId.of("Europe/Madrid"));
		
		List<Log> l=creator.createLogs(a);
		System.out.println(l);
	}
	
	public UBULogController (UBUGrades ubuGrades) {
		this.ubuGrades=ubuGrades;
	}
	
	public void updateCourseLog(){
		
	}
	
}
