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
		this.type = wrap(type);
		this.value = value;
		//
		if (!isWrapper(this.type)) return;
		//
		for (var field : type.getFields()) {
			map.put(field.getName(), new Writable(null));
		}
	}
	
	public void set(Object v) {
		this.value = value;
	}
	
	//table display
	public Table table(String name) {
		//
		if (isWrapper(type)) {
			return Debugger.display(Color.maroon, name, single(new Table()));
		} else {
			return Debugger.table(Color.maroon, name, multi(new Table()));
		}
	}
	
	private Table single(Table t) {
		var v = new Writable(value);
		//
		t.field(value.toString(), Styles.defaultField, (String txt) -> {
			//
			v.stored = parse(type, value, txt);
			//
		}).center().pad(4f);
		//
		this.value = v.stored;
		return t;
	}
	
	private Table multi(Table t) {
		//
		for (var k : map.keys()) try {
			
			var input = new Table();
			var f = type.getField(k);
			var w = map.get(k);
			//
			boolean stored = w.stored != null;
			var v = stored ? w.stored : f.get(value);
			//
			input.field(v.toString(), Styles.defaultField, (String txt) -> {
				//
				w.stored = parse(wrap(f.getType()), v, txt);
				//
			}).center().pad(4f);
			//
			t.add(Debugger.display(stored ? Color.green : Color.darkGray, f.name, input)).pad(4f).row();
		} catch (Exception e) {}
		//
		//apply changes 
		var set = t.button("Set", () -> {}).right().pad(2f).get();
		//
		if (set.isChecked()) for (var k : map.keys()) try {
			type.getField(k).set(value, map.get(k).stored);
		} catch (Exception e) {}
		//
		//revert changes
		var revert = t.button(Icon.cancel, () -> {}).right().pad(2f);
		//
		//update display
		if (set.isChecked() || revert.isChecked()) {
			for (var v : map.values()) v.stored = null;
			//
			t.clearChildren();
			multi(t);
		}
		//
		return t;
	}
	
	protected class Writable {
		Object stored; 
		
		Writable(Object value) {
			stored = value;
		}
	}
}