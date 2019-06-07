package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * Item created with ID .
 * Item with ID  deleted.
 * @author Yi Peng Ji
 *
 */
public class Item extends ReferencesLog {

	/**
	 * Instacia única de la clase Item.
	 */
	private static Item instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private Item() {
	}

	/**
	 * Devuelve la instancia única de Item.
	 * @return instancia singleton
	 */
	public static Item getInstance() {
		if (instance == null) {
			instance = new Item();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		//item id

	}

}
