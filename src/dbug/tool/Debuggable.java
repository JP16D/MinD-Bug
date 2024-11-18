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
		this.type = wrap(type);
		this.value = value;
		//
		if (isWrapper(this.type)) return;
		//
		for (var field : type.getFields()) {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Writable(null));
		}
	}
	
	//table display
	public Table table(String name, Object value) {
		//
		if (isWrapper(type)) {
			return Debugger.display(Color.maroon, name, single(new Table(), value));
		} else {
			return Debugger.table(Color.maroon, name, multi(new Table(), value));
		}
	}
	
	private Table single(Table t, Object v) {
		//
		t.field(v.toString(), Styles.defaultField, (String txt) -> {
			//
			value = parse(type, value, txt);
			//
			priority = true;
		}).center().pad(4f);
		//
		return t;
	}
	
	private Table multi(Table t, Object src) {
		//
		for (var k : map.keys()) try {
			
			var input = new Table();
			var f = type.getField(k);
			var w = map.get(k);
			var v = w.empty() ? f.get(src) : w.stored;
			//
			input.field(v.toString(), Styles.defaultField, (String txt) -> {
				//
				w.set(parse(wrap(f.getType()), v, txt));
				//
			}).center().pad(4f);
			//
			t.add(Debugger.display(w.empty() ? Color.darkGray : Color.green, f.getName(), input)).pad(4f).row();
		} catch (Exception e) {}
		//
		//apply changes 
		t.button("Set", () -> {
			for (var k : map.keys()) try {
				type.getField(k).set(value, map.get(k).stored);
				//
			} catch (Exception e) {}
			//
			priority = true;
			//
			reset(t);
		}).right().pad(2f);
		//
		//revert changes
		t.button(Icon.cancel, () -> {
			reset(t);
		}).right().pad(2f).get();
		//
		return t;
	}
	
	private void reset(Table t) {
		for (var v : map.values()) v.set(null);
		//
		t.clearChildren();
		//
		multi(t);
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