package dbug.tool;

import arc.func.*;
import java.lang.*;

public class Debuggable {
	public Prov<?> value;
	public Class<?> type;
	
	public Debuggable(Class<?> type, Prov<?> val) {
		value = val;
		this.type = type;
	}
	
	//some sort of parsing shenanigans
	public Debuggable(Class<?> type, String val) {
		var old = this.value;
		//
		if (type == String.class) {
			this.type = String.class;
			value = () -> val;
			//
		} else {
			try {
				this.type = type;
				//
				var method = this.type.getMethod("valueOf", String.class);
				//
				value = () -> {
					try {
						return method.invoke(type, val);
					} catch (Exception e) {
						return old;
						//warn();
					}
				};
				//
			} catch (Exception e) {
				//Primitives only!
			}
		}
	}
}