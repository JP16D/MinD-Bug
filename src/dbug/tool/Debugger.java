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
			var main = table(Tex.whiteui).left().pad(4f).get();
			var label = main.table().center().pad(2f, 4f, 2f, 4f).get();
			var value = main.table(Tex.whiteui).pad(4f).color(Color.black).get();
			//
			if (v.get() instanceof Debuggable d) {
				//
				String type = d.type.getSimpleName();
				//
				label.table(Tex.whiteui, t -> {
					t.add(type).pad(2f);
					t.setColor(Color.royal);
				}).left();
				//
				main.setColor(Color.slate);
				value.field(d.value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					map.put(k, () -> new Debuggable(d.type, txt));
					//
				}).center();
				//
			} else {
				//
				main.setColor(Color.sky);
				value.add("" + v.get()).pad(8f);
			}
			//
			label.add(new Label(k, Styles.outlineLabel)).center().pad(2f, 8f, 2f, 8f).get().setColor(main.color);
			row();
		}
		//
		float mw = 0f;
		float lw = 0f;
		float vw = 0f;
		for (var main : getCells()) {
			mw = Math.max(mw, main.get().getWidth());
			//
			lw = Math.max(lw, ((Table) main.get()).getCells().get(0).get().getWidth());
			vw = Math.max(vw, ((Table) main.get()).getCells().get(1).get().getWidth());
		}
		//
		for (var main : getCells()) {
			main.get().setWidth(mw);
			//
			((Table) main.get()).getCells().get(0).get().setWidth(lw);
			((Table) main.get()).getCells().get(1).get().setWidth(vw);
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
