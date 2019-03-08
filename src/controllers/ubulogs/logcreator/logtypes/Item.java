package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.Log;
/**
 * Item created with ID .
 * Item with ID  deleted.
 * @author Yi Peng Ji
 *
 */
public class Item extends ReferencesLog {
	/**
	 * static Singleton instance.
	 */
	private static Item instance;

	/**
	 * Private constructor for singleton.
	 */
	private Item() {
	}

	/**
	 * Return a singleton instance of Item.
	 */
	public static Item getInstance() {
		if (instance == null) {
			instance = new Item();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub

	}

}
