package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		test = (Vec2) dw(Vec2.class, "localTest", () -> test).get();
	}
}