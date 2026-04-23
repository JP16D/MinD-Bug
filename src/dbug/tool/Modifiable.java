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
			    t.addListener(a -> {
    			    //update fields
    				for (var k : map.keys()) try {
    				    var entry = map.get(k);
    				    var value = type.getField(k).get(this.value);
    				    //
    				    var field = entry.show();
    				    entry.set(value);
    				    //
    				    field.marker.set(entry.priority ? Color.green : Color.darkGray);
    				    if (entry.priority) field.content.add(value.toString()).center().pad(4f);
    					//
    					t.add(field).grow().row();
			        } catch (Exception e) {}
			        //
			        return a.capture;
			    });
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
    					/*for (var k : map.keys()) map.put(k, null);
    					//
    					update();*/
    				}).right().pad(2f).get();
				});
			});
			//
			table.group = true;
			table.marker.set(Color.maroon);
			table.setContent(content);
			//
			return table;
		} else {
		    var table = new DebugField(name, type);
    		table.setContent(writable(this, () -> table.updateContent()));
    		//
    		return table;
		}
	}
	
	public void pass(String input) {
	    var old = value;
	    value = parse(type, value, input);
	    //
	    priority = old != value;
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
}