package es.ubu.lsi.ubumonitor.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum Charsets {
	
 	ISO_8859_1(StandardCharsets.ISO_8859_1),
 	US_ASCII(StandardCharsets.US_ASCII),
 	UTF_16(StandardCharsets.UTF_16),
 	UTF_16LE(StandardCharsets.UTF_16LE),
 	UTF_16BE(StandardCharsets.UTF_16BE),
 	UTF_8(StandardCharsets.UTF_8);
	
	
	private Charset charset;
	private Charsets(Charset charset) {
		this.charset = charset;
	}
	
	public Charset get() {
		return charset;
	}
	
	@Override
	public String toString() {
		return charset.toString();
	}
}
