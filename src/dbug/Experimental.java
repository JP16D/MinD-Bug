package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		dv("test-ax", test.x);
		dv("test-ay", test.y);
		test.x = (float) dw(float.class, "local-x", test.x);
		test.y = (float) dw(float.class, "local-y", test.y);
		test = (Vec2) dw(Vec2.class, "localTest", test);
		dv("test-bx", test.x);
		dv("test-by", test.y);
	}
}