/*package dbug.tool;

import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.ui.*;
import java.lang.reflect.*;

import static dbug.MDBugVars.*;

public class Compound extends Debuggable {
	protected OrderedMap<String, Debuggable> components = new OrderedMap<>();
	protected OrderedMap<String, Debuggable> archive = new OrderedMap<>();
	protected OrderedMap<String, Field> fields = new OrderedMap<>();
	//
	boolean revert;
	
	public Compound(Class<?> type, Prov<?> val) {
		super(type, val);
		//
		set(type, val);
		archive.putAll(components.copy());
	}
	
	@Override
	public void set(Class<?> type, Prov<?> val) {
		this.type = type;
		this.value = val;
		//
		var fields = type.getFields();
		//
		for (var c : fields) {
			if (Modifier.isFinal(c.getModifiers())) continue;
			var v = new Debuggable(c.getDeclaringClass(), () -> {
				try {
					return c.get(val.get());
				} catch (Exception e) {
					return null;
				}
			});
			//
			components.put(c.getName(), v);
		}
	}
	
	public void prioritize(Compound p) {
		if (!type.isInstance(p)) return;
		//
		if (priority) {
			for (var k : archive.keys()) {
				var v = archive.get(k);
				components.put(k, v);
				//
				try {
					fields.get(k).set(value.get(), v);
				} catch (Exception e) {
					//warn();
				}
			}
			//
			priority = false;
			//
		} else if
		//
		p.set(type, value);
	}
	
	public Table actor() {
		return new Table(t -> {
			for (var k : archive.keys()) {
				var v = archive.get(k);
				//
				t.add(Debugger.display(Color.darkGray, k, new Table(comp -> {
					comp.field(v.value.get().toString(), Styles.defaultField, (String txt) -> {
						//
						v.parse(v.type, txt);
						//
					}).center().pad(4f);
				}))).left().pad(4f).row();
			}
			//
			t.button("Set", () -> priority = true).right().pad(2f);
			t.button("×", () -> revert = true).right().pad(2f);
		}) ;
	}
}*/