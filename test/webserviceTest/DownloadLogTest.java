package webserviceTest;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;

import controllers.ubulogs.DownloadLogController;

public class DownloadLogTest {

	static DownloadLogController log;
	static final String PATH = "./resultadosTest/";

	@BeforeClass
	public static void login() throws NumberFormatException, Exception {

		Console consola = System.console();
		String host;
		String username;
		String password;
		String idCourse;
		File dir = new File(PATH);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}

		// En eclipse no funciona la Clase consola, hay que usar el scanner.
		if (consola != null) {
			host = consola.readLine("Host, hay que introducir con \"http://\" o \"https://\": ");
			username = consola.readLine("Nombre de usuario: ");
			password = new String(consola.readPassword("Contraseña: "));
			idCourse = consola.readLine("Id del curso: ");
		} else {
			try (Scanner scanner = new Scanner(System.in)) {

				System.out.println("Host, hay que introducir con \"http://\" o \"https://\": ");
				host = scanner.nextLine();
				System.out.println("Nombre de usuario: ");
				username = scanner.nextLine();
				System.out.println("Contraseña: ");
				password = scanner.nextLine();
				System.out.println("Id del curso");
				idCourse = scanner.nextLine();
			}
		}
		
		String usertimezone = "99";
		log = new DownloadLogController(host, username, password, Integer.parseInt(idCourse), usertimezone);

	}

	@Test
	public void downloadOneDayTest() throws FileNotFoundException, IOException {
		System.out.println("Descargando un dia de log desde 15/02/2019 18:40");
		LocalDateTime fecha = LocalDateTime.of(2019, 2, 15, 18, 40);
		String stringLog = log.downloadLog(fecha);
		saveInFile(PATH + "OneDay", stringLog);
	}

	@Test
	public void downloadAllLogTest() throws FileNotFoundException, IOException {
		System.out.println("Descargando el log completo");
		String stringLog = log.downloadLog();
		saveInFile(PATH + "All", stringLog);

	}

	@Test
	public void downloadLogSince() throws FileNotFoundException, IOException {
		System.out.println("Descargando log desde 15/02/2019 18:40 hasta ahora");
		LocalDateTime fecha = LocalDateTime.of(2019, 2, 15, 18, 40);
		List<String> stringLog = log.downloadLog(fecha, LocalDateTime.now());
		saveInFile(PATH + "sinceToNow", String.join("\n", stringLog));
	}
	
	
	@Test
	public void downloadLog() {
		System.out.println("Descargando log desde 15/02/2019 18:40 hasta ahora");
		LocalDateTime fecha = LocalDateTime.of(2019, 2, 15, 18, 40);
		String stringLog = log.downloadLog(fecha);
		System.out.println(stringLog);
	}

	@Test
	public void downloadLogUsingTimezone() throws FileNotFoundException, IOException {
		System.out.println("Descargando un dia de log desde 15/02/2019 18:40 con zona horaria "+ZoneId.of("Asia/Tokyo").getId());
		ZonedDateTime fecha = ZonedDateTime.of(2019, 2, 15, 18, 40,0,0, ZoneId.of("Asia/Tokyo"));
		List<String> stringLog = log.downloadLog(fecha, ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Tokyo")));
		saveInFile(PATH + "sinceToNowUingTimezone", String.join("\n", stringLog));
		System.out.println();
	}

	public void saveInFile(String path, String stringLog) throws FileNotFoundException, IOException {
		try (FileOutputStream out = new FileOutputStream(path)) {
			out.write(stringLog.getBytes());
		}
	}
}
