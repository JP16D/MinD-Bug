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
	public Class<?> type;
	
	public Debuggable(Class<?> type, Object value) {
		this.type = type;
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields()) {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Writable(null));
		}
	}
	
	//table display
	public Object call(String name, Object value, Table table) {
		//
		if (map.size > 0) {
			table.add(Debugger.table(Color.maroon, name, new Table(t -> {
				for (var k : map.keys()) try {
					//
					var input = new Table();
					var f = type.getField(k);
					var w = map.get(k);
					var v = v.empty() ? f.get(value) : w.stored;
					//
					input.field(v.toString(), Styles.defaultField, (String txt) -> {
						//
						w.set(parse(wrap(f.getType()), v, txt));
						//
					}).center().pad(4f);
					//
					t.add(Debugger.display(v.empty() ? Color.darkGray : Color.green, f.getName(), input)).pad(4f).row();
				} catch (Exception e) {}
				//
				//apply changes 
				t.button("Set", () -> {
					for (var k : map.keys()) try {
						var v = map.get(k);
						//
						type.getField(k).set(value, v.stored);
						v.set(null);
						//
					} catch (Exception e) {}
					//
				}).right().pad(2f);
				//
				//revert changes
				t.button(Icon.cancel, () -> {
					for (var v : map.values()) v.set(null);
					//
				}).right().pad(2f).get();
				//
			})));
		} else {
			table.add(Debugger.display(Color.maroon, name, new Table(t -> {
				t.field(value.toString(), Styles.defaultField, (String txt) -> {
					//
					value = parse(type, value, txt);
					//
				}).center().pad(4f);
				//
			})));
		}
		//
		return value;
	}
	
	protected class Writable {
		Object stored;
		
		Writable(Object value) {
			stored = value;
		}
		
		void set(Object v) {
			stored = v;
		}
		
		boolean empty() {
			return stored == null;
		}
	}
}