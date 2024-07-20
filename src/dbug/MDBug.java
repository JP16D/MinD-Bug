package dbug;

import blui.ui.*;
import mindustry.mod.*;

public class MDBug extends Mod {
	public static Debugger debugger = new Debugger();
	
	public MDBug() {
		//debugger.load();
	}
	
	@Override
	public void init() {
		BLSetup.addTable(table -> table.pane(debugger));
	}
}