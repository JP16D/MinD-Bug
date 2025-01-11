package dbug.ui;

import arc.func.*;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import dbug.tool.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class DebugField extends Table {
	protected Object content;
	protected Cons<String> modifier;
	//
	public final String type;
	public final String name;
	
	public DebugField(String name, Object content, Cons<String> modifier) {
		this.type = (content instanceof Modifiable m ? m.type : content.getClass()).getSimpleName();
		this.name = name;
		this.content = content;
		//
		this.modifier = modifier;
		//
		updateContent();
	}
	
	public DebugField(String name, Object content) {
		this.type = (content instanceof Modifiable m ? m.type : content.getClass()).getSimpleName();
		this.name = name;
		this.content = content;
		//
		updateContent();
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
		}).pad(12f, 4f, 4f, 4f).left().row();
		//Value field
		table(Tex.button, t -> {
			//Writable value
			if (content instanceof Modifiable m) {
				if (m.map.size > 0) {
					//
					for (var k : m.map.keys()) add(new DebugField(k, m.map.get(k), modifier));
					//
				} else {
					//
					t.image(Icon.editSmall).pad(4f);
					//
					t.field(m.value.toString(), Styles.defaultField, modifier).pad(4f);
					//
				}
			} else {
				//View only value
				t.image(Icon.eyeSmall).pad(4f);
				//
				if (content instanceof Drawable img) {
					t.table(Tex.pane, p -> p.image(img).pad(4f)).size(52f);
					//
				} else if (content instanceof TextureRegion img) {
					t.table(Tex.pane, p -> p.image(img)).pad(4f).size(52f);
					//
				} else t.add("" + content).pad(4f);
			}
		}).pad(4f).left().row();
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