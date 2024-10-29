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
	public Seq<WritableField> fields = new Seq<>();
	//
	public Object value;
	public Class<?> type;
	//
	protected Seq<Boolean> queue = new Seq<>();
	protected boolean priority;
	
	public Debuggable(Pair<Class<?>, Object> pair) {
		set(pair);
	}
	
	public Debuggable(Class<?> type, Object val) {
		set(type, val);
	}
	
	//set up important values
	public Debuggable set(Pair<Class<?>, Object> pair) {
		return set(pair.v1, pair.v2);
	}
	
	public void set(Class<?> type, Object value) {
		//
		this.value = value;
		this.type = type;
		//
		if (isWrapper(type)) return;
		//
		for (var f : type.getFields()) {
			//
			if (!Modifier.isFinal(f.getModifiers())) fields.add(new WritableField(f));
		}
		//
		return;
	}
	
	public Debuggable prioritize(Debuggable d) {
		if (priority) {
			d.set(type, value);
			//
			priority = false;
		}
		//
		if (fields.size > 0) {
			for (var f : fields) {
				d.fields.replace(i -> (f.name == i.name) && f.queued ? f : i);
			}
		}
	}
	
	//table display
	public Table table(String name) {
		if (isWrapper(type)) {
			return Debugger.display(Color.maroon, name, new Table(t -> {
				t.field(value.toString(), Styles.defaultField, (String txt) -> {
					//
					set(parse(type, value, txt));
					//
					priority = true;
				}).center().pad(4f);
			}));
			//
		} else {
			return Debugger.table(Color.maroon, name, new Table(t -> {
				//
				for (var f : fields) {
					t.add(Debugger.display(Color.darkGray, f.field.getName(), new Table(input -> {
						var v = f.value;
						//
						input.field(v.toString(), Styles.defaultField, (String txt) -> {
							//
							f.queue(parse(wrap(f.getType()), v, txt).v2);
							//
						}).center().pad(4f);
						//
					}))).pad(4f).row();
				}
				//
				t.button("Set", () -> {
					for (var f : fields) f.set();
					//
					priority = true;
				}).right().pad(2f);
				//
				t.button(Icon.cancel, () -> {
					for (var f : fields) f.revert();
				}).right().pad(2f);
				//
			}));
		}
	}
	
	protected class WritableField {
		Field field;
		String name;
		Object value;
		//
		boolean queued;
		
		WritableField(Field field) {
			this.field = field;
			this.name = field.getName();
			//
			revert();
		}
		
		void queue(Object val) {
			value = val;
			queued = true;
		}
		
		void set() {
			try {
				field.set(Debuggable.this.value, value);
			} catch (Exception e) {
				//warn();
			}
			//
			queued = false;
		}
		
		void revert() {
			try {
				value = field.get(Debuggable.this.value);
			} catch (Exception e) {
				value = null;
			}
			//
			queued = false;
		}
	}
}