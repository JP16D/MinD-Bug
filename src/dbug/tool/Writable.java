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

public class Writable extends Viewable {
    protected OrderedMap<String, Writable> map = new OrderedMap<>();
    protected Object incoming;
	
	public Writable(Class<?> type, String name, Object value) {
	    super(type, name, value);
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields()) try {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Writable(field.getType(), field.getName(), field.get(value)));
		} catch (Exception e) {}
	}
	
	@Override
	public DebugField show() {
		//
		if (map.size > 0) {
			var table = new DebugField(type, name);
			var content = new Table(t -> {
			    //update fields
				for (var k : map.keys()) {
				    var entry = map.get(k);
				    var field = entry.show();
				    //
				    var hint = new Viewable("");
				    //
    				field.content.update(() -> {
    				    try {
    				        var v = type.getField(k).get(value);
    				        hint.set(entry.priority() ? v.toString() : "--");
        				    entry.set(v);
    				    } catch (Exception e) {}
    				});
    				//
    				field.content.image(Icon.zoomSmall).pad(4f);
				    field.content.add(viewable(hint)).pad(4f);
				    //
				    field.marker.set(Color.darkGray);
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
    						type.getField(v.name).set(value, v.release());
    					} catch (Exception e) {}
    				}).pad(2f);
    				//
    				//cancel changes
    				actions.button(Icon.cancel, () -> {
    					for (var v : map.values()) v.release();
    				}).pad(2f);
				}).right();
			});
			//
			table.marker.set(Color.maroon);
			table.setContent(content);
			//
			return table;
		} else return new DebugField(type, name, writable(this));
	}
	
	public void push(Object input) {
	    incoming = input;
	}
	
	public Object release() {
	    if (priority()) {
	        value = incoming;
	        incoming = null;
	    }
	    return value;
	}
	
	public boolean priority() {
	    return incoming != null && incoming != value;
	}
}