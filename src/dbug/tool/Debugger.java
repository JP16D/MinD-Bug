package dbug.tool;

import java.lang.annotation.*;
import java.lang.reflect.*;

import arc.struct.*;

import static  dbug.util.Identity.*;
import static dbug.util.ParseUtil.*;

public class Debugger {

    public static final OrderedMap<String, Viewable> entries = new OrderedMap<>();

    /**
     * guards a value from null errors by returning a generated dummy object in a null case
     */
	@SuppressWarnings("unchecked")
	public static <T> T guard(Class<T> type, Object value) {
		if (type == null || value != null) return (T) value;

		try {
			var wtype = wrap(type);
			if (isWrapper(wtype)) return (T) parse(wtype, value, "0");
			// 
			try {
				var constructor = type.getDeclaredConstructor();
				// 
				constructor.setAccessible(true);
				// 
				return constructor.newInstance();
			} catch (NoSuchMethodException e) {}
			// 
			return (T) dummy(type, 0, new Seq<>());
		} catch (
			IllegalAccessException 
			| IllegalArgumentException 
			| InstantiationException  
			| SecurityException 
			| InvocationTargetException 
			e
		) {
			return null; 
		}
	}

    /**
	 * registers a new debuggable object
	 * 
	 * @param identity a debug entry query string 
	 * @param value the debuggable object
	 * 
	 * @see dbug.util.ParseIdentity
	 */
    public static <T> T debug(String identity, T value) {
		var id = parse(identity);
		//
		return debug(id.name, id.writable, value, id.depth);
    }

	@SuppressWarnings("unchecked")
	private static <T> T debug(String name, boolean writable, T value, int depth) {
		var entry = entries.get(name, writable
			? Writable.entry(value.getClass(), name, value, depth)
			: Viewable.entry(value.getClass(), name, value, depth)
		);
		//
        if (!entries.containsKey(name)) entries.put(name, entry);
        //
		entry.set(value);
        return (T) (entry instanceof Writable e ? e.release() : entry.get());
	}

    //add a builder function (W.I.P.)
    public static void build(String name) {
        //new Builder();
    }

	@Retention(RetentionPolicy.RUNTIME)
    @Target({
		ElementType.FIELD,
		ElementType.METHOD,
		ElementType.PARAMETER,
		ElementType.LOCAL_VARIABLE
	})
    public @interface Guarded {

		Class<?> value() default void.class;

	}

    @Retention(RetentionPolicy.RUNTIME)
	@Target({
		ElementType.FIELD,
		ElementType.LOCAL_VARIABLE
	})
    public @interface Debugs {

        Debug[] value();

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({
		ElementType.FIELD,
		ElementType.LOCAL_VARIABLE
	})
    @Repeatable(Debugs.class)
    public @interface Debug {

        String value() default "";

    }

    /**
     * Automatically scans an object instance (or class for static fields) and
     * registers all @Debug annotated fields.
     */
    public static void register(Object target) {
        if (target == null) return;
        //
        var isClass = target instanceof Class<?>;
        var type = isClass ? (Class<?>) target : target.getClass();
        var instance = isClass ? null : target;

        for (Field field : type.getDeclaredFields()) {
			debugRegister(field, instance);
			guardRegister(field, instance);
        }
    }

	private static void guardRegister(Field field, Object instance) {
		if (field.isAnnotationPresent(Guarded.class)) try {
			var guard = field.getAnnotation(Guarded.class);
			var ftype = guard.value() != void.class ? guard.value() : field.getType();
			var value = field.get(instance);

			field.set(instance, guard(ftype, value));
		} catch (IllegalAccessException | IllegalArgumentException e) {}
	}

	private static void debugRegister(Field field, Object instance) {
		var debugs = field.getAnnotationsByType(Debug.class);
		//
		if (debugs.length > 0) {
			field.setAccessible(true);
			//
			if ((instance == null) == (Modifier.isStatic(field.getModifiers()))) try {
				var value = field.get(instance);
				//
				// Loop through each @Debug annotation found on this field
				for (var debug : debugs) {
					var id = parse(debug.value());
					var name = id.name.isEmpty() ? field.getName() : id.name;
					//
					value = debug(name, id.writable, value, id.depth);
					//
					if (id.writable && value != null && instance != null) {
						field.set(instance, value);
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {}
		}
	}

	private static Object dummy(Class<?> type, int depth, Seq<Class<?>> ancestry) {
		if (depth > 5 || ancestry.contains(type)) return null;
		// 
		ancestry.add(type);
		try {
			var constructors = Seq.with(type.getDeclaredConstructors());
			//
			if (constructors.size == 0) return null;
			// 
			// Sort constructors: fewest parameters first, then fewest complex object parameters
			constructors.sort((c1, c2) -> {
				int pn1 = c1.getParameterCount();
				int pn2 = c2.getParameterCount();
				//
				if (pn1 != pn2) {
					return Integer.compare(pn1, pn2);
				}
				//
				return Long.compare(
					Seq.with(c1.getParameterTypes()).count(p -> !isWrapper(wrap(p)) && p != String.class),
					Seq.with(c2.getParameterTypes()).count(p -> !isWrapper(wrap(p)) && p != String.class)
				);
			});
			// 
			for (var constructor : constructors) try {
				var ptypes = constructor.getParameterTypes();
				// 
				// Skip constructors that contain a type already in our ancestry stack
				var hasCircularDependency = false;
				for (var ptype : ptypes) {
					if (!isWrapper(wrap(ptype)) && ptype != String.class && ancestry.contains(ptype)) {
						hasCircularDependency = true;
						break;
					}
				}
				//
				if (hasCircularDependency) continue;
				// 
				constructor.setAccessible(true);
				var args = new Object[ptypes.length];
				//
				for (int i = 0; i < ptypes.length; i++) {
					var ptype = ptypes[i];
					var wtype = wrap(ptype);
					// 
					if (isWrapper(wtype) || ptype == String.class) args[i] = parse(wtype, null, "0");
					else args[i] = dummy(ptype, depth + 1, ancestry);
				}
				//
				return constructor.newInstance(args);
			} catch (
				IllegalAccessException
				| IllegalArgumentException
				| InstantiationException
				| InvocationTargetException 
				e
			) {}
			//
		} finally {
			ancestry.remove(type);
		}
		return null;
	}
}
