package dbug;

import arc.*;
import arc.scene.ui.*;
import blui.*;
import blui.ui.*;
import mindustry.gen.*;
import mindustry.mod.*;

public class MDBug extends Mod {
	public static Debugger debugger = new Debugger();
	public static boolean expand = false;
	
	public MDBug() {
		debugger.load();
	}
	
	@Override
	public void init() {
		BLSetup.addTable(table -> {
			debugger.update();
			table.table(Tex.pane, t -> {
				t.button(Icon.elevation, () -> {
					expand = !expand;
				});
				//
				var p = t.pane(debugger);
				p.size(360f, expand ? Core.scene.getHeight * 0.5f : BLVars.iconSize);
				p.get().setScrollingDisabledX(true);
			});
		});
	}
}