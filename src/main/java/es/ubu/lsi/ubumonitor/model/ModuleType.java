package es.ubu.lsi.ubumonitor.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeración de los diferentes tipos de modulos del curso y con metodos de
 * creacion de instancias de ese tipo segun u codigo nemotecnico.
 * 
 * @author Yi Peng Ji
 *
 */
public enum ModuleType {

	ASSIGNMENT("assign"),
	BOOK("book"),
	CATEGORY("category"),
	CHAT("chat"),
	CHOICE("choice"),
	DATABASE("data"),
	EXTERNAL_TOOL("lti"),
	FEEDBACK("feedback"),
	FILE("resource"),
	FOLDER("folder"),
	FORUM("forum"),
	KALTURA_MEDIA_ASSIGNMENT("kalvidassign"),
	KALTURA_VIDEO_RESOURCE("kalvidres"),
	GLOSSARY("glossary"),
	GUIA_DOCENTE("guiadocente"),
	H5P("h5pactivity"),
	HOT_POT("hotpot"),
	IMS_PACKAGE("imscp"),
	JOURNAL("journal"),
	LABEL("label"),
	LESSON("lesson"),
	MANUAL_ITEM("manual"),
	MODULE("module"),
	PAGE("page"),
	QUIZ("quiz"),
	SCORM_PACKAGE("scorm"),
	SURVEY("survey"),
	TAB_DISPLAY("tab"),
	TURNITIN_TOOL_2("turnitintooltwo"),
	URL("url"),
	WIKI("wiki"),
	WORKSHOP("workshop"),
	DUMMY("Not avaible");

	private String modname;
	private static Map<String, ModuleType> modTypes;
	static {
		modTypes = new HashMap<>();
		for (ModuleType mod : ModuleType.values()) {
			modTypes.put(mod.modname, mod);
		}
	}

	ModuleType(String modname) {
		this.modname = modname;
	}


	public static ModuleType get(String modname) {
		return modTypes.getOrDefault(modname, MODULE);
	}

	public String getModName() {
		return modname;
	}



}
