package es.ubu.lsi.ubumonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import es.ubu.lsi.ubumonitor.view.chart.enrollment.EnrollmentBar;

public class UnitTest {

	@Test
	public void enrolledYear() {
		enrolledYear(9, 7, null, null);
		enrolledYear(9, 7, ZonedDateTime.of(2021, 10, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/10/2021
		enrolledYear(9, 7, ZonedDateTime.of(2021, 8, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/08/2021
		enrolledYear(9, 7, ZonedDateTime.of(2021, 7, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2020); // 03/07/2021
		enrolledYear(9, 7, ZonedDateTime.of(2020, 9, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2020); // 03/09/2020
		
		
		enrolledYear(3, 9, ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/01/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 3, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/03/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 8, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/08/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 9, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/09/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 10, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2022); // 03/10/2021
		
	}
	
	private void enrolledYear(int startMonth, int endMonth, Instant firstAccess, Integer expectedYear) {
		Integer year = EnrollmentBar.getYear(startMonth, endMonth, firstAccess);
		assertEquals(expectedYear, year);
	}
}

	

