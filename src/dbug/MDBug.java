package dbug;

import arc.scene.ui.*;
import blui.*;
import blui.ui.*;
import mindustry.gen.*;
import mindustry.mod.*;

public class MDBug extends Mod {
	public static Debugger debugger = new Debugger();
	
	public MDBug() {
		debugger.load();
	}
	
	@Override
	public void init() {
		BLSetup.addTable(table -> {
			debugger.update();
			table.table(Tex.pane, t -> {
				var p = t.pane(new ScrollPane(debugger) {{
					setScrollingDisabledY(true);
				}});
				p.size(360f, BLVars.iconSize);
				p.get().setScrollingDisabledX(true);
			});
		});
	}
}