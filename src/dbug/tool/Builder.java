package dbug.tool;

import arc.func.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;

import java.lang.*;

public class Builder {
	default static final Seq<Runnable> functions = new Seq<>();
	
	public static Table table() {
		return new Table(t -> {
			t.button("Add", Icon.plus, () -> {
				
			});
		});
	} 
}