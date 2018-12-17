/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2015 Yamaguchi Laboratory, Keio University. All rights reserved. 
 * 
 * This file is part of DODDLE-OWL.
 * 
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.sourceforge.doddle_owl.data;

import net.sourceforge.doddle_owl.ui.InputDocumentSelectionPanel;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.swing.*;
import java.io.*;

/**
 * @author Takeshi Morita
 */
public class Document implements Comparable<Document> {

	private File file;
	private String lang;
	private String text;
	private String[] texts;

	public Document(File f) {
		lang = "ja";
		file = f;
		text = getTextString();
		texts = getSplitText();
	}

	public Document(String l, File f) {
		lang = l;
		file = f;
		text = getTextString();
		texts = getSplitText();
	}

	public Document(String l, File f, String t) {
		lang = l;
		file = f;
		text = t;
		texts = getSplitText();
	}

	private String[] getSplitText() {
		return text.split(InputDocumentSelectionPanel.PUNCTUATION_CHARS + "|\n");
	}

	private String getTextString() {
		if (!file.exists()) {
			return "";
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			String fileName = file.getName().toLowerCase();
			if (fileName.matches(".*.txt")) {
				return getTextString(new InputStreamReader(fis, "UTF-8"));
			} else if (fileName.matches(".*.pdf")) {
				PDFParser pdfParser = new PDFParser(new RandomAccessFile(file, "r"));
				pdfParser.parse();
				PDDocument pddoc = pdfParser.getPDDocument();
				PDFTextStripper stripper = new PDFTextStripper();
				String text = stripper.getText(pddoc);
				pddoc.close();
				return text;
			} else if (fileName.matches(".*.docx")) {
				XWPFDocument doc = new XWPFDocument(fis);
				return new XWPFWordExtractor(doc).getText();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	/**
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private String getTextString(Reader reader) throws IOException {
		BufferedReader bufReader = new BufferedReader(reader);
		StringWriter writer = new StringWriter();
		String line = "";
		while ((line = bufReader.readLine()) != null) {
			writer.write(line);
			writer.write(System.getProperty("line.separator"));
		}
		writer.close();
		reader.close();
		String text = writer.toString();
		return text;
	}

	private boolean isWindowsOS() {
		return UIManager.getSystemLookAndFeelClassName().equals(
				"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	}

	public String getText() {
		StringBuilder builder = new StringBuilder();
		for (String text : texts) {
			builder.append(text);
			builder.append("\n");
		}
		return builder.toString();
	}

	public String[] getTexts() {
		return texts;
	}

	public void resetText() {
		texts = getSplitText();
	}

	public void setText(String text) {
		this.text = text;
		texts = getSplitText();
	}

	public int getSize() {
		return texts.length;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String toString() {
		return "[" + lang + "]" + " " + file.getAbsolutePath();
	}

	@Override
	public int compareTo(Document d) {
		return file.getAbsoluteFile().compareTo(d.getFile());
	}
}
