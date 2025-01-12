package dbug.ui;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import dbug.tool.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class DebugField extends Table {
	protected Table content;
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
		table(Tex.buttonDown, nt -> {
			//
			nt.table(Tex.whiteui, t -> {
				t.add(type).pad(4f);
				t.setColor(Color.royal);
				//
			}).pad(4f).left();
			//
			nt.add(name, Styles.outlineLabel).pad(4f).center();
			//
			nt.add(content).pad(4f).right();
		}).pad(12f, 4f, 4f, 4f).left().row();
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
					p.image(img).size(52f);
					//
				} else if (content instanceof TextureRegion img) {
					p.image(img).size(40f);
					//
				} else p.add("" + content).pad(2f);
			}).pad(2f);
		});
	}
	
	public static Table writable(Object content, Cons<String> modifier) {
		return new Table(t -> {
			t.image(Icon.editSmall).pad(4f);
			//
			t.field(content.toString(), Styles.defaultField, modifier).pad(4f);
		});
	}
	
	public static Table display(Color color, Class<?> type, String name, Table val) {
		return new Table(Tex.pane, panel -> {
			//
			panel.table(Tex.whiteui, view -> {
				view.setColor(color);
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
	
	public static Table mdisplay(Color color, Class<?> type, String name, Table val) {
		return new Table(Tex.pane, panel -> {
			//
			panel.table(Tex.whiteui, view -> {
				view.setColor(color);
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