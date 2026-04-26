package dbug.ui;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.MDBugVars.*;

public class MainPanel {
    private static Table container;
	private static ScrollPane panel;
	private static ImageButton expander;
	//
	private static boolean expand;
	private static float scale;
	
	public static void init(Table container) {
	    MainPanel.container = container;
	    //
		expander = Elem.newImageButton(Icon.upOpen, () -> {
			expand = !expand;
			update();
		});
		//
		panel = new ScrollPane(new Table(), Styles.noBarPane);
	    panel.setOverscroll(false, true);
		panel.setClamp(true);
		//
		container.add(expander).padRight(8f).row();
		container.add(panel).fill().row();
		container.add("");
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
	    container.sizeBy(300f, expand ? scale * 0.25f : 75f);
	    //
	    if (panel == null) return;
	    //
	    expander.replaceImage(new Image(expand ? Icon.downOpen : Icon.upOpen));
	    //
	    panel.setWidget(new Table(t -> {
			for (var v : debugger.values()) t.add(v).grow().row();
		}));
	}
}