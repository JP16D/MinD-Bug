package dbug.tool;

import java.lang.annotation.*;
import java.lang.reflect.*;

import arc.struct.*;

public class Debugger {
	public static final OrderedMap<String, Viewable> entries = new OrderedMap<>();
    
    /**
	 * returns a default value if main value is null to avoid null error crashes
	 */
	//(Temporary) I'm planning on adding one that automatically generates a dummy value 
	public static <T> T check(T val, T def) {
        return val != null ? val : def;
    }
    
    //add debugger (view-only)
    @SuppressWarnings("unchecked")
    public static <T> T dv(String name, T val) {
        if (!entries.containsKey(name)) {
            entries.put(name, new Viewable(val.getClass(), name, val));
        }
		//
        var entry = entries.get(name);
		//
        entry.set(val);
        return (T) entry.get();
    }
    
    //add debugger (writable)
    @SuppressWarnings("unchecked")
    public static <T> T dw(String name, T val) {
        if (!entries.containsKey(name)) {
            entries.put(name, new Writable(val.getClass(), name, val));
        }
		//
        var entry = (Writable) entries.get(name);
		//
        entry.set(val);
        return (T) entry.release();
    }
	
	//add a builder function (W.I.P.)
	public static void build(String name) {
		//new Builder();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Repeatable(Debuggables.class)
	public @interface Debuggable {
		String name() default "";
		boolean writable() default false;
	}

	@Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Debuggables {
        Debuggable[] value();
    }

	/**
     * Automatically scans an object instance (or class for static fields) 
     * and registers all @Debuggable annotated fields.
     */
	public static void register(Object target) {
        if (target == null) return;
		//
        var isClass = target instanceof Class<?>;
        var type = isClass ? (Class<?>) target : target.getClass();
        var instance = isClass ? null : target;

        for (Field field : type.getDeclaredFields()) {
            var debugs = field.getAnnotationsByType(Debuggable.class);
            //
            if (debugs.length > 0) {
                field.setAccessible(true);
				//
                if ((instance == null) == (Modifier.isStatic(field.getModifiers()))) try {
                    var val = field.get(instance);
					//
                    // Loop through each @Debuggable annotation found on this field
                    for (var debug : debugs) {
                        var name = debug.name().isEmpty() ? field.getName() : debug.name();
						//
                        if (debug.writable()) {
                            val = dw(name, val);
							//
                            if (val != null && instance != null) field.set(instance, val);
							//
                        } else dv(name, val);
                    }
                } catch (Exception e) {}
            }
        }
    }
}