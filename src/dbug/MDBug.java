package dbug;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import blui.*;
import blui.ui.*;
import dbug.tool.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;

public class MDBug extends Mod {
	public static Debugger debugger = new Debugger();
	//
	private Table caller;
	private boolean expand;
	private float scale;
	
	public MDBug() {
		Events.run(EventType.Trigger.update, () -> {
			MDBug
			//
			if (scale != Core.scene.getHeight()) {
				scale = Core.scene.getHeight();
				//
				 if (caller != null) {
				 	caller.clearChildren();
					call(caller);
				 }
			}
		});
	}
	
	@Override
	public void init() {
		BLSetup.addTable(table -> {
			var display = new ScrollPane(this);
			display.setClamp(true);
			//
			table.table(Tex.pane, t -> {
				t.button(expand ? Icon.downOpen : Icon.upOpen, () -> {
					expand = !expand;
					table.clearChildren();
					call(table);
					return;
				}).padRight(8f).top();
				//
				t.add(display).size(240f, expand ? scale * 0.25f : 52f);
			});
			//
			caller = table;
		});
	}
}