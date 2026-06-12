package dbug.ui;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.struct.*;
import arc.util.*;
import dbug.tool.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.ui.MainPanel.*;
import static dbug.util.ParseUtil.*;

public class DebugField extends Table {
    private final Color highlight = new Color(Color.slate);
    //
    private final String type, name;
    //
	public Table content;
	public boolean group;
	
	public DebugField(Class<?> type, String name, Table content) {
		this(type, name);
		//
		setContent(content);
	}
	
	public DebugField(Class<?> type, String name) {
	    this.type = type.getSimpleName();
	    this.name = name;
	}
	
	private Table nametag() {
	    return new Table(Tex.whiteui, nt -> {
		    nt.setColor(highlight);
		    //
            nt.table(Tex.whiteui, t -> {
            	t.add(type).fontScale(0.75f).pad(4f);
            	t.setColor(Color.royal);
            }).pad(4f).left();
            //
            nt.add(name, Styles.outlineLabel).fontScale(0.75f).pad(4f).center();
		});
	}
	
	public void updateContent() {
		clearChildren();
		left();
		//
		var panel = table(Tex.pane, p -> {
			p.add(nametag()).color(highlight).pad(4f).fill();
			//
			p.row();
			if (content != null) p.add(content).pad(4f).fill().center();
		}).pad(4f).get();
		//
		row();
	}
	
	public void setContent(Table content) {
		this.content = content;
		//
		updateContent();
	}
	
	public void setHighlight(Color color) {
	    highlight.set(color);
	    //
	    updateContent();
	}
	
	public static Table group(Writable group, OrderedMap<String, Writable> map) {
	    return new Table(t -> {
		    //update fields
			for (var k : map.keys()) {
			    var entry = map.get(k);
			    var field = entry.show();
			    //
			    var hint = new Viewable("");
				field.content.update(() -> {
				    try {
				        var v = group.type().getField(k).get(group.get());
				        hint.set(entry.priority() ? v.toString() : "--");
    				    entry.set(v);
				    } catch (Exception e) {}
				});
				//
				field.content.image(Icon.zoomSmall).pad(4f);
			    field.content.add(viewable(hint)).pad(4f);
			    //
			    field.setHighlight(Color.darkGray);
				//
				t.add(field).grow().row();
	        }
		    //
		    //group actions
			t.table(actions -> {
			    //apply changes
				actions.button("Set", () -> {
					for (var v : map.values()) try {
						group.type().getField(v.name()).set(group.get(), v.release());
					} catch (Exception e) {}
				}).pad(2f);
				//
				//cancel changes
				actions.button(Icon.cancel, () -> {
					for (var v : map.values()) v.release();
				}).pad(2f);
			}).right();
		});
	}
	
	public static Table viewable(Viewable entry) {
	    return new Table(Tex.pane, p -> {
			p.update(() -> {
                p.clearChildren();
                //
				if (entry.get() instanceof Drawable img)
					p.image(img).size(20f).scaling(Scaling.bounded);
					//
				else if (entry.get() instanceof TextureRegion img)
					p.image(img).size(20f).scaling(Scaling.bounded);
					//
				else p.add(entry.get().toString()).pad(2f, 4f, 2f, 4f);
			});
		});
	}
	
	public static Table writable(Writable entry) {
        var field = Elem.newField(entry.get().toString(), (String txt) -> {
            entry.push(parse(entry.type(), entry.get(), txt));
        });
        //
        field.setStyle(Styles.defaultField);
        field.update(() -> {
            if (!entry.priority() || entry.get() == parse(entry.type(), entry.get(), field.getText())) field.setText(entry.get().toString());
        });
        //
		return new Table(t -> {
			t.image(Icon.editSmall).pad(4f);
			t.add(field).pad(4f).get().setAlignment(Align.center);
		});
	}
}