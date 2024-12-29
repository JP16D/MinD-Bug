package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		dv("test-ax", test.x);
		dv("test-ay", test.y);
		test.x = (float) dw("local-x", test.x);
		test.y = (float) dw("local-y", test.y);
		test = (Vec2) dw("localTest", test);
		dv("test-bx", test.x);
		dv("test-by", test.y);
	}
}