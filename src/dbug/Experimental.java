package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		test = (Vec2) dw(Vec2.class, "localTest", () -> test).get();
		test.x = (float) dw(float.class, "local-x", () -> test.x).get();
		test.y = (float) dw(float.class, "local-y", () -> test.y).get();
	}
}