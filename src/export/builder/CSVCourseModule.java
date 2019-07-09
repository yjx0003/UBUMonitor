package export.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import export.CSVBuilderAbstract;
import model.DataBase;
import model.mod.Module;

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
	private static final String[] HEADER = new String[] { "CourseModule", "ModuleName", "ModuleTypeName" };

	/** Comparator for modules. */
	static Comparator<Module> compareById = (Module o1, Module o2) -> Integer.valueOf(o1.getCmid())
			.compareTo(Integer.valueOf(o2.getCmid()));

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
		Set<Module> modulesSet = Controller.getInstance().getActualCourse().getModules();
		List<Module> modules = new ArrayList<>(modulesSet);
		// Sort
		Collections.sort(modules, compareById);
		// Build body
		for (Module module : modules) {
			LOGGER.debug("Data line: {}, {}, {}", module.getCmid(), module.getModuleName(), module.getDescription());
			getData().add(new String[] { Integer.toString(module.getCmid()), module.getModuleName(),
					module.getModuleType().getModName()

			});
		}
	}

}
