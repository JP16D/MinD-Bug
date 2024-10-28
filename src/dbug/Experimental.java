package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		test.x = (float) dw(Float.class, "pre-local-x", () -> test.x).get();
		test.y = (float) dw(Float.class, "pre-local-y", () -> test.y).get();
		test = (Vec2) dw(Vec2.class, "localTest", () -> test).get();
		test.x = (float) dw(Float.class, "post-local-x", () -> test.x).get();
		test.y = (float) dw(Float.class, "post-local-y", () -> test.y).get();
		dv("test-vx", () -> test.x);
		dv("test-vy", () -> test.y);
	}
}