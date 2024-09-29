package dbug;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import dbug.tools.*;

public class MDBugVars {
	public static final OrderedMap<String, Table> debugger = new OrderedMap<>();
	public static final OrderedMap<String, Builder> builder = new OrderedMap<>();
}