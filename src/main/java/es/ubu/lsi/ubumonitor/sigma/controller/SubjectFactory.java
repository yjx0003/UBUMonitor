package es.ubu.lsi.ubumonitor.sigma.controller;

import java.util.HashMap;

import es.ubu.lsi.ubumonitor.sigma.parser.model.Subject;

public class SubjectFactory {

	private static SubjectFactory instance;
	private HashMap<String, Subject> map = new HashMap<>();

	private SubjectFactory() {
		// private
	}

	public static SubjectFactory getInstance() {
		if (instance == null)
			instance = new SubjectFactory();
		return instance;
	}
	
	public Subject getSubject(String code, String name, String type) {
		return map.computeIfAbsent(code, k-> new Subject(code, name, type));
	}
	
}
