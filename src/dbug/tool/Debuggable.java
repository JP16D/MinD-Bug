package dbug.tool;

import arc.func.*;

import java.lang.*;

public class Debuggable {
	public boolean priority;
	public Prov<?> value;
	public Class<?> type;
	
	public Debuggable(Class<?> type, Prov<?> val) {
		value = val;
		this.type = type;
	}
	
	//some sort of parsing shenanigans
	public Debuggable parse(Class<?> type, String val) {
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
						return value;
						//warn();
					}
				};
				//
			} catch (Exception e) {
				//Primitives only!
			}
		}
		//
		priority = true;
		return this;
	}
	
	public void set(Class<?> type, Prov<?> val) {
		value = val;
		this.type = type;
	}
}