package dbug;

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
import mindustry.ui.dialogs.*;
import java.lang.*;

public class Debugger extends Table {
	private static final OrderedMap<String, Prov<?>> map = new OrderedMap<>();
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
			t.add(display).size(240f, expand ? scale * 0.25f : 40f);
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
			var main = table(Tex.whiteui).get();
			var label = main.table().left().pad(2f).get();
			//
			main.row();
			//
			var val = main.table(Tex.pane).pad(2f).get();
			//
			if (v.get() instanceof Debuggable d) {
				//
				String[] arr = ((Debuggable) v.get()).type.toString().split(".");
				String type = arr[arr.length - 1];
				//
				label.table(Tex.whiteui, t -> {
					t.add(type);
					t.setColor(Color.royal);
				});
				//
				main.setColor(Color.slate);
				val.field(d.value.get().toString(), Styles.defaultField, (String txt) -> {
					//
					map.put(k, () -> new Debuggable(type, txt));
					//
				}).center().pad(2f);
				//
			} else {
				//
				removeChild(label);
				main.setColor(Color.sky);
				val.add("" + v.get()).pad(4f).width(main.getWidth());
			}
			//
			label.add(k).left().pad(2f);
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
