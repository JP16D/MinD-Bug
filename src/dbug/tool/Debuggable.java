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

import static dbug.tool.Debugger.*;
import static dbug.util.ParseUtil.*;

public class Debuggable {
	protected OrderedMap<String, Writable> map = new OrderedMap<>();
	//
	protected boolean priority;
	//
	public Object value;
	public Class<?> type;
	
	public Debuggable(Class<?> type, Object value) {
		this.type = type;
		this.value = value;
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields()) {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Writable(null));
		}
	}
	
	//table display
	public Table call(String name, Object value) {
		//
		if (map.size > 0) {
			return Debugger.table(Color.maroon, name, new Table(t -> {
				for (var k : map.keys()) try {
					//
					var input = new Table();
					var f = type.getField(k);
					var w = map.get(k);
					//
					if (w.stored == null) w.stored = f.get(value);
					//
					boolean empty = w.stored == f.get(value);
					var v = w.stored;
					//
					input.field(v.toString(), Styles.defaultField, (String txt) -> {
						//
						w.set(parse(wrap(f.getType()), v, txt));
						//
					}).center().pad(4f);
					//
					if (!empty) input.add(f.get(value).toString()).center().pad(4f);
					
					t.add(Debugger.display(empty ? Color.darkGray : Color.green, type, f.getName(), input)).grow().row();
				} catch (Exception e) {}
				//
				//apply changes 
				t.button("Set", () -> {
					for (var k : map.keys()) try {
						//
						type.getField(k).set(this.value, map.get(k).stored);
						//
						priority = true;
					} catch (Exception e) {}
					//
				}).right().pad(2f);
				//
				//revert changes
				t.button(Icon.cancel, () -> {
					for (var v : map.values()) v.set(null);
					//
				}).right().pad(2f).get();
			}));
		} else {
			return Debugger.display(Color.maroon, type, name, new Table(t -> {
				t.field(value.toString(), Styles.defaultField, (String txt) -> {
					//
					this.value = parse(type, value, txt);
					//
					priority = true;
				}).center().pad(4f);
			}));
		}
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