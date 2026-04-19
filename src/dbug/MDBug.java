package dbug;

import arc.*;
import blui.*;
import blui.ui.*;
import dbug.tool.*;
import mindustry.game.*;
import mindustry.mod.*;

import static dbug.ui.MainPanel.*;

public class MDBug extends Mod {
	public static Debugger debugger = new Debugger();
	//
	private static Experimental exp = new Experimental();
	//
	public MDBug() {
		Events.run(EventType.Trigger.update, () -> scale());
	}
	
	@Override
	public void init() {
		BLSetup.addTable(table -> {
			init(table);
			exp.loadTest();
		});
	}
}