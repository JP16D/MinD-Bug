package dbug.tool;

import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.util.*;
import arc.struct.*;
import dbug.ui.*;
import dbug.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import java.lang.*;
import java.lang.reflect.*;

import static dbug.ui.DebugField.*;
import static dbug.ui.MainPanel.*;
import static dbug.util.ParseUtil.*;

public class Modifiable {
	protected boolean priority;
	//
	public OrderedMap<String, Modifiable> map = new OrderedMap<>();
	//
	public Object value;
	public Class<?> type;
	
	public Modifiable(Class<?> type, Object value) {
		this.type = type;
		this.value = value;
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields()) {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), null);
		}
	}
	
	//table display
	public Table call(String name, Object value) {
		//
		if (map.size > 0) {
			return mdisplay(Color.maroon, type, name, new Table(t -> {
				/*for (var k : map.keys()) try {
					//
					var input = new Table();
					var field = type.getField(k);
					var stored = map.get(k);
					//
					boolean empty = stored == null || stored == field.get(value);
					var v = empty ? field.get(value) : stored;
					var mod = new Modifiable(field.getType(), v);
					map.put(k, mod);
					//
					input.field(v.toString(), Styles.defaultField, (String txt) -> {
						//
						write(txt);
						//
					}).center().pad(4f);
					//
					if (!empty) input.add(field.get(value).toString()).center().pad(4f);
					
					t.add(display(empty ? Color.darkGray : Color.green, field.getType(), field.getName(), input)).grow().row();
				} catch (Exception e) {}
				//
				//apply changes 
				t.button("Set", () -> {
					for (var k : map.keys()) try {
						var v = map.get(k);
						//
						type.getField(k).set(this.value, v);
						map.put(k, null);
						//
						priority = true;
					} catch (Exception e) {}
					//
					update();
				}).right().pad(2f);
				//
				//revert changes
				t.button(Icon.cancel, () -> {
					for (var k : map.keys()) map.put(k, null);
					//
					update();
				}).right().pad(2f).get();*/
			}));
		} else {
			var table = new DebugField(name, type);
			//
			table.setContent(writable(this, () -> {
    			priority = true;
    			table.updateContent();
    		}));
			//
			return table;
		}
	}
}