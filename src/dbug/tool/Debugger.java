package dbug.tool;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.MDBugVars.*;

public class Debugger {
	static final OrderedMap<String, Debuggable> writable = new OrderedMap<>();
	
	//returns a default value if main value is null to avoid null error crashes
	public static <T extends Object> T check(T val, T def) {
		//
		return val != null ? val : def;
		//if (val == null) warn();
	}
	
	//add debuggable object (read-only)
	public static Prov<?> dv(String name, Prov<?> val) {
		debugger.put(name, display(Color.slate, name, new Table(Tex.pane, t -> t.add("" + val.get()))));
		//
		return val;
	}
	
	//add debuggable primitive value (writable)
	public static Prov<?> dw(Class<?> type, String name, Prov<?> val) {
		var v = new Debuggable(type, val);
		//
		if (writable.containsKey(name)) {
			var d = writable.get(name);
			//
			if (d.priority) {
				v.set(d.type, d.value);
			}
		}
		//
		writable.put(name, v);
		//
		debugger.put(name, display(Color.maroon, name, new Table(Tex.pane, t -> {
				t.field(v.value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					writable.put(name, v.parse(type, txt));
				//
				}).center().pad(4f);
			})));
		//
		return v.value;
	}
	
	//add debuggable object (writable)
	public static Prov<?> dwo(Class<?> type, String name, Prov<?> val) {
		var v = new Compound(type, val);
		//
		if (writable.containsKey(name)) {
			var d = (Compound) writable.get(name);
			//
			d.prioritize(v);
		}
		//
		writable.put(name, v);
		//
		debugger.put(name, table(Color.maroon, name, v.actor()));
		//
		return v.value;
	}
	
	public static Table display(Color color, String name, Table val) {
		return new Table(Tex.pane, panel -> {
			//
			panel.table(Tex.whiteui, view -> {
				view.setColor(color);
				if (writable.containsKey(name)) view.table(Tex.whiteui, tag -> {
					//
					tag.add(writable.get(name).type.getSimpleName(), Styles.outlineLabel).pad(4f);
					tag.setColor(Color.royal);
					//
				}).pad(4f);
				//
				view.add(name, Styles.outlineLabel).pad(4f).center();
			}).grow();
			//
			panel.add(val).padLeft(8f).size(160f, 48f);
		});
	}
	
	public static Table table(Color color, String name, Table val) {
		return new Table(Tex.pane, panel -> {
			//
			panel.table(Tex.whiteui, view -> {
				view.setColor(color);
				if (writable.containsKey(name)) view.table(Tex.whiteui, tag -> {
					//
					tag.add(writable.get(name).type.getSimpleName(), Styles.outlineLabel).pad(4f);
					tag.setColor(Color.royal);
					//
				}).pad(4f).height(32f);
				//
				view.add(name, Styles.outlineLabel).pad(4f).center();
			}).grow().row();
			//
			panel.add(val).pad(4f).width(148f);
		});
	}
}
