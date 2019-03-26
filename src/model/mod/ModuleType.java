package model.mod;

import java.util.HashMap;
import java.util.Map;

public enum ModuleType {

	ASSIGNMENT("assign") {
		@Override
		public Module create() {
			return new Assignment();
		}
	},
	CHAT("chat") {
		@Override
		protected Module create() {
			return new Chat();
		}
	},
	CHOICE("choice") {
		@Override
		public Module create() {
			return new Choice();
		}
	},
	DATABASE("data") {
		@Override
		protected Module create() {
			return new Database();
		}
	},
	EXTERNAL_TOOL("lti") {
		@Override
		protected Module create() {
			return new ExternalTool();
		}
	},
	FEEDBACK("feedback") {
		@Override
		protected Module create() {
			return new Feedback();
		}
	},
	FILE("resource") {
		@Override
		protected Module create() {
			return new File();
		}
	},
	FOLDER("folder") {
		@Override
		public Module create() {
			return new Folder();
		}
	},
	FORUM("forum") {
		@Override
		public Module create() {
			return new Forum();
		}
	},

	IMS_PACKAGE("imscp") {
		@Override
		protected Module create() {
			return new IMSContentPackage();
		}
	},
	GLOSSARY("glossary") {
		@Override
		protected Module create() {
			return new Glossary();
		}
	},
	LABEL("label") {
		@Override
		public Module create() {
			return new Label();
		}
	},
	LESSON("lesson") {
		@Override
		public Module create() {
			return new Lesson();
		}
	},
	MODULE("module") {
		@Override
		public Module create() {
			return new Module();
		}
	},
	PAGE("page") {
		@Override
		public Module create() {
			return new Page();
		}
	},
	QUIZ("quiz") {
		@Override
		public Module create() {
			return new Quiz();
		}
	},
	SCORM_PACKAGE("scorm") {
		@Override
		protected Module create() {
			return new SCORMPackage();
		}
	},
	SURVEY("survey") {
		@Override
		protected Module create() {
			return new Survey();
		}
	},
	URL("url") {
		@Override
		public Module create() {
			return new URL();
		}
	},
	WIKI("wiki") {
		@Override
		protected Module create() {
			return new Wiki();
		}
	},
	WORKSHOP("workshop") {
		@Override
		public Module create() {
			return new Workshop();
		}
	};

	private String modname;
	private static Map<String, ModuleType> modTypes;
	static {
		modTypes = new HashMap<String, ModuleType>();
		for (ModuleType mod : ModuleType.values()) {
			modTypes.put(mod.modname, mod);
		}
	}

	ModuleType(String modname) {
		this.modname = modname;
	}

	protected abstract Module create();

	public static ModuleType get(String modname) {
		return modTypes.get(modname);
	}

	public String getModName() {
		return modname;
	}

	public static Module createInstance(String modname) {
		return modTypes.getOrDefault(modname, MODULE).create();
	}
}
