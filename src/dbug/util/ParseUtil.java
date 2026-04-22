package dbug.util;

import arc.struct.*;
import java.lang.*;
import java.lang.reflect.*;

public class ParseUtil {
	//confirm if class is a primitive wrapper
	public static boolean isWrapper(Class<?> type) {
		try {
			 var t = type.getField("TYPE");
			 return ((Class) t.get(type)).isPrimitive();
			//
		} catch (Exception e) {/*do nothing*/}
		//
		return false;
	}
	
	public static Class<?> wrap(Class<?> type) {
		var map = OrderedMap.of(
			boolean.class, Boolean.class,
			byte.class, Byte.class,
			char.class, Character.class,
			short.class, Short.class,
			int.class, Integer.class,
			long.class, Long.class,
			float.class, Float.class,
			double.class, Double.class,
			void.class, Void.class
		);
			
		for (var k : map.keys()) {
			if (type == k) return (Class) map.get(k);
		}
		//
		return type;
	}
	
	//some sort of parsing shenanigans
	public static Object parse(Class<?> type, Object fallback, String value) {
		//cancel parse when string type
		if (type == String.class) return value;
		//
		try {
			var wraptype= wrap(type);
			var parser = wraptype.getMethod("valueOf", String.class);
			//
			return parser.invoke(wraptype, value);
			//
		} catch (Exception e) {
			//warn();
			return fallback;
		}
	}
}