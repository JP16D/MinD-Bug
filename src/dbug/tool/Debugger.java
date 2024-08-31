package dbug.tool;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.MDBugVars.*;

public class Debugger {
	//
	public static boolean expand = false;
	private float scale;
	private Table caller;
	
	public void call(Table table) {
		//
		var display = new ScrollPane(this);
		display.setClamp(true);
		//
		table.table(Tex.pane, t -> {
			t.button(expand ? Icon.downOpen : Icon.upOpen, () -> {
				expand = !expand;
				table.clearChildren();
				call(table);
				return;
			}).padRight(8f).top();
			//
			t.add(display).size(240f, expand ? scale * 0.25f : 52f);
		});
		//
		caller = table;
	}
	
	public void load() {
		//
		Events.run(EventType.Trigger.update, () -> {
			this.update();
			//
			if (scale != Core.scene.getHeight()) {
				scale = Core.scene.getHeight();
				//
				 if (caller != null) {
				 	caller.clearChildren();
					call(caller);
				 }
			}
		});
	}
	
	public void update() {
		//
		clearChildren();
		//
		float cw = 0f;
		//
		for (var k : map.keys()) {
			var v = map.get(k);
			//
			var tag = new Table(Tex.whiteui);
			var val = new Table();
			//
			val.setColor(Color.black);
			if (v.get() instanceof Debuggable d) {
				//
				tag.setColor(Color.maroon);
				tag.table(Tex.whiteui, t -> {
					//
					t.add(d.type.getSimpleName(), Styles.outlineLabel).pad(4f);
					t.setColor(Color.royal);
					//
				}).left();
				//
				val.field(d.value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					map.put(k, () -> new Debuggable(d.type, txt));
					//
				}).center().pad(4f);
				//
			} else {
				//
				tag.setColor(Color.slate);
				val.add("" + v.get()).pad(8f);
			}
			//
			tag.add(k, Styles.outlineLabel).center().pad(4f);
			//
			table(Tex.paneSolid, t -> {
				//
				t.add(tag).grow().row();
				t.add(val).growX().height(48f);
				//
			}).cell.pad(4f).growX();
			//
			row();
		}
	}
	
	//returns the placeholder value if main value is null to avoid null error crashes
	public static <T extends Object> T nullCatch(T val, T def) {
		//
		return val != null ? val : placeholder;
	}
	
	//add debuggable object (read-only)
	public static Prov<?> dv(String name, Prov<?> val) {
		return map.put(name, val);
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
		UI.put(name, new Table(display -> {
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
