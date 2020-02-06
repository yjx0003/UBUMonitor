package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SubDataBase<E extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<Integer, E> map;
	private SerializableFunction<Integer, E> creatorFunction;
	

	public SubDataBase(SerializableFunction<Integer, E> creator) {
		map = new HashMap<>();
		this.creatorFunction= creator;
	}

	public Map<Integer, E> getMap() {
		return map;
	}

	public E getById(int id) {
		return map.computeIfAbsent(id, creatorFunction );
	}

	public boolean containsId(int id) {
		return map.containsKey(id);
	}

	public E putIfAbsent(int id, E element) {
		return map.putIfAbsent(id, element);
	}

	public void clear() {
		map.clear();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

}
