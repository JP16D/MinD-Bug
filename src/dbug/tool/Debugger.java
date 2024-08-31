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
	private static final OrderedMap<String, Debuggable> writable = new OrderedMap<>();
	
	//returns a default value if main value is null to avoid null error crashes
	public static <T extends Object> T nullCatch(T val, T def) {
		//
		return val != null ? val : def;
	}
	
	//add debuggable object (read-only)
	public static Prov<?> dv(String name, Prov<?> val) {
		ui.put(name, new Table(display -> {
			//
			var tag = new Table(Tex.whiteui);
			var value = new Table();
			//
			tag.setColor(Color.slate);
			tag.add(name, Styles.outlineLabel).center().pad(4f);
			//
			value.add("" + val.get()).pad(8f);
			//
			display.add(tag).grow().row();
			display.add(value).growX().height(48f);
		}));
		//
		return val;
	}
	
	//add debuggable primitive value (writable)
	public static Prov<?> dw(Class<?> type, String name, Prov<?> val) {
		var v = new Debuggable(type, val);
		//
		if (writable.containsKey(name)) {
			//
			if (d.priority) {
				v = d;
				//
				d.priority = false;
			}
		}
		//
		writable.put(name, v);
		//
		ui.put(name, new Table(display -> {
				//
			var tag = new Table(Tex.whiteui);
			var value = new Table();
				//
			tag.setColor(Color.maroon);
			tag.table(Tex.whiteui, t -> {
				//
				t.add(v.type.getSimpleName(), Styles.outlineLabel).pad(4f);
				t.setColor(Color.royal);
					//
			}).left();
			//
			tag.add(name, Styles.outlineLabel).center().pad(4f);
			//
			value.field(v.value.toString(), Styles.defaultField, (String txt) -> {
				//
				writable.put(name, new Debuggable(type, txt));
				//
			}).center().pad(4f);
			//
			display.add(tag).grow().row();
			display.add(value).growX().height(48f);
		}));
		//
		return v.value;
	}
}
