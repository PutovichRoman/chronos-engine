package internal.objects;

public interface Attachable {
    default void onAttach() {}
    default void onDetach() {}
}
