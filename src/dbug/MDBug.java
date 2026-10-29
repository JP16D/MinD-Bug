package dbug;

import arc.*;
import mindustry.game.*;
import mindustry.mod.*;

import dbug.ui.*;

public class MDBug extends Mod {

	public MDBug() {
		Events.run(EventType.Trigger.update, () -> {
			MDBugTest.load();
			MainPanel.scale();
		});
	}
	
	@Override
	public void init() {
		Core.scene.add(MainPanel.load());
	}
}
