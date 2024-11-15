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
	
	public Debuggable(Class<?> type, Object val) {
		set(type, val);
	}
	
	public void set(Class<?> type, Object value) {
		//
		this.value = value;
		this.type = type;
		//
		if (isWrapper(type)) return;
		//
		
		for (var field : type.getFields()) {
			var entry = new WritableField(field);
			//
			if (fields.size < type.getFields().length) {
				//
				fields.add(entry);
				//
			} else if (!Modifier.isFinal(field.getModifiers()) {
				//
				for (var f : fields) f.field = f.name.equals(entry.name) ? entry.field : f.field;
			}
		}
		//
		return;
	}
	
	//table display
	public Table table(String name) {
		if (isWrapper(type)) {
			return Debugger.display(Color.maroon, name, new Table(t -> {
				t.field(value.toString(), Styles.defaultField, (String txt) -> {
					//
					set(type, parse(type, value, txt));
					//;
				}).center().pad(4f);
			}));
			//
		} else {
			return Debugger.table(Color.maroon, name, new Table(t -> {
				//
				for (var f : fields) {
					boolean stored = f.stored != null;
					var v = stored ? f.stored : f.value();
					//
					t.add(Debugger.display(stored ? Color.Green : Color.darkGray, f.name, new Table(input -> {
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
				t.button("Set", () -> {
					for (var f : fields) f.set();
					//
					set(type, value);
				}).right().pad(2f);
				//
				t.button(Icon.cancel, () -> {
					for (var f : fields) f.revert();
					//
				}).right().pad(2f);
				//
			}));
		}
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