package es.ubu.lsi.ubumonitor.controllers;

import java.io.IOException;
import java.io.Writer;

public interface Exportable {
	public void exportCSV(String path) throws IOException;
	public default void exportCSVDesglosed(String path) throws IOException{}
	public Writer getWritter(String path) throws IOException;
}
