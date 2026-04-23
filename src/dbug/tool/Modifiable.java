package dbug.tool;

import arc.graphics.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import dbug.ui.*;
import dbug.util.*;
import mindustry.gen.*;
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
	private String name;
	private Object value;
	private Class<?> type;
	
	public Modifiable(String name, Class<?> type, Object value) {
	    this.name = name;
		this.type = type;
		this.value = value;
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields()) try {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Modifiable(field.getName(), field.getType(), field.get(value)));
		} catch (Exception e) {}
	}
	
	//table display
	public DebugField show() {
		//
		if (map.size > 0) {
			var table = new DebugField(name, type);
			var content = new Table(t -> {
			    //update fields
				for (var k : map.keys()) {
				    var entry = map.get(k);
				    var field = entry.show();
				    //
				    var hint = field.content.add("").center().pad(4f).visible(() -> entry.priority);
    				field.addListener(l -> {
    				    try {
        				    var value = type.getField(k).get(this.value);
        				    //
        				    entry.set(value);
        				    hint.get().setText(value.toString());
        				    //
        				    field.marker.set(entry.priority ? Color.green : Color.darkGray);
    				    } catch (Exception e) {}
    				    //
    				    return l.capture;
    				});
					//
					t.add(field).grow().row();
		        }
			    //
				t.table(actions -> {
				    //apply changes
    				actions.button("Set", () -> {
    					/*for (var k : map.keys()) try {
    						var v = map.get(k);
    						//
    						type.getField(k).set(this.value, v);
    						map.put(k, null);
    						//
    						priority = true;
    					} catch (Exception e) {}
    					//
    					update();*/
    				}).right().pad(2f);
    				//
    				//cancel changes
    				actions.button(Icon.cancel, () -> {
    					for (var v : map.values()) v.get();
    				}).right().pad(2f).get();
				});
			});
			//
			table.group = true;
			table.marker.set(Color.maroon);
			table.setContent(content);
			//
			return table;
		} else return new DebugField(name, type, writable(this));
	}
	
	public void push(Object input) {
	    if (!(priority = value != input)) return;
	    value = input;
	}
	
	public void set(Object value) {
	    if (!priority) this.value = value;
	}
	
	public Object get() {
	    if (priority) priority = false;
	    return value;
	}
	
	public Class<?> type() {
	    return type;
	}
	
	public boolean priority() {
	    return priority;
	}
}