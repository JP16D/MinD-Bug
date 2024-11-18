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
	protected boolean priority =;
	//
	public Object temp;
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
	
	public void set(Object val) {
		value = val;
	}
	
	//table display
	public Table table(String name) {
		if (temp != null) {
			value = temp;
			temp = null;
		}
		//
		if (isWrapper(type)) {
			return Debugger.display(Color.maroon, name, single(new Table()));
		} else {
			return Debugger.table(Color.maroon, name, multi(new Table()));
		}
	}
	
	private Table single(Table t) {
		//
		t.field(value.toString(), Styles.defaultField, (String txt) -> {
			//
			temp = parse(type, value, txt);
			//
		}).center().pad(4f);
		//
		return t;
	}
	
	private Table multi(Table t) {
		//
		for (var k : map.keys()) try {
			
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
			t.add(Debugger.display(w.empty() ? Color.darkGray : Color.green, f.getName(), input)).pad(4f).row();
		} catch (Exception e) {}
		//
		//apply changes 
		t.button("Set", () -> {
			temp = value;
			//
			for (var k : map.keys()) try {
				type.getField(k).set(temp, map.get(k).stored);
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
		for (var v : map.values()) if (!v.empty()) v.set(null);
		//
		t.clearChildren();
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