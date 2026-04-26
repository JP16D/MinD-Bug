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
    //
	private static ImageButton expander;
	private static ScrollPane panel;
	private static Table controls;
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
		controls = new Table(Tex.pane);
		//
		container.add(expander).size(35f, 20f).row();
		container.add(panel).row();
		container.add(controls);
		//
		// BLUI button surgery
		Button bluiBtn = null;
		for (var cell : ((Table) container.parent).getCells()) if (cell.get() instanceof Button btn) bluiBtn = btn;
		bluiBtn.setStyle(Styles.defaultt);
		bluiBtn.remove();
		//
		controls.add(bluiBtn);
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
	    if (container == null) return;
	    container.getCell(panel).size(280f, expand ? scale * 0.25f : 75f);
	    //
	    expander.replaceImage(new Image(expand ? Icon.downOpen : Icon.upOpen));
	    //
	    panel.setWidget(new Table(t -> {
			for (var v : debugger.values()) t.add(v).grow().row();
		}));
	}
}