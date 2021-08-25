package org.isaqb.onlineexam.mockexam.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil {

	public static boolean arrayContains(Character[] array, char value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i]==value) return true;
		}
		return false;
	}

	public static List<Character> asList(char[] positions) {
		List<Character> list = new ArrayList<>();
		for(int i=0; positions!=null && i<positions.length; i++) {
			list.add(positions[i]);
		}
		return list;
	}

}
