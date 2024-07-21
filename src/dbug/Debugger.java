package dbug;

import arc.*;
import arc.func.*;
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
import mindustry.ui.dialogs.*;
import java.lang.*;

public class Debugger extends Table {
	private static final OrderedMap<String, Prov<?>> map = new OrderedMap<>();
	public static boolean expand = false;
	
	public void call(Table table) {
		//
		var display = new ScrollPane(this);
		display.setClamp(true);
		//
		table.table(Tex.pane, t -> {
			t.button(expand ? Icon.down : Icon.up, () -> {
				expand = !expand;
				table.clearChildren();
				call(table);
				return;
			});
			//
			t.add(display).size(360f, expand ? Core.scene.getHeight() * 0.25f : 40f);
		});
	}
	
	public void load() {
		//
		Events.run(EventType.Trigger.update, () -> {
			this.update();
		});
	}
	
	public void update() {
		//
		clearChildren();
		//
		var kt = table(Tex.pane).get();
		var vt = table(Tex.pane).get();
		//
		for (var k : map.keys()) {
			var v = map.get(k);
			//
			kt.add(k).center().pad(1f, 2f, 1f, 2f);
			//
			if (v.get() instanceof Debuggable d) {
				//
				String[] arr = ((Debuggable) v.get()).type.toString().split(".");
				String type = arr[arr.length - 1];
				//
				vt.add(type).center();
				vt.field(d.value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					map.put(k, () -> new Debuggable(type, txt));
					//
				}).center().pad(1f, 2f, 1f, 2f);
				//
			} else {
				//
				vt.add("" + v.get()).pad(1f, 2f, 1f, 2f);
			}
			//
			kt.row();
			vt.row();
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
	
	private static class Debuggable {
		public Prov<?> value;
		public Class<?> type;
		
		public Debuggable(Class<?> type, Prov<?> val) {
			value = val;
			this.type = type;
		}
		
		//some sort of parsing shenanigans
		public Debuggable(String type, String val) {
			var old = value;
			//
			if (type == "String") {
				this.type = String.class;
				value = () -> val;
				//
			} else {
				try {
					this.type = Class.forName(type);
					//
					var method = this.type.getMethod("valueOf", String.class);
					var sample = this.type.getConstructor().newInstance();
					//
					value = () -> {
						try {
							return this.type.cast(method.invoke(sample, val));
						} catch (Exception e) {
							return old;
							//warn();
						}
					};
					//
				} catch (Exception e) {
					//Impossible
				}
			}
		}
	}
}
