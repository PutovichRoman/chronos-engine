package internal.lib.object_pool;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class ObjectPool<T extends Reusable> {
    private final Queue<T> activeObjects;
    private final Queue<T> freeObjects;
    private final Supplier<T> objectFactory;

    public ObjectPool(Supplier<T> supplier, int numElements) {
        activeObjects = new ArrayDeque<>(numElements);
        freeObjects = new ArrayDeque<>(numElements);
        objectFactory = supplier;
    }

    public ObjectPool(Supplier<T> supplier) {
        this(supplier, 16);
    }

    public T get() {
        T obj = freeObjects.poll();
        if (obj == null) {
            obj = objectFactory.get();
        }
        activeObjects.offer(obj);
        return obj;
    }

    public int activeCount() {
        return activeObjects.size();
    }

    public int freeCount() {
        return freeObjects.size();
    }

    public int totalCount() {
        return activeObjects.size() + freeObjects.size();
    }

    public void reclaimAll() {
        for (T obj : activeObjects) {
            obj.onRelease();
            obj.reset();
            freeObjects.offer(obj);
        }
        activeObjects.clear();
    }
}
