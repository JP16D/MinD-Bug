package dbug.ui;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;

import static dbug.MDBugVars.*;

public class MainPanel {
	private static Table caller;
	private static ScrollPane panel;
	private static boolean expand;
	private static float scale;
	
	public static void call(Table table, float x, float y) {
		caller = table;
		//
		panel = new ScrollPane(new Table(t -> {
			for (var v : debugger.values()) t.add(v).growX().pad(2f).row();
		}));
		//
		panel.setOverscroll(false, true);
		panel.setClamp(true);
		//
		table.button(expand ? Icon.downOpen : Icon.upOpen, () -> {
			expand = !expand;
			update();
		}).padRight(8f).row();
		//
		table.table(Tex.pane, t -> {
			//
			t.add(panel).size(320f, expand ? scale * 0.25f : 72f).top();
		});
		//
		panel.setScrollX(x);
		panel.setScrollY(y);
	}
	
	public static void scale() {
		if (scale != Core.scene.getHeight()) {
			scale = Core.scene.getHeight();
			//
			update();
		}
	}
	
	public static void update() {
		if (caller == null) return;
		//
		float x = panel.getScrollX();
		float y = panel.getScrollY();
		//
		caller.clearChildren();
		call(caller, x, y);
	}
}