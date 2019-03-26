package model;

import java.util.HashMap;
import java.util.Map;

public enum ItemType {
	
	MOD("mod"),
	MANUAL("manual"), 
	CATEGORY("category");

	private String name;
	private static Map<String, ItemType> map;

	private ItemType(String name) {
		this.name = name;
	}

	static {
		map = new HashMap<String, ItemType>();
		for (ItemType itemType : ItemType.values()) {
			map.put(itemType.name, itemType);
		}
	}

	public static ItemType get(String name) {
		return map.getOrDefault(name, CATEGORY);
	}
	
	public String getItemTypeName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
