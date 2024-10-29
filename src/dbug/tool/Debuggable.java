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
	public void set(Pair<Class<?>, Object> pair) {
		set(pair.v1, pair.v2);
	}
	
	public void set(Class<?> type, Object value) {
		//
		this.value = value;
		this.type = type;
		//
		if (isWrapper(type)) return;
		//
		for (var field : type.getFields()) {
			var f = new WritableField(field);
			//
			if (!Modifier.isFinal(field.getModifiers())) {
				if (fields.size > 0 && fields.contains(i -> i.name.equals(f.name))) {
					fields.replace(i -> i.queued ? i : f);
					//
				} else fields.add(f);
			}
		}
		//
		return;
	}
	
	public void prioritize(Class<?> type, Object value) {
		if (priority) {
			priority = false;
			//
		} else set(type, value);
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
					Debugger.dv("prio", priority);
				}).center().pad(4f);
			}));
			//
		} else {
			return Debugger.table(Color.maroon, name, new Table(t -> {
				//
				for (var f : fields) {
					t.add(Debugger.display(Color.darkGray, f.name, new Table(input -> {
						var v = f.value;
						//
						input.field(v.toString(), Styles.defaultField, (String txt) -> {
							//
							f.queue(parse(wrap(f.field.getType()), v, txt).v2);
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