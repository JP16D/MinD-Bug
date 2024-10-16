package dbug;

import arc.math.geom.*;
import dbug.tool.*;

public class Experimental {
	public Vec2 test = new Vec2();
	
	public void loadTest() {
		test = (Vec2) dwo(Vec2.class, "localTest", () -> test).get();
	}
}