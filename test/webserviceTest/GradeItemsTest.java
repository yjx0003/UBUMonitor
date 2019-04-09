package webserviceTest;

import java.io.Console;
import java.io.IOException;

import webservice.ParametersCourseidUseridGroupid.OptionalParameter;
import webservice.WebService;
import webservice.gradereport.GradereportUserGetGradeItems;

public class GradeItemsTest {
	//Se pued asignar el host y username en la declaracion. Si no se asigna hay que introducirlos en la consola.
	private static String host;
	private static String username;
	private static String password;

	public static void main(String[] args) throws IOException {
		Console consola = System.console();
		// En eclipse no funciona la Clase consola, hay que usar el scanner.
		if (consola != null) {
			if (host == null)
				host = consola.readLine("Host, hay que introducir con \"http://\" o \"https://\": ");
			if (username == null)
				username = consola.readLine("Nombre de usuario: ");
			if (password == null)
				password = new String(consola.readPassword("Contraseña: "));

		}
		WebService.initialize(host, username, password);
		int courseid = Integer.parseInt(consola.readLine("Id de curso: "));
		int idAlumno = Integer.parseInt(consola.readLine("Id de alumno: "));
		System.out.println("Descargamos las calificaciones de todos los usuarios del curso");
		WebService ws = new GradereportUserGetGradeItems(courseid, OptionalParameter.USERID, idAlumno);
		System.out.println("Descargamos las calificaciones de un usuario del curso");
		System.out.println(ws.getResponse());
	}
}
