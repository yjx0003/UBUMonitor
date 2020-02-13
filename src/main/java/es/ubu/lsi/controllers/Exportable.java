package es.ubu.lsi.controllers;

import java.io.IOException;

public interface Exportable {
	public void exportCSV(String path) throws IOException;
	public default void exportCSVDesglosed(String path) throws IOException{}
}
