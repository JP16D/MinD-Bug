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
		this.type = content.getClass().getSimpleName();
		this.name = name;
		this.content = content;
		this.modifier = modifier;
		//
		updateContent();
	}
	
	public DebugField(String name, Object content) {
		var type = content.getClass();
		//
		this.type = (type == Modifiable.class ? content.type : type).getSimpleName();
		this.name = name;
		this.content = content;
		//
		updateContent();
	}
	
	public void updateContent() {
		clearChildren();
		//
		table(Tex.buttonDown, nt -> {
			//
			nt.table(Tex.whiteui, t -> {
				t.add(type);
				t.setColor(Color.royal);
			}).pad(4f);
			//
			nt.add(name, Styles.outlineLabel).pad(4f);
		}).row().left();
		//
		table(Tex.button, t -> {
			if (content instanceof Modifiable m) {
				if (m.map.size > 0) {
					for (var k : m.map.keys()) add(new DebugField(k, m.map.get()));
				} else {
					//
					t.image(Icon.editSmall).pad(4f);
					//
					t.field(m.value.toString(), Styles.defaultField, modifier).pad(4f);
				}
			} else t.image(Icon.eyeSmall).pad(4f);
			//
			if (content instanceof Drawable img) {
				t.table(Tex.pane, p -> p.image(img).pad(4f)).size(52f);
				//
			} else if (content instanceof TextureRegion img) {
				t.table(Tex.pane, p -> p.image(img)).pad(4f).size(52f);
				//
			} else t.add("" + content);
		}).row().left();
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