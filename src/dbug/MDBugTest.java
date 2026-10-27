package dbug;

import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;

import static dbug.tool.Debugger.*;

public class MDBugTest {
    // Registered automatically via annotation
	@Debuggable(name = "test-v")
    @Debuggable(name = "test-w", writable = true)
    public static Vec2 test = new Vec2();

	@Debuggable(writable = true)
	public static Table table = new Table();
    
    public static void load() {
        register(MDBugTest.class);
        
        dv("test-x", test.x);
        dv("test-y", test.y);
        
        test.x = dw("test-vx", test.x);
        test.y = dw("test-vy", test.y);
        
        for (var f : Icon.class.getFields()) {
            try {
                if (f.get(new Icon()) instanceof Drawable d) {
                    dv("icon-" + f.getName(), d);
                }
            } catch (Exception e) {}
        }
    }
}