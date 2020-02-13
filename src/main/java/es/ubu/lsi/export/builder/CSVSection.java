package es.ubu.lsi.export.builder;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.export.CSVBuilderAbstract;
import es.ubu.lsi.model.DataBase;
import es.ubu.lsi.model.Section;

public class CSVSection extends CSVBuilderAbstract {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVRole.class);

	/** Header. */
	private static final String[] HEADER = new String[] { "SectionId", "Name", "visible", "SectionNumber" };

	public CSVSection(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		Set<Section> sections = getDataBase().getActualCourse().getSections();
		for (Section section : sections) {
			LOGGER.debug("Data line: {}, {}, {}, {}", section.getId(), section.getName(), section.isVisible(),
					section.getSectionNumber());
			getData().add(new String[] {
					Integer.toString(section.getId()),
					section.getName(),
					Boolean.toString(section.isVisible()),
					Integer.toString(section.getSectionNumber())
			});
		}

	}

}
