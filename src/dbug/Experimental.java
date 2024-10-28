package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		test = (Vec2) dw(Vec2.class, "localTest", () -> test).get();
		test.x = (float) dw(Float.class, "local-x", dv("test-vx", () -> test.x)).get();
		test.y = (float) dw(Float.class, "local-y", dv("test-vy", () -> test.y)).get();
	}
}