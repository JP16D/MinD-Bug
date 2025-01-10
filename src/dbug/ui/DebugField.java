package dbug.ui;

import arc.func*;
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
	protected Cons<?> function;
	//
	public final String type;
	public final String name;
	
	public DebugField(String name, Object content, Cons<?> function) {
		this.type = content.getClass().getSimpleName();
		this.name = name;
		this.content = content;
		this.function = function;
		//
		update();
	}
	
	public void update() {
		clearChildren();
		//
		table(Tex.buttonDown, nt -> {
			//
			if (!type.isBlank()) nt.table(Tex.whiteui, t -> {
				t.add(type);
				t.setColor(Color.royal);
			}).pad(4f);
			//
			nt.add(name, Styles.outlineLabel).pad(4f);
		}).row();
		
		if (content instanceof Modifiable m) {
			if (m.isObject()) {
				
			} else {
				table(Tex.button, t -> {
					//
					t.image(Icon.editSmall).pad(4f);
					t.field(m.value.toString(), Styles.defaultField, function).pad(4f);
					//
				}).row();
			}
			//
		} else table(Tex.button, t -> {
			if (content instanceof Drawable || content instanceof TextureRegion) {
				//
				t.image(content).pad(4f);
				//
			} else add("" + content);
		}).row();
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