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

public class Modifiable extends Viewable {
    protected OrderedMap<String, Modifiable> map = new OrderedMap<>();
	protected boolean priority;
	protected boolean released;
	
	public Modifiable(String name, Class<?> type, Object value) {
	    super(name, type, value);
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields()) try {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Modifiable(field.getName(), field.getType(), field.get(value)));
		} catch (Exception e) {}
	}
	
	@Override
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
				    var hint = new Viewable("");
				    var hintc = viewable(hint);
				    field.marker.set(Color.darkGray);
    				field.update(() -> {
    				    try {
        				    entry.set(type.getField(k).get(value));
        				    hint.set(entry.get().toString());
    				    } catch (Exception e) {}
    				    //
    				    field.content.removeChild(hintc, false);
    				    if (entry.priority) field.content.add(hintc);
    				});
    				field.updateContent();
					//
					t.add(field).grow().row();
		        }
			    //
			    //group actions
				t.table(actions -> {
				    //apply changes
    				actions.button("Set", () -> {
    					for (var v : map.values()) try {
    						type.getField(v.name).set(value, v.get());
    						//
    						priority = true;
    					} catch (Exception e) {}
    				}).pad(2f);
    				//
    				//cancel changes
    				actions.button(Icon.cancel, () -> {
    					for (var v : map.values()) v.open();
    				}).pad(2f);
				}).right();
			});
			//
			table.marker.set(Color.maroon);
			table.setContent(content);
			//
			return table;
		} else return new DebugField(name, type, writable(this));
	}
	
	@Override
	public void set(Object value) {
	    if (!priority) this.value = value;
	}
	
	@Override
	public Object get() {
	    released = priority;
	    return value;
	}
	
	public void push(Object input) {
	    if (!(priority = value != input)) return;
	    value = input;
	}
	
	public void open() {
        if (priority && released) priority = released = false;
	}
	
	public boolean priority() {
	    return priority;
	}
}