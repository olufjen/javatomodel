package no.itpr.parser.model;

import java.io.BufferedReader;
import java.io.IOException;

public interface FileProcess {

	/**
	 * @author oluf
	 * A functional interface  passes behavior
	 * This is what lambda expressions are for.
	 * The behavior of this functional interface is: BufferedReader -> String
	 * The function is readFile and the signature is String fname(BufferedReader)
	 * This is a functional descriptor
	 */
	@FunctionalInterface
	public interface ReadFile {
		String readFile(BufferedReader b) throws IOException;
	}
}
