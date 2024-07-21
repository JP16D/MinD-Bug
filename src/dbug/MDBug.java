package dbug;

import arc.*;
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
			var expand = debugger.expand;
			//
			var display = new ScrollPane(main);
			display.setClamp(true);
			//
			table(Tex.pane, t -> {
				t.add(display).size(360f, expand ? Core.scene.getHeight() * 0.25f : 40f);
				//
				t.button(expand ? Icon.down : Icon.up, () -> {
					debugger.expand = !expand;
					table.clearChildrend();
				});
			});
		});
	}
}