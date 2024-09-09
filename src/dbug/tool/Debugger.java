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
		ui.put(name, new Table(table -> {
			//
			table.table(Tex.whiteui, tg -> {
				tg.setColor(Color.slate);
				tg.add(name, Styles.outlineLabel).center().pad(4f);
			}).growX().row();
			//
			table.add("" + val.get()).pad(8f).growX().height(48f);
			//
		}));
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
				//
				d.priority = false;
			}
		}
		//
		writable.put(name, v);
		//
		ui.put(name, new Table(table -> {
			//
			table.table(Tex.whiteui, tg -> {
				tg.setColor(Color.maroon);
				tg.table(Tex.whiteui, t -> {
					//
					t.add(v.type.getSimpleName(), Styles.outlineLabel).pad(4f);
					t.setColor(Color.royal);
					//
				}).left();
				//
				tg.add(name, Styles.outlineLabel).center().pad(4f);
			}).growX().row();
			//
			table.field(v.value.toString(), Styles.defaultField, (String txt) -> {
				//
				writable.put(name, new Debuggable(type, txt));
				//
			}).center().pad(4f).growX().height(48f);
		}));
		//
		return v.value;
	}
}
