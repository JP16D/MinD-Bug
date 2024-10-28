package dbug.tool;

import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import dbug.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import java.lang.*;
import java.lang.reflect.*;

import static dbug.MDBug.*;
import static dbug.tool.Debugger.*;
import static dbug.util.ParseUtil.*;

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
					updateCaller();
				}).center().pad(4f).get();
				//
			}));
			//
		} else {
			return Debugger.table(Color.maroon, name, new Table(t -> {
				//
				boolean archived = false;
				//
				for (var k : fields.keys()) {
					t.add(Debugger.display(Color.darkGray, k.getName(), new Table(ft -> {
						ft.field((String) dv(k.getName() + "txt-init", () -> fields.get(k).get().toString()).get(), Styles.defaultField, (String txt) -> {
							//
							fields.put(k, parse(wrap(k.getType()), fields.get(k), txt).v2);
							//
							archived = true;
							dv(k.getName() + "txt", () -> txt);
						}).center().pad(4f);
						dv(k.getName() + "txt-post", () -> fields.get(k).get().toString());
					}))).pad(4f).row();
				}
				//
				t.button("Set", () -> {
					for (var k : fields.keys()) {
						try {
							k.set(value.get(), fields.get(k).get());
						} catch (Exception e) {
							//warn();
						}
						//
						updateCaller();
					}
				}).right().pad(2f);
				//
				if (archived) t.button(Icon.cancel, () -> {
					set(this.type, this.value);
					archived = false;
				}).right().pad(2f);
				//
			}));
		}
	}
}