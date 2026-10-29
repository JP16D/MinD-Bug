package dbug;

import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;

import static dbug.tool.Debugger.*;

public class MDBugTest {
	
	@Debug("(view)test-v")
	@Debug("(write)test-w")
	public static Vec2 test = new Vec2();
	//
	@Guarded
	@Debug("(writable):5")
	public static Table table;
	
	public static void load() {
		debug("test-vx", test.x);
		debug("test-vy", test.y);
		//
		test.x = debug("(w)test-wx", test.x);
		test.y = debug("(w)test-wy", test.y);
		//
		register(MDBugTest.class);
		//
		for (var f : Icon.class.getFields()) try {
			if (f.get(new Icon()) instanceof Drawable d)
			debug("icon-" + f.getName(), d);
		} catch (Exception e) {}
	}
}