package dbug.tool;

import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;

public class Builder {
	
	static final Seq<Runnable> functions = new Seq<>();
	
	public Table run() {
		return new Table(t -> {
			t.button("Add", Icon.add, () -> {
				//Something something W.I.P.
			});
		});
	} 
}