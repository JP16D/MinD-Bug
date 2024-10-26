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
	
	//some sort of parsing shenanigans
	public Pair<Class<?>, Prov<?>> parse(Class<?> type, String val) {
		var v = new Pair<Class<?>, Prov<?>>(type, null);
		//
		if (type == String.class) {
			return v.set(type, () -> val);
			//
		} else if (type.isPrimitive()) {
			//
			try {
				//
				var method = type.getMethod("valueOf", String.class);
				//
				if (!type.isInstance(method.getReturnType())) {
					//
					return v.set(type, () -> {
						try {
							return method.invoke(type, val);
						} catch (Exception e) {
							return null;
							//warn();
						}
					});
				}
			} catch (Exception e) {/*impossible*/}
		}
		//
		return v;
	}
	
	public void set(Pair<Class<?>, Prov<?>> pair) {
		set(pair.v1, pair.v2);
	}
	
	public void set(Class<?> type, Prov<?> val) {
		this.value = val;
		this.type = type;
		//
		if (!type.isPrimitive()) for (var f : type.getFields()) {
			//
			if (Modifier.isFinal(f.getModifiers()) || !f.getDeclaringClass().isPrimitive()) continue;
			//
			fields.put(f, () -> {
				try {
					return f.get(val.get());
				} catch (Exception ex) {
					return null;
				}
			});
		}
	}
	
	public Table table(String name) {
		if (type.isPrimitive()) {
			return Debugger.display(Color.maroon, name, new Table(t -> {
				t.field(value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					set(parse(type, txt));
					//
				}).center().pad(4f)
			}));
			//
		} else {
			return Debugger.table(Color.maroon, name, new Table(t -> {
				for (var k : fields.keys()) {
					t.add(Debugger.display(Color.darkGray, k.getName(), new Table(ft -> {
						ft.field(fields.get(k).get().toString(), Styles.defaultField, (String txt) -> {
							//
							fields.put(k, parse(k.getDeclaringClass(), txt).v2);
							//
						}).center().pad(4f);
					}))).row();
				}
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
		}
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