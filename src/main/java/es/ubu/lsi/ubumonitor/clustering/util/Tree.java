package es.ubu.lsi.ubumonitor.clustering.util;

import java.util.ArrayList;
import java.util.List;

public class Tree<E> {

	private Node<E> root;

	public Tree(E root) {
		this.root = new Node<>(root);
	}

	public static class Node<E> {
		private E element;
		private Node<E> parent;
		private List<Node<E>> childrens;

		private Node(E element) {
			this.element = element;
			childrens = new ArrayList<>();
		}

		public void addChildren(E children) {
			childrens.add(new Node<>(children));
		}
	}

}
