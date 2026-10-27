package dbug.ui;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.*;

import dbug.tool.*;

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
			nt.left();
		    //
            nt.table(Tex.whiteui, t -> {
            	t.add(type, 0.75f).labelAlign(Align.center).pad(4f).center();
            	t.setColor(Color.royal);
            }).pad(4f).left();
            //
			var label = nt.table().grow().center().get();
			nt.update(() -> {
				label.clearChildren();
				label.add(name, Styles.outlineLabel, 0.75f).pad(8f).labelAlign(Align.center).grow().center();
			});
		});
	}
	
	public void updateContent() {
		clearChildren();
		//
		table(Tex.pane, p -> {
			p.add(nametag()).color(highlight).pad(4f).fill().center();
			//
			p.row();
			if (content != null) p.add(content).pad(4f).fill().left();
		}).pad(4f).left().get();
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
				}).pad(2f).minWidth(60f);
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
					p.image(img).size(15f).scaling(Scaling.bounded).pad(8f);
					//
				else if (entry.get() instanceof TextureRegion img)
					p.image(img).size(15f).scaling(Scaling.bounded).pad(8f);
					//
				else p.add(entry.get().toString()).fontScale(0.75f).pad(2f, 4f, 2f, 4f);
			});
		});
	}
	
	public static Table writable(Writable entry) {
        var field = Elem.newField(entry.get().toString(), (String txt) -> {
            entry.push(parse(entry.type(), entry.get(), txt));
        });
        //
        var style = Styles.defaultField;
        style.font.getData().setScale(0.75f);
        //
        field.setStyle(style);
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