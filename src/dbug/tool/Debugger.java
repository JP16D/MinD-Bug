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
	private static final OrderedMap<Table, Table> list = new OrderedMap<>();
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
			t.add(display).size(240f, expand ? scale * 0.25f : 50f);
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
		for (var k : map.keys()) {
			var v = map.get(k);
			//
			var label = new Table(Tex.whiteui);
			var value = new Table(Tex.pane);
			//
			if (v.get() instanceof Debuggable d) {
				//
				label.setColor(Color.sky);
				label.table(Tex.whiteui, t -> {
					t.add(type.getSimpleName());
					t.setColor(Color.royal);
				}).left().pad(4f);
				//
				value.field(d.value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					map.put(k, () -> new Debuggable(d.type, txt));
					//
				}).center();
				//
			} else {
				//
				label.setColor(Color.slate);
				value.add("" + v.get()).pad(8f);
			}
			//
			label.add(k, Styles.outlineLabel).center().pad(8f);
			//
			list.put(label, value);
		}
		//
		float kw = 0f;
		float vw = 0f;
		//
		for (var entry : list) {
			kw = Math.max(kw, entry.key.width);
			vw = Math.max(vw, entry.value.width)
		}
		//
		for (var entry : list) {
			add(entry.key.fill()).width(kw).height(52);
			add(entry.value.fill()).width(vw).height(52);
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
