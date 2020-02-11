package es.ubu.lsi.export.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.controllers.Controller;
import es.ubu.lsi.export.CSVBuilderAbstract;
import es.ubu.lsi.model.CourseModule;
import es.ubu.lsi.model.DataBase;

/**
 * Builds the course module file.
 * 
 * @author Raúl Marticorena
 * @since 2.4.0.0
 */
public class CSVCourseModule extends CSVBuilderAbstract {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVCourseModule.class);

	/** Header. */
	private static final String[] HEADER = new String[] { "CourseModule", "ModuleName", "ModuleTypeName", "SectionId" };

	/** Comparator for modules. */
	private static final Comparator<CourseModule> COMPARATOR_BY_ID = Comparator.comparing(CourseModule::getCmid); 

	/**
	 * Constructor.
	 * 
	 * @param name name 
	 * @param dataBase dataBase
	 */
	public CSVCourseModule(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		Set<CourseModule> modulesSet = Controller.getInstance().getActualCourse().getModules();
		List<CourseModule> modules = new ArrayList<>(modulesSet);
		// Sort
		Collections.sort(modules, COMPARATOR_BY_ID);
		// Build body
		for (CourseModule module : modules) {
			LOGGER.debug("Data line: {}, {}, {}, {}", module.getCmid(), module.getModuleName(), module.getDescription(), module.getSection().getId());
			getData().add(new String[] { 
					Integer.toString(module.getCmid()),
					module.getModuleName(),
					module.getModuleType().getModName(),
					Integer.toString(module.getSection().getId())
			});
		}
	}

}
