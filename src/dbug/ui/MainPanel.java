package dbug.ui;

import arc.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.tool.Debugger.*;

public class MainPanel {
    private static Table container;
    //
	private static ImageButton expander;
	private static ScrollPane panel;
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
		panel = new ScrollPane(new Table(Tex.pane), Styles.noBarPane);
	    panel.setOverscroll(false, true);
		panel.setClamp(true);
		//
		container.add(expander).row();
		container.add(panel).row();
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
	    container.getCell(panel).size(180f, expand ? scale * 0.25f : 75f);
	    //
	    expander.replaceImage(new Image(expand ? Icon.downOpen : Icon.upOpen));
	    //
	    var widget = (Table) panel.getWidget();
	    widget.update(() -> {
			for (var v : entries.values()) {
			    var entry = v.show();
			    if (!entry.hasParent()) widget.add(entry).fill().row();
			}
	    });
	}
}