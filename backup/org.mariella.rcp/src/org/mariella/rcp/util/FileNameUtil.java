package org.mariella.rcp.util;

public class FileNameUtil {
public final static Character[] FORBIDDEN_CHARS = {'*', '"', '/', '\\', '[', ']', ':', ';', '|', '=', ',', '?', '>', '<'};

public static String createValidFileName(String fileName) {
	StringBuffer result = new StringBuffer();
	char[] chs = fileName.toCharArray();
	for (char ch : chs) {
		if (isForbidden(ch))
			result.append("_" + (int)ch + "_");
		else
			result.append(ch);
	}
	return result.toString();
}

private static boolean isForbidden(char ch) {
	for (char f : FORBIDDEN_CHARS)
		if (f == ch) return true;
	return false;
}

}
