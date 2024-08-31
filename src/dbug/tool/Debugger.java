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
	//returns a default value if main value is null to avoid null error crashes
	public static <T extends Object> T nullCatch(T val, T def) {
		//
		return val != null ? val : placeholder;
	}
	
	//add debuggable object (read-only)
	public static Prov<?> dv(String name, Prov<?> val) {
		ui.put(name, new Table(display -> {
			//
			var tag = new Table(Tex.whiteui);
			var val = new Table();
			//
			tag.setColor(Color.slate);
			tag.add(k, Styles.outlineLabel).center().pad(4f);
			//
			val.add("" + v.get()).pad(8f);
			//
			display.add(tag).grow().row();
			display.add(val).growX().height(48f);
		}));
		//
		return val;
	}
	
	//add debuggable primitive value (writable)
	public static Prov<?> dw(Class<?> type, String name, Prov<?> val) {
		Prov<Debuggable> v = () -> new Debuggable(type, val);
		//
		if (map.containsKey(name) && map.get(name).get() instanceof Debuggable d) {
			//
			if (d.priority) {
				v = () -> d;
				//
				d.priority = false;
			}
		}
		//
		ui.put(name, new Table(display -> {
				//
			var tag = new Table(Tex.whiteui);
			var val = new Table();
				//
			tag.setColor(Color.maroon);
			tag.table(Tex.whiteui, t -> {
				//
				t.add(type.getSimpleName(), Styles.outlineLabel).pad(4f);
				t.setColor(Color.royal);
					//
			}).left();
			//
			tag.add(name, Styles.outlineLabel).center().pad(4f);
			//
			val.field(value.get().toString(), Styles.defaultField, (String txt) -> {
				//
				map.put(name, () -> new Debuggable(type, txt));
				//
			}).center().pad(4f);
			//
			display.add(tag).grow().row();
			display.add(val).growX().height(48f);
		}));
		//
		return v.get().value;
	}
}
