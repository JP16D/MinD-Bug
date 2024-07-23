package dbug.tool;

import arc.func.*;
import java.lang.*;

public class Debuggable {
	public Prov<?> value;
	public Class<?> type;
	
	public Debuggable(Class<?> type, Prov<?> val) {
		this.value = val;
		this.type = type;
	}
	
	//some sort of parsing shenanigans
	public Debuggable(Class<?> type, String val) {
		var old = this.value;
		//
		if (type == String.class) {
			this.type = String.class;
			this.value = () -> val;
			//
		} else {
			try {
				this.type = type;
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
				//Primitives only
			}
		}
	}
}