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
	public void call(Table table) {
		//
		var display = new ScrollPane(new Table(t -> {
			for (var v : MDBugVars.ui.values()) {
				t.add(v).growX().pad(4f).top().row();
			}
		}));
		//
		display.setClamp(true);
		//
		table.table(Tex.pane, t -> {
			//
			t.add(display).size(240f, expand ? scale * 0.25f : 52f);
		});
		//
		table.button(expand ? Icon.downOpen : Icon.upOpen, () -> {
			expand = !expand;
			table.clearChildren();
			call(table);
			return;
		}).padRight(8f);
		//
		caller = table;
	}
	
	@Override
	public void init() {
		BLSetup.addTable(table -> {
			call(table);
		});
	}
}