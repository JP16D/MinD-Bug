package dbug.ui;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.util.*;
import dbug.tool.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.ui.MainPanel.*;
import static dbug.util.ParseUtil.*;

public class DebugField extends Table {
    public final Table extras = new Table(Tex.pane);
    public final Color marker = new Color(Color.slate);
    //
	public Table content;
	public boolean group;
	//
	public final String type;
	public final String name;
	
	public DebugField(String name, Class<?> type, Table content) {
		this(name, type);
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
		//
		table(Tex.pane, panel -> {
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
			panel.row();
			if (content != null) panel.add(content).pad(4f).fill().center();
		}).pad(4f).left();
		//
		update(() -> {
		   if (!extras.hasParent()) add(extras).pad(4f).fillY();
		   else removeChild(extras);
		});
		//
		row();
	}
	
	public void setContent(Table content) {
		this.content = content;
		//
		updateContent();
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
	
	public static Table writable(Modifiable entry) {
	    var field = Elem.newField(entry.get().toString(), (String txt) -> {
	        entry.push(parse(entry.type(), entry.get(), txt));
	    });
	    //
	    field.setStyle(Styles.defaultField);
        field.update(() -> {
            if (!entry.priority()) field.setText(entry.get().toString());
        });
        //
		return new Table(t -> {
			t.image(Icon.editSmall).pad(4f);
			t.add(field).pad(4f).get().setAlignment(Align.center);
		});
	}
}