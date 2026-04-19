package dbug.ui;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.MDBugVars.*;

public class MainPanel {
	private static Cell<ScrollPane> panel;
	private static boolean expand;
	private static float scale;
	
	public static void call(Table table) {
	    table.button(expand ? Icon.downOpen : Icon.upOpen, () -> {
			expand = !expand;
			update();
		}).padRight(8f).row();
		//
		panel = table.add(new ScrollPane(new Table(), Styles.noBarPane));
		//
		panel.get().setOverscroll(false, true);
		panel.get().setClamp(true);
		//
		update();
	}
	
	public static void scale() {
		if (scale != Core.scene.getHeight()) {
			scale = Core.scene.getHeight();
			//
			update();
		}
	}
	
	public static void update() {
	    if (panel == null) return;
	    //
	    panel.get().setWidget(new Table(t -> {
			for (var v : debugger.values()) t.add(v).pad(2f).grow().row();
		}));
		panel.size(360f, expand ? scale * 0.25f : 74f).top();
	}
}