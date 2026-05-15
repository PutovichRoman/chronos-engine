package internal.objects;

public interface Activatable {
    default void onActivate() {}
    default void onDeactivate() {}
}
