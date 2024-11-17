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
	private Seq<WritableField> fields = new Seq<>();
	//
	public Object value;
	public Class<?> type;
	
	public Debuggable(Class<?> type, Object value) {
		this.type = wrap(type);
		this.value = value;
		//
		if (isWrapper(this.type)) return;
		//
		if (fields.size <= 0) for (var field : this.type.getFields()) {
			var entry = new WritableField(field);
			//
			if (!Modifier.isFinal(field.getModifiers())) fields.add(entry);
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
		boolean priority = false;
		//
		t.field(v.toString(), Styles.defaultField, (String txt) -> {
			//
			this.value = parse(type, v, txt);
			priority = true;
			//
		}).center().pad(4f);
		//
		if (!priority) this.value = v;
		return t;
	}
	
	private Table multi(Table t, Object src) {
		//
		for (var f : fields) {
			boolean stored = f.stored != null;
			var v = stored ? f.stored : f.value(src);
			//
			t.add(Debugger.display(stored ? Color.green : Color.darkGray, f.name, new Table(input -> {
				//
				input.field(v.toString(), Styles.defaultField, (String txt) -> {
					//
					f.stored = parse(wrap(f.field.getType()), v, txt);
					//
				}).center().pad(4f);
				//
			}))).pad(4f).row();
		}
		//
		//apply changes 
		t.button("Set", () -> {
			for (var f : fields) f.set(src);
			//
			t.clearChildren();
			panel(t, src);
		}).right().pad(2f);
		//
		//revert changes
		t.button(Icon.cancel, () -> {
			for (var f : fields) f.revert();
			//
			t.clearChildren();
			panel(t, src);
		}).right().pad(2f);
		//
		this.value = src;
		return t;
	}
	
	protected class WritableField {
		Field field;
		String name;
		//
		Object stored;
		
		WritableField(Field field) {
			this.field = field;
			this.name = field.getName();
		}
		
		void set(Object src) {
			try {
				field.set(src, stored);
			} catch (Exception e) {
				//warn();
			}
			//
			revert();
		}
		
		void revert() {
			stored = null;
		}
		
		Object value(Object src) {
			try {
				return field.get(src);
			} catch (Exception e) {
				return null;
			}
		}
	}
}