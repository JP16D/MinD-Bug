package dbug;

import arc.math.geom.*;

import static dbug.tool.Debugger.*;

public class Experimental {
	public Vec2 test = new Vec2();
	public float ntest = 0;
	
	public void loadTest() {
		ntest = (float) dw(Float.class, "local-n", ntest);
		test = (Vec2) dw(Vec2.class, "localTest", test);
		dv("test-vx", test.x);
		dv("test-vy", test.y);
	}
}