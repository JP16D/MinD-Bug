package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		dv("test-x", test.x);
		dv("test-y", test.y);
		dv("test", test);
		test.x = (float) dw("test-vx", test.x);
		test.y = (float) dw("test-vy", test.y);
		test = (Vec2) dw("test-v", test);
	}
}