package com.bergerkiller.bukkit.common.config.yaml;

/**
 * A path to a node or value inside a YAML configuration tree.
 * Each path consists of the name of the last part of the path, and
 * a recursive parent path.
 */
public final class YamlPath {
    /**
     * Root yaml path with an empty name and no parent path
     */
    public static final YamlPath ROOT = new YamlPath();

    private final YamlPath parent;
    private final String name;
    private final int hashcode;
    private final int depth;

    // Constructor for ROOT
    private YamlPath() {
        this.parent = null;
        this.name = "";
        this.hashcode = 0;
        this.depth = 0;
    }

    // Constructor used in the static factory methods, parent cannot be null
    private YamlPath(YamlPath parent, String name) {
        this.parent = parent;
        this.name = name;
        this.hashcode = this.name.hashCode() + 31 * this.parent.hashcode;
        this.depth = this.parent.depth + 1;
    }

    /**
     * Creates a new YamlPath using a .-delimited path string.
     * 
     * @param path
     * @return creates yaml path
     */
    public static YamlPath create(String path) {
        return createChild(ROOT, path);
    }

    private static YamlPath createListChild(YamlPath parent, int listIndex) {
        return new YamlPath(parent, "[" + listIndex + "]");
    }

    private static YamlPath createChild(YamlPath parent, String path) {
        if (path.isEmpty()) {
            // If path is empty, then return the parent path
            return parent;
        } else {
            // Turn .-delimited parts in the path into additional recursive parents
            int startIndex = 0;
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                if (c == '.') {
                    parent = createChild(parent, path.substring(startIndex, i));
                    startIndex = i + 1;
                } else if (c == '[') {
                    parent = createChild(parent, path.substring(startIndex, i));
                    startIndex = i;
                }
            }

            // Create the path
            return new YamlPath(parent, path.substring(startIndex));
        }
    }

    /**
     * Gets whether this is a root path, which is equivalent to an
     * empty path String. This will be the {@link #ROOT} instance.
     * There are no deeper parents.
     * 
     * @return True if this is a root path
     */
    public boolean isRoot() {
        return this == ROOT;
    }

    /**
     * Gets whether this path refers to a list element, which means
     * the name contains an index. For example: [12]
     * 
     * @return True if this is a path to a list element
     */
    public boolean isList() {
        return this.name.length() > 2 &&
               this.name.charAt(0) == '[' &&
               this.name.charAt(this.name.length()-1) == ']';
    }

    /**
     * Gets the index to the parent node's list this path refers to.
     * If this is not a list as indicated by {@link #isList()} then this
     * method returns -1.
     * 
     * @return list index
     */
    public int listIndex() {
        if (this.isList()) {
            try {
                return Integer.parseInt(this.name.substring(1, this.name.length()-1));
            } catch (NumberFormatException ex) {}
        }
        return -1;
    }

    /**
     * Gets the parent path of this YamlPath
     * 
     * @return parent YamlPath, null if this is a root path
     */
    public YamlPath parent() {
        return this.parent;
    }

    /**
     * Gets a child path of this path. The child path can contain
     * multiple path parts to refer to a deeper child. If the child
     * path is empty, this path is returned instead.
     * 
     * @param childPath The path to the child
     * @return child path
     */
    public YamlPath child(String childPath) {
        return createChild(this, childPath);
    }

    /**
     * Gets a child path of this path. The child path can contain
     * multiple path parts to refer to a deeper child. If the child
     * path is root, this path is returned instead.
     * 
     * @param childPath The YamlPath to append to this path to make a child
     * @return child path
     */
    public YamlPath child(YamlPath childPath) {
        if (childPath.isRoot()) {
            return this;
        } else {
            return child(childPath.parent()).child(childPath.name());
        }
    }

    /**
     * Creates a child path of this path to a list element
     * 
     * @param listIndex The index of the element in the list
     * @return list child path
     */
    public YamlPath listChild(int listIndex) {
        return createListChild(this, listIndex);
    }

    /**
     * Gets the name of the last part of this YamlPath
     * 
     * @return name
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the depth of this path, which is the amount of
     * sub-nodes below this path.
     * 
     * @return depth, 0 if {@link #parent()} == null
     */
    public int depth() {
        return this.depth;
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof YamlPath) {
            YamlPath p1 = this;
            YamlPath p2 = (YamlPath) o;
            if (p1.depth() == p2.depth()) {
                while (p1.name.equals(p2.name)) {
                    p1 = p1.parent();
                    p2 = p2.parent();
                    if (p1 == p2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.isRoot()) {
            return "";
        } else {
            // Child of root
            StringBuilder str = new StringBuilder(this.name);        
            YamlPath path = this;
            while (!path.parent.isRoot()) {
                if (!path.isList()) {
                    str.insert(0, '.');
                }
                path = path.parent;
                str.insert(0, path.name);
            }
            return str.toString();
        }
    }
}
