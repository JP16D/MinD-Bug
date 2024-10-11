package dbug.tool;

import arc.func.*;

import java.lang.*;

public class Debuggable {
	protected boolean priority;
	public Prov<?> value;
	public Class<?> type;
	
	public Debuggable(Class<?> type, Prov<?> val) {
		value = val;
		this.type = type;
	}
	
	//some sort of parsing shenanigans
	public Debuggable parse(Class<?> type, String val) {
		//
		if (this.type == type) if (type == String.class) {
			value = () -> val;
			//
		} else {
			try {
				//
				var method = type.getMethod("valueOf", String.class);
				//
				if (!type.isInstance(method.getReturnType())) {
					value = () -> {
						try {
							return method.invoke(type, val);
						} catch (Exception e) {
							return value;
							//warn();
						}
					};
				}
			} catch (Exception e) {
				//warn();
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