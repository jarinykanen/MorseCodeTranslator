package fi.jari.morsecodetranslator;

import java.util.List;

public class Alphabets {
	private String id;
	private String name;
	private String description;

	private List<CodeMapAttrs> alphabet;
	private List<CodeMapAttrs> digit;
	private List<CodeMapAttrs> punctuation_mark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<CodeMapAttrs> getalphabet() {
		return alphabet;
	}

	public void setalphabet(List<CodeMapAttrs> alphabet) {
		this.alphabet = alphabet;
	}

	public List<CodeMapAttrs> getDigit() {
		return digit;
	}

	public void setDigit(List<CodeMapAttrs> digit) {
		this.digit = digit;
	}

	public List<CodeMapAttrs> getPunctuation_mark() {
		return punctuation_mark;
	}

	public void setPunctuation_mark(List<CodeMapAttrs> punctuation_mark) {
		this.punctuation_mark = punctuation_mark;
	}
}
