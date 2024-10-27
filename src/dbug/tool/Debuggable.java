package dbug.tool;

import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ui.*;
import java.lang.reflect.*;

import java.lang.*;

public class Debuggable {
	public OrderedMap<Field, Prov<?>> fields = new OrderedMap<>();
	//
	public Prov<?> value;
	public Class<?> type;
	
	public Debuggable(Pair<Class<?>, Prov<?>> pair) {
		set(pair);
	}
	
	public Debuggable(Class<?> type, Prov<?> val) {
		set(type, val);
	}
	
	//set up important values
	public void set(Pair<Class<?>, Prov<?>> pair) {
		set(pair.v1, pair.v2);
	}
	
	public void set(Class<?> type, Prov<?> value) {
		this.value = value;
		this.type = type;
		//
		if (isWrapper(type)) return;
		//
		for (var f : type.getFields()) {
			//
			if (Modifier.isFinal(f.getModifiers())) continue;
			//
			fields.put(f, () -> {
				try {
					return f.get(value.get());
				} catch (Exception e) {
					return null;
				}
			});
		}
	}
	
	//table display
	public Table table(String name) {
		if (isWrapper(type)) {
			return Debugger.display(Color.maroon, name, new Table(t -> {
				t.field(value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					set(parse(type, value, txt));
					//
				}).center().pad(4f);
			}));
			//
		} else {
			return Debugger.table(Color.maroon, name, new Table(t -> {
				for (var k : fields.keys()) {
					t.add(Debugger.display(Color.darkGray, k.getName(), new Table(ft -> {
						ft.field(fields.get(k).get().toString(), Styles.defaultField, (String txt) -> {
							//
							fields.put(k, parse(k.getType(), fields.get(k), txt).v2);
							//
						}).center().pad(4f);
					}))).row();
				}
				//
				t.button("Set", () -> {
				for (var k : fields.keys()) {
					try {
						k.set(value.get(), fields.get(k).get());
					} catch (Exception e) {
						//warn();
					}
				}
				}).right().pad(2f);
			}));
		}
	}
	
	//confirm if class is a primitive wrapper
	public static boolean isWrapper(Class<?> type) {
		try {
			 var t = type.getField("TYPE");
			 return ((Class) t.get(type)).isPrimitive();
			//
		} catch (Exception e) {/*do nothing*/} finally { return false;
		}
	}
	
	//some sort of parsing shenanigans
	public static Pair<Class<?>, Prov<?>> parse(Class<?> type, Prov<?> def, String val) {
		var v = new Pair<Class<?>, Prov<?>>(type, def);
		//
		if (type == String.class) {
			v.set(type, () -> val);
			//
		} else (isWrapper(type)) {
			//
			try {
				//
				var method = type.getMethod("valueOf", String.class);
				//
				if (!type.isInstance(method.getReturnType())) {
					//
					v.set(type, () -> {
						try {
							return method.invoke(type, val);
						} catch (Exception e) {
							return def.get();
							//warn();
						}
					});
				}
			} catch (Exception e) {/*impossible*/}
		}
		//
		return v;
	}
	
	//Object pairing class, stores a single pair of value without needing an object map
	public class Pair<V1, V2>  {
		public V1 v1;
		public V2 v2;
		//
		public Pair(V1 val1, V2 val2) {
			set(val1, val2);
		}
		
		public Pair set(V1 val1, V2 val2) {
			v1 = val1;
			v2 = val2;
			//
			return this;
		}
	}
}