package dbug.ui;

import arc.*;
import arc.scene.*;
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
	
	public static Cell<Element> addControl(Element e) {
	    return controls.add(e);
	}
	
	public static void bluiFix() {
	    if (container == null) return;
	    //
	    
    		Table bluiBtn = null;
    		//
    		for (var cell : ((Table) container.parent).getCells()) bluiBtn = (Table) cell.get();
    		bluiBtn.background(Tex.button);
    		//
    		controls.add(bluiBtn);

	}
}