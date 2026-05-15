package internal.objects;

import internal.Engine;
import internal.exceptions.NodeException;
import internal.lib.math.colliders.PolygonCollider2D;
import internal.objects.graphic.Drawable;

import java.util.*;

public class Node implements Attachable, UpdateListener {
    String name = getClass().getSimpleName();
    Node parent;

    final Map<String, Node> children = new HashMap<>();

    public boolean isActivatedScene() {
        return Engine.getActivatedScene() == this;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void attach(Node child) {
        if (child == null) {
            throw new NullPointerException("Child is null");
        }
        if (child.parent != null) {
            throw new NodeException("Node already have a parent");
        }
        if (child == Engine.getActivatedScene()) {
            throw new NodeException("Cannot attach root node");
        }
        if (child == this) {
            throw new NodeException("Cannot attach child to a node");
        }

        child.parent = this;
        child.onAttach();
        children.put(child.name, child);

        if (child instanceof Drawable drawable) {
            drawable.startRendering();
        }
    }

    public void detach(String name) {
        Node child = children.get(name);
        if (child == null) {
            throw new NodeException("Not found child with name " + name);
        }
        child.onDetach();
        child.parent = null;
        children.remove(name);

        if (child instanceof Drawable drawable) {
            drawable.stopRendering();
        }
    }

    public Collection<Node> getChildren() {
        if (children.isEmpty()) {
            return Collections.emptyList();
        }
        return List.copyOf(children.values());
    }

    public <T extends Node> T getChild(String name, Class<T> type) {
        return type.cast(children.get(name));
    }

    public Node getChild(String name) {
        return children.get(name);
    }

    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        if (hasParent()) {
            parent.children.remove(this.name);
            this.name = name;
            if (parent.children.containsKey(name)) {
                throw new NodeException("Node already has child with name " + name);
            }
            parent.children.put(name, this);
        } else {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node[");
        sb.append("name=").append(name);
        sb.append(']');
        return sb.toString();
    }

    public final void updateTree(float delta) {
        onUpdate(delta);
        for (Node child : children.values()) {
            child.updateTree(delta);
        }
    }
}
