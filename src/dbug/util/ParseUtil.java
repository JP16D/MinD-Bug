package dbug.util;

import arc.func.*;
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
	public static Pair<Class<?>, Object> parse(Class<?> type, Object def, String val) {
		var v = new Pair<Class<?>, Object>(type, def);
		//
		if (type == String.class) {
			v.set(type, val);
			//
		} else {
			//
			try {
				//
				var method = type.getMethod("valueOf", String.class);
				v.set(type, ((Prov) () -> {
					try {
						return method.invoke(type, val);
					} catch (Exception e) {
						return def;
						//warn();
					}
				}).get());
			} catch (Exception e) {/*impossible*/}
		}
		//
		return v;
	}
}