package controllers.ubulogs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubulogs.logcreator.LogCreator;
import model.Log;
import model.UBUGrades;

public class UBULogController {
	static final Logger logger = LoggerFactory.getLogger(UBULogController.class);
	private UBUGrades ubuGrades;

	
	public UBULogController (UBUGrades ubuGrades) {
		this.ubuGrades=ubuGrades;
	}
	
	public void updateCourseLog(){
		
	}
	
}
