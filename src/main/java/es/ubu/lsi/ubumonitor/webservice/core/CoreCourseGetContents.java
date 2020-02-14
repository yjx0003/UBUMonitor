package es.ubu.lsi.ubumonitor.webservice.core;

import es.ubu.lsi.ubumonitor.webservice.ParametersCourseid;
import es.ubu.lsi.ubumonitor.webservice.WSFunctions;

/**
 * Función de moodle que devuelve los contenidos del curso.
 * @author Yi Peng Ji
 *
 */
public class CoreCourseGetContents extends ParametersCourseid {
	/**
	 * Do not return modules, return only the sections structure
	 */
	private boolean excludemodules;

	/**
	 * Do not return module contents (i.e: files inside a resource)
	 */
	private boolean excludecontents;

	/**
	 * Return stealth modules for students in a special section (with id -1)
	 */
	private boolean includestealthmodules;

	/**
	 * Return only this section
	 */
	private int sectionid;

	/**
	 * Return only this section with number (order)
	 */
	private int sectionnumber;

	/**
	 * Return only this module information (among the whole sections structure)
	 */
	private int cmid;

	/**
	 * Return only modules with this name "label, forum, etc..."
	 */
	private String modname;

	/**
	 * Return only the module with this id (to be used with modname)
	 */
	private int modid;

	private int index = 0;

	/**
	 * Constructor con el id del curso.
	 * @param courseid id del curso
	 */
	public CoreCourseGetContents(int courseid) {
		super(courseid);

	}

	private CoreCourseGetContents(Builder builder) {
		super(builder.courseid);

		excludemodules = builder.excludemodules;
		excludecontents = builder.excludecontents;
		includestealthmodules = builder.includestealthmodules;
		sectionid = builder.sectionid;
		sectionnumber = builder.sectionnumber;
		cmid = builder.cmid;
		modname = builder.modname;
		modid = builder.modid;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToUrlParameters() {

		if (modid > 0 && modname != null)
			throw new IllegalStateException("Hay que asignar modname si se asigna modid");

		super.appendToUrlParameters();
		appendIfPossible("excludemodules", excludemodules);
		appendIfPossible("excludecontents", excludecontents);
		appendIfPossible("includestealthmodules", includestealthmodules);
		appendIfPossible("sectionid", sectionid);
		appendIfPossible("sectionnumber", sectionnumber);
		appendIfPossible("cmid", cmid);
		appendIfPossible("modname", modname);
		appendIfPossible("modid", modid);

	}

	private void appendIfPossible(String name, String value) {
		if (value != null)
			appendToUrlOptions(index++, name, value);
	}

	private void appendIfPossible(String name, int value) {
		if (value != 0)
			appendToUrlOptions(index++, name, value);
	}

	private void appendIfPossible(String name, boolean value) {
		if (value)
			appendToUrlOptions(index++, name, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {

		return WSFunctions.CORE_COURSE_GET_CONTENTS;
	}

	/**
	 * Si se quiere excluir los modulos.
	 * @return true si se quiere excluir los modulos, false en caso contrario
	 */
	public boolean isExcludemodules() {
		return excludemodules;
	}

	/**
	 * Modifica si se quiere excluir los modulos.
	 * @param excludemodules true o false
	 */
	public void setExcludemodules(boolean excludemodules) {
		this.excludemodules = excludemodules;
	}

	/**
	 * Devuelve si se excluye los contenidos.
	 * @return true si son excluidos, false en caso contrario.
	 */
	public boolean isExcludecontents() {
		return excludecontents;
	}

	/**
	 * Modifica si se quiere excluir el contenido.
	 * @param excludecontents true o false.
	 */
	public void setExcludecontents(boolean excludecontents) {
		this.excludecontents = excludecontents;
	}

	/**
	 * Si se incluye modulos ocultos, con id de sección -1.
	 * @return true si se incluye, falso en caso contrario
	 */
	public boolean isIncludestealthmodules() {
		return includestealthmodules;
	}

	/**
	 * Modifica si se quiere cambiar los módulos ocultos.
	 * @param includestealthmodules true si se quiere incluir, falso en caso contrario
	 */
	public void setIncludestealthmodules(boolean includestealthmodules) {
		this.includestealthmodules = includestealthmodules;
	}

	/**
	 * Id de la sección del curso
	 * @return id de la sección del curso
	 */
	public int getSectionid() {
		return sectionid;
	}

	/**
	 * Modifica la id de sección del curso.
	 * @param sectionid id de la sección
	 */
	public void setSectionid(int sectionid) {
		this.sectionid = sectionid;
	}

	/**
	 * Devuelve el numero de seccion en el curso.
	 * @return el numero de seccion en el curso.
	 */
	public int getSectionnumber() {
		return sectionnumber;
	}

	/**
	 * Modifica el numero de seccion en el curso.
	 * @param sectionnumber el nuevo numero de seccion en el curso
	 */
	public void setSectionnumber(int sectionnumber) {
		this.sectionnumber = sectionnumber;
	}

	/**
	 * Devuelve la id del modulo del curso.
	 * @return id del modulo 
	 */
	public int getCmid() {
		return cmid;
	}

	/**
	 * Modifica el id del modulo del curso.
	 * @param cmid id del modulo del curso
	 */
	public void setCmid(int cmid) {
		this.cmid = cmid;
	}

	/**
	 * Devuelve el tipo de modulo (quiz, assignment etc.)
	 * @return tipo de modulo
	 */
	public String getModname() {
		return modname;
	}

	/**
	 * Modifica el tipo de modulo.
	 * @param modname tipo de modulo
	 */
	public void setModname(String modname) {
		this.modname = modname;
	}

	/**
	 * Devuelve el id de un modulo (no confundir con cmid que es un id general para todos los modulos).
	 * @return el id de un tipo de modulo
	 */
	public int getModid() {
		return modid;
	}

	/**
	 * Modifica el modulo id.
	 * @param modid id modulo
	 */
	public void setModid(int modid) {
		this.modid = modid;
	}
	/**
	 * Devuelve una nueva instancia para añadir parametros adicioneales.
	 * @param courseid id del curso
	 * @return builder
	 */
	public static Builder newBuilder(int courseid) {
		return new Builder(courseid);
	}

	/**
	 * Añade parametros adicionales a las request de Moodle.
	 * @author Yi Peng Ji
	 *
	 */
	public static class Builder {

		private int courseid;
		private boolean excludemodules;
		private boolean excludecontents;
		private boolean includestealthmodules;
		private int sectionid;
		private int sectionnumber;
		private int cmid;
		private String modname;
		private int modid;

		public Builder(int courseid) {
			this.courseid = courseid;
			
		}

		public Builder setExcludemodules(boolean excludemodules) {
			this.excludemodules = excludemodules;
			return this;
		}

		public Builder setExcludecontents(boolean excludecontents) {
			this.excludecontents = excludecontents;
			return this;
		}

		public Builder setIncludestealthmodules(boolean includestealthmodules) {
			this.includestealthmodules = includestealthmodules;
			return this;
		}

		public Builder setSectionid(int sectionid) {
			this.sectionid = sectionid;
			return this;
		}

		public Builder setSectionnumber(int sectionnumber) {
			this.sectionnumber = sectionnumber;
			return this;
		}

		public Builder setCmid(int cmid) {
			this.cmid = cmid;
			return this;
		}

		public Builder setModname(String modname) {
			this.modname = modname;
			return this;
		}

		public Builder setModid(int modid) {
			this.modid = modid;
			return this;
		}

		public CoreCourseGetContents build() {
			return new CoreCourseGetContents(this);
		}

	}

}
