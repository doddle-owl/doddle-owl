/*
 * @(#)  2006/01/15
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;

/**
 * @author takeshi morita
 */
public class Document {

    private String lang;
    private File file;

    public Document(File f) {
        lang = "ja";
        file = f;
    }

    public Document(String l, File f) {
        lang = l;
        file = f;
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
}
