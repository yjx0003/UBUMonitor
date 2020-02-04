package model;

import java.io.Serializable;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class SubDataBase<E extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private TIntObjectMap<E> map;
	private SerializableFunction<Integer, E> creatorFunction;
	

	public SubDataBase(SerializableFunction<Integer, E> creator) {
		map = new TIntObjectHashMap<>();
		this.creatorFunction= creator;
	}

	public TIntObjectMap<E> getMap() {
		return map;
	}

	public E getById(int id) {
		if(map.containsKey(id)) {
			return map.get(id);
		}
		E value = creatorFunction.apply(id);
		map.put(id, value);
		return value;
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
