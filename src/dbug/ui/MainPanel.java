package dbug.ui;

import arc.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.struct.*;
import mindustry.gen.*;

import static dbug.tool.Debugger.*;

public class MainPanel {

    private static final OrderedMap<String, Table> map = new OrderedMap<>();
    private static final Vec2 scale = new Vec2();
	//
    private static Table container;
	private static ImageButton expander;
	private static ScrollPane panel;
	//
	private static boolean expand;
	
	public static void init() {
		if (panel != null) return;
		expander = Elem.newImageButton(Icon.upOpen, () -> {
			expand = !expand;
			update();
		});
		expander.setBackground(null);
		//
		panel = new ScrollPane(new Table(), new ScrollPane.ScrollPaneStyle(){{
			background = Tex.pane;
		}});
		panel.setOverscroll(false, true);
		panel.setClamp(true);
	}
	
	public static Table load() {
		MainPanel.container = new Table();
		init();
		//
		container.add(panel).center().row();
		container.add(expander).center().row();
		//
		update();
		return container;
	}
	
	public static void scale() {
		if (scale.x != Core.scene.getHeight()) {
			scale.set(Core.scene.getWidth(), Core.scene.getHeight());
			//
			update();
		}
	}
	
	public static void update() {
		if (container == null) return;
	    container.getCell(panel).size(Math.max(scale.x * 0.4f, 300f), expand ? scale.y * 0.8f : 32f);
		container.getCell(expander).size(64f, 48f);
		container.setPosition(scale.x * 0.5f, scale.y - (expand ? panel.getHeight() * 0.5f + 20f : 0f));
	    //
		expander.replaceImage(new Image(expand ? Icon.upOpen : Icon.downOpen));
	    //
		var widget = (Table) panel.getWidget();
		widget.update(() -> {
			for (var k : entries.keys()) {
				if (!map.containsKey(k)) map.put(k, entries.get(k).show());
		        //
				var entry = map.get(k);
				if (!entry.hasParent()) widget.add(entry).row();
			}
		});
	}
}