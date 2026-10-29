package dbug.util;

import java.util.*;
import java.util.regex.*;

public class Identity {
    
    private static final Pattern IDENTITY_PATTERN = Pattern.compile("^\\s*(?:\\((.*?)\\))?\\s*(.*?)(?::(\\d+))?\\s*$");
	//
	public final String name;
	public final boolean writable;
	public final int depth;

	Identity(String name, boolean writable, int depth) {
		this.name = name;
		this.writable = writable;
		this.depth = depth;
	}

    /**
     * Parses debugger identity queries by matching the pattern: `(mode)name:depth`
     * <br>- mode: writable|write|w for writable mode, viewable|view|v|read|r for explicit view-only mode, view-only by default
     * <br>- name: identifier used in the entries, leaving empty to use the variable name
     * <br>- depth: complex object inspection depth, any positive integer above 0
     */
    public static Identity parse(String identity) {
        if (identity == null) {
            throw new IllegalArgumentException("Identity string cannot be null.");
        }
        
        Matcher matcher = IDENTITY_PATTERN.matcher(identity);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid debug identity pattern: " + identity);
        }
        
        var modifier = matcher.group(1);
        var cleanName = matcher.group(2);
        var depthStr = matcher.group(3);
        
        boolean writable = false;
        if (modifier != null) {
            String modLower = modifier.trim().toLowerCase();
            if (Set.of("writable", "write", "w").contains(modLower)) {
                writable = true;
            } else if (Set.of("viewable", "view", "v", "read", "r").contains(modLower)) {
                writable = false;
            } else {
                throw new IllegalArgumentException("Unknown or illegal modifier '(" + modifier + ")' in identity: " + identity);
            }
        }
        
        int depth = 3; // Default max depth
        if (depthStr != null) {
            depth = Integer.parseInt(depthStr);
            if (depth <= 0) {
                throw new IllegalArgumentException("Inspect depth must be a positive integer greater than 0 in identity: " + identity);
            }
        }
        
        return new Identity(cleanName.trim(), writable, depth);
    }
}