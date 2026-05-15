package internal.lib.object_pool;

public interface Reusable {
    /**
     * Сбрасывает объект в исходное состояние для повторного использования.
     */
    void reset();

    /**
     * Вызывается при извлечении из пула
     */
    default void onAcquire() {}

    /**
     * Вызывается при возвращении в пул
     */
    default void onRelease() {}
}
