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

public class Debugger extends Table {
	private static final ObjectMap<String, Prov<?>> map = new ObjectMap<>();
	
	public void load() {
		//
		Events.run(EventType.Trigger.update, () -> {
			this.update();
		});
	}
	
	public void update() {
		//
		clearChildren();
		table(Tex.pane, t -> {
			for (var k : map.keys()) {
				var v = map.get(k);
				//
				t.add(k).left().pad(0f, 2f, 0f, 2f);
				t.add(":").pad(0f, 8f, 0f, 8f);
				//
				if (v.get() instanceof Debuggable d) {
					//
					String[] arr = ((Debuggable) v.get()).type.toString().split(".");
					String type = arr[arr.length - 1];
					//
					t.table(tf -> {
						tf.add(type);
						tf.field(d.value.get().toString(), Styles.defaultField, (String txt) -> {
							//
							map.put(k, () -> new Debuggable(type, txt));
							//
						}).get().setAlignment(Align.center);
					}).pad(0f, 2f, 0f, 2f).row();
					//
				} else {
					//
					t.add("" + v.get()).pad(0f, 2f, 0f, 2f).row();
				}
			}
		});
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
		public Debuggable(String type, String val) throws ClassNotFoundException {
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
					value = () -> this.type.cast(method.invoke(sample, val));
				} catch (Exception e) {
					//
					value = () -> old;
					//warn();
				}
			}
		}
		
	}
}