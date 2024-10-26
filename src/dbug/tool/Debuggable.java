package dbug.tool;

import arc.func.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
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
		
		if (type == String.class) {
			return new Pair(type, () -> val);
			//
		} else if (type.isPrimitive()) {
			var v = () -> null;
			//
			try {
				//
				var method = type.getMethod("valueOf", String.class);
				//
				if (!type.isInstance(method.getReturnType())) {
					v = () -> {
						try {
							return method.invoke(type, val);
						} catch (Exception e) {
							return null;
							//warn();
						}
					};
				}
			} catch (Exception e) {/*impossible*/}
			//
			return new Pair(type, v);
		}
	}
	
	public void set(Pair<Class<?>, Prov<?>> pair) {
		set(pair.v1, pair.v2);
	}
	
	public void set(Class<?> type, Prov<?> val) {
		this.value = val;
		this.type = type;
		//
		var fields = type.getFields();
		//
		if (!type.isPrimitive()) for (var f : fields) {
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
	
	public void field(Table t) {
		if (type.isPrimitive()) {
			t.field(value.get().toString(), Styles.defaultField, (String txt) -> {
				//
				set(parse(type, txt));
				//
			}).center().pad(4f);
			//
		} else {
			Debugger.display(Color.darkGray, k, new Table(ft -> {
				for (var k : fields.keys()) {
					ft.field(fields.get(k).get().toString(), Styles.defaultField, (String txt) -> {
						//
						fields.put(k, parse(k.getDeclaringClass(), txt).v2);
						//
					}).center().pad(4f).row();
				}
			}));
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
			v1 = val1;
			v2 = val2;
		}
	}
}