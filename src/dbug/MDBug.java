package dbug;

import arc.*;
import blui.*;
import blui.ui.*;
import dbug.ui.*;
import mindustry.game.*;
import mindustry.mod.*;

public class MDBug extends Mod {
	public MDBug() {
		Events.run(EventType.Trigger.update, () -> {
		    MDBugTest.load();
		    MainPanel.scale();
		});
	}
	
	@Override
	public void init() {
		BLSetup.addTable(table -> {
			MainPanel.load(table);
		});
	}
}