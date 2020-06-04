package es.ubu.lsi.ubumonitor.clustering.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tree<E> {

	private Node<E> root;

	public Tree(E root) {
		this.root = new Node<>(root, null);
	}

	public Node<E> getRoot() {
		return root;
	}

	@Override
	public String toString() {
		return root.toString();
	}

	public static class Node<E> {
		private E element;
		private Node<E> parent;
		private List<Node<E>> childrens = new ArrayList<>();
		private Map<String, String> info = new LinkedHashMap<>();

		private Node(E element, Node<E> parent) {
			this.element = element;
			this.parent = parent;
		}

		public Node<E> addChildren(E children) {
			Node<E> node = new Node<>(children, this);
			childrens.add(node);
			return node;
		}

		public E getValue() {
			return element;
		}

		public Node<E> getParentNode() {
			return parent;
		}

		public List<Node<E>> getChildrens() {
			return childrens;
		}

		public void putInfo(String key, String value) {
			info.put(key, value);
		}

		public String getInfo(String key) {
			return info.get(key);
		}

		@Override
		public String toString() {
			return element.toString();
		}
	}

}
