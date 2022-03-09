package es.ubu.lsi.ubumonitor.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.BiFunction;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class FileUtil {
	
	public static final ExtensionFilter EXCEL = new ExtensionFilter("Excel 2013-2016 (*.xlsx)", "*.xlsx");
	public static final ExtensionFilter WORD = new ExtensionFilter("Word (*.docx)", "*.docx");
	public static final ExtensionFilter PNG = new ExtensionFilter("Portable Network Graphics (*.png)", "*.png");
	public static final ExtensionFilter CSV = new ExtensionFilter("Comma-separated values (*.csv)", "*.csv");
	public static final ExtensionFilter JSON = new ExtensionFilter("JavaScript Object Notation (*.json)", "*.json");
	public static final ExtensionFilter ALL = new ExtensionFilter("All type of files (*)", "*");
	public static final ExtensionFilter XLS = new ExtensionFilter("Excel (*.xls)", "*.xls");
	

	public enum FileChooserType {
		OPEN(FileChooser::showOpenDialog), SAVE(FileChooser::showSaveDialog);

		private BiFunction<FileChooser, Window, File> function;

		private FileChooserType(BiFunction<FileChooser, Window, File> function) {
			this.function = function;
		}

		public File getFile(FileChooser fileChooser, Window window) {
			return function.apply(fileChooser, window);
		}
	}

	@FunctionalInterface
	public interface ThrowingConsumer<T, E extends IOException> {
		void accept(T t) throws E;
		
	}

	private FileUtil() {
		throw new UnsupportedOperationException();
	}
	
	public static void exportFile(Path source, Path destDir, Path dest) throws IOException {
	
			Files.createDirectories(destDir);
			Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
			
	
	}
}
