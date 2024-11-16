package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		test.x = (float) dw(Float.class, "local-x", test.x);
		test.y = (float) dw(Float.class, "local-y", test.y);
		test = (Vec2) dw(Vec2.class, "localTest", test);
		dv("test-vx", test.x);
		dv("test-vy", test.y);
	}
}