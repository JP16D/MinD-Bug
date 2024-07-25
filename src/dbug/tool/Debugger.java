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

public class Debugger extends Table {
	private static final OrderedMap<String, Prov<?>> map = new OrderedMap<>();
	private static final OrderedMap<String, Seq<Table>> entries = new OrderedMap<>();
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
			}).top();
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
		for (var k : map.keys()) {
			var v = map.get(k);
			//
			var tag = new Table(Tex.whiteui);
			var val = new Table(Tex.pane);
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
				}).center();
				//
			} else {
				//
				tag.setColor(Color.slate);
				val.add("" + v.get()).pad(8f);
			}
			//
			tag.add(k, Styles.outlineLabel).center();
			//
			entries(k, (new Seq(true)).add(tag, val));
			//
			cw = Math.max(cw, tag.getWidth());
		}
		//
		for (var entry : entries) {
			//
			t.add(entry[0]).width(cw).height(48f).pad(2f).grow();
			t.add(entry[1]).width(160f).height(48f).pad(2f).grow();
			//
			row();
		}
	}
	
	//add debuggable object (read-only)
	public static Prov<?> dv(String name, Prov<?> val) {
		map.put(name, val);
		//
		return map.get(name);
	}
	
	//add debuggable primitive value (writable)
	public static Prov<?> dw(Class<?> type, String name, Prov<?> val) {
		map.put(name, () -> new Debuggable(type, val));
		//
		return ((Debuggable) map.get(name).get()).value;
	}
}
