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
	
	public void set(Object val) {
		value = val;
	}
	
	//table display
	public Table table(String name) {
		var table = new Table();
		//
		if (map.size > 0) {
			//
			for (var k : map.keys()) try {
				//
				var input = new Table();
				var f = type.getField(k);
				var w = map.get(k);
				var v = w.empty() ? f.get(value) : w.stored;
				//
				input.field(v.toString(), Styles.defaultField, (String txt) -> {
					//
					w.set(parse(wrap(f.getType()), v, txt));
					//
				}).center().pad(4f);
				//
				table.add(Debugger.display(w.empty() ? Color.darkGray : Color.green, f.getName(), input)).pad(4f).row();
			} catch (Exception e) {}
			//
			//apply changes 
			table.button("Set", () -> {
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
			table.button(Icon.cancel, () -> {
				for (var v : map.values()) v.set(null);
				//
			}).right().pad(2f).get();
			//
			return Debugger.table(Color.maroon, name, table);
		} else {
			table.field(value.toString(), Styles.defaultField, (String txt) -> {
				//
				set(parse(type, value, txt));
				//
			}).center().pad(4f);
			//
			return Debugger.display(Color.maroon, name, table);
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
		
		boolean empty() {
			return stored == null;
		}
	}
}