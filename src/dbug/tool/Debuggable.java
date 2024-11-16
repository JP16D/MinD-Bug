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
	//
	protected Seq<Boolean> queue = new Seq<>();
	
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
	
	public void set(Object value) {
		this.value = value;
	}
	
	//table display
	public Table table(String name) {
		var tab = new Table();
		//
		if (isWrapper(type)) {
			return Debugger.display(Color.maroon, name, field(tab));
		} else {
			return Debugger.table(Color.maroon, name, panel(tab));
		}
		dv("table-value", value);
	}
	
	private Table field(Table t) {
		t.field(value.toString(), Styles.defaultField, (String txt) -> {
			//
			set(dv("parsed", parse(type, value, txt)));
			//;
		}).center().pad(4f);
		dv("post-parsed", value);
		//
		return t;
	}
	
	private Table panel(Table t) {
		//
		for (var f : fields) {
			boolean stored = f.stored != null;
			var v = stored ? f.stored : f.value();
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
			for (var f : fields) f.set();
			//
			t.clearChildren();
			panel(t);
		}).right().pad(2f);
		//
		//revert changes
		t.button(Icon.cancel, () -> {
			for (var f : fields) f.revert();
			//
			t.clearChildren();
			panel(t);
		}).right().pad(2f);
		//
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
		
		void set() {
			try {
				field.set(value, stored);
			} catch (Exception e) {
				//warn();
			}
			//
			revert();
		}
		
		void revert() {
			stored = null;
		}
		
		Object value() {
			try {
				return field.get(value);
			} catch (Exception e) {
				return null;
			}
		}
	}
}