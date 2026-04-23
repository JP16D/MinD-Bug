package dbug.ui;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.util.*;
import dbug.tool.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.util.ParseUtil.*;

public class DebugField extends Table {
	public Color marker = new Color(Color.slate);
	//
	public Table content;
	public boolean group;
	//
	public final String type;
	public final String name;
	
	public DebugField(String name, Class<?> type, Table content) {
		this.type = type.getSimpleName();
		this.name = name;
		//
		setContent(content);
	}
	
	public DebugField(String name, Class<?> type) {
		this.type = type.getSimpleName();
		this.name = name;
	}
	
	public void updateContent() {
		clearChildren();
		left();
		//Nametag
		table(Tex.pane, panel -> {
			//
			panel.table(Tex.whiteui, nt -> {
			    nt.setColor(marker);
			    //
    			nt.table(Tex.whiteui, t -> {
    				t.add(type).fontScale(0.75f).pad(4f);
    				t.setColor(Color.royal);
    			}).pad(4f).left();
    			//
    			nt.add(name, Styles.outlineLabel).fontScale(0.75f).pad(4f).center();
			}).pad(4f).fill();
			//
			if (group) panel.row();
			if (content != null) panel.add(content).pad(4f).right();
		}).pad(4f).left().row();
	}
	
	public void setContent(Table content) {
		this.content = content;
		//
		updateContent();
	}
	
	public static Table viewOnly(Object content) {
		return new Table(t -> {
			t.image(Icon.gridSmall).pad(4f);
			//
			t.table(Tex.pane, p -> {
				if (content instanceof Drawable img) {
					p.image(img).size(20f).scaling(Scaling.bounded);
					//
				} else if (content instanceof TextureRegion img) {
					p.image(img).size(20f).scaling(Scaling.bounded);
					//
				} else p.add("" + content).pad(2f, 4f, 2f, 4f);
			}).pad(2f);
		});
	}
	
	public static Table writable(Modifiable entry) {
	    var field = Elem.newField(entry.get().toString(), (String txt) -> {
	        entry.push(parse(entry.type(), entry.get(), txt));
	    });
	    //
	    field.setStyle(Styles.defaultField);
        field.addListener(l -> {
            if (!entry.priority()) field.setText(entry.get().toString());
            return l.capture;
        });
        //
		return new Table(t -> {
			t.image(Icon.editSmall).pad(4f);
			t.add(field).pad(4f).get().setAlignment(Align.center);
		});
	}
	
	public static Table display(Color marker, Class<?> type, String name, Table val) {
		return new Table(Tex.pane, panel -> {
			//
			panel.table(Tex.whiteui, view -> {
				view.setColor(marker);
				view.table(Tex.whiteui, tag -> {
					//
					tag.add(type.getSimpleName(), Styles.outlineLabel).pad(4f);
					tag.setColor(Color.royal);
					//
				}).pad(4f).left();
				//
				view.add(name, Styles.outlineLabel).pad(4f).center();
			}).grow();
			//
			panel.add(val).padLeft(8f).size(160f, 48f);
		});
	}
	
	public static Table mdisplay(Color marker, Class<?> type, String name, Table val) {
		return new Table(Tex.pane, panel -> {
			//
			panel.table(Tex.whiteui, view -> {
				view.setColor(marker);
				view.table(Tex.whiteui, tag -> {
					//
					tag.add(type.getSimpleName(), Styles.outlineLabel).pad(4f);
					tag.setColor(Color.royal);
					//
				}).pad(4f).height(32f);
				//
				view.add(name, Styles.outlineLabel).pad(4f).center();
			}).grow().row();
			//
			panel.add(val).pad(4f);
		});
	}
}