package dbug.ui;

import arc.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.tool.Debugger.*;

public class MainPanel {
    private static OrderedMap<String, Table> map = new OrderedMap();
    //
    private static Table container;
	private static ImageButton expander;
	private static ScrollPane panel;
	//
	private static boolean expand;
	private static float scale;
	
	public static void init() {
	    if (panel != null) return;
	    expander = Elem.newImageButton(Icon.upOpen, () -> {
			expand = !expand;
			update();
		});
		//
		panel = new ScrollPane(new Table(), new ScrollPane.ScrollPaneStyle(){{
		    background = Tex.pane;
		}});
	    panel.setOverscroll(false, true);
		panel.setClamp(true);
	}
	
	public static void load(Table container) {
	    MainPanel.container = container;
		init();
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
			for (var k : entries.keys()) {
			    if (!map.containsKey(k)) map.put(k, entries.get(k).show());
		        //
		        var entry = map.get(k);
		        if (!entry.hasParent()) widget.add(entry).fill().row();
			}
			//
			for (var c : widget.getCells()) {
			    c.fill().row();
			}
	    });
	}
}