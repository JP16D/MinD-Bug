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

import static dbug.ui.DebugField.*;
import static dbug.ui.MainPanel.*;
import static dbug.util.ParseUtil.*;

public class Modifiable {
	protected OrderedMap<String, Writable> map = new OrderedMap<>();
	//
	protected boolean priority;
	//
	public Object value;
	public Class<?> type;
	
	public Modifiable(Class<?> type, Object value) {
		this.type = type;
		this.value = value;
		//
		if (isObject()) for (var field : type.getFields()) {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Writable(null));
		}
	}
	
	//table display
	public Table call(String name, Object value) {
		//
		if (map.size > 0) {
			return mdisplay(Color.maroon, type, name, new Table(t -> {
				for (var k : map.keys()) try {
					//
					var input = new Table();
					var f = type.getField(k);
					var w = map.get(k);
					//
					boolean empty = w.stored == null || w.stored == f.get(value);
					var v = empty ? f.get(value) : w.stored;
					//
					input.field(v.toString(), Styles.defaultField, (String txt) -> {
						//
						w.set(parse(wrap(f.getType()), v, txt));
						//
					}).center().pad(4f);
					//
					if (!empty) input.add(f.get(value).toString()).center().pad(4f);
					
					t.add(display(empty ? Color.darkGray : Color.green, f.getType(), f.getName(), input)).grow().row();
				} catch (Exception e) {}
				//
				//apply changes 
				t.button("Set", () -> {
					for (var k : map.keys()) try {
						var v = map.get(k);
						//
						type.getField(k).set(this.value, v.stored);
						v.set(null);
						//
						priority = true;
					} catch (Exception e) {}
					//
					update();
				}).right().pad(2f);
				//
				//revert changes
				t.button(Icon.cancel, () -> {
					for (var v : map.values()) v.set(null);
					//
					update();
				}).right().pad(2f).get();
			}));
		} else {
			return new DebugField(type, value, (String txt) -> {
					//
				this.value = parse(type, value, txt);
					//
				priority = true;
				update();
			});
		}
	}
	
	public boolean isObject() {
		return !(isWrapper(type) || type.isPrimitive());
	}
	
	protected class Writable {
		Object stored;
		
		Writable(Object value) {
			stored = value;
		}
		
		void set(Object v) {
			stored = v;
		}
	}
}