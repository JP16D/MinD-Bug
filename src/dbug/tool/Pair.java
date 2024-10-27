package dbug.tool;

public class Pair<V1, V2>  {
	public V1 v1;
	public V2 v2;
	//
	public Pair(V1 val1, V2 val2) {
		set(val1, val2);
	}
	
	public Pair set(V1 val1, V2 val2) {
		v1 = val1;
		v2 = val2;
		//
		return this;
	}
}