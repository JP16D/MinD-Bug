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
	public Debuggable(String type, String val) {
		var old = value;
		//
		if (type == "String") {
			this.type = String.class;
			value = () -> val;
			//
		} else {
			try {
				this.type = Class.forName(type);
				//
				var method = this.type.getMethod("valueOf", String.class);
				var sample = this.type.getConstructor().newInstance();
				//
				value = () -> {
					try {
						return this.type.cast(method.invoke(sample, val));
					} catch (Exception e) {
						return old;
						//warn();
					}
				};
				//
			} catch (Exception e) {
				//Impossible
			}
		}
	}
}