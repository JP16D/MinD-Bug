package dbug.tool;

import arc.func.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;

import java.lang.*;

public class Builder {
	static final Seq<Runnable> functions = new Seq<>();
	
	public static Table table() {
		return new Table(t -> {
			t.button("Add", Icon.add, () -> {
				
			});
		});
	} 
}