package dbug;

import blui.ui.*;
import mindustry.mod.*;

public class MDBug extends Mod {
	public static Debugger debugger = new Debugger();
	
	public MDBug() {
		//debugger.load();
	}
	
	public static void init() {
		BLSetup.addTable(debugger);
	}
}