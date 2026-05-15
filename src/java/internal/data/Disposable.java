package internal.data;

import internal.exceptions.ResourceException;

public interface Disposable {
    boolean THROWING_EXCEPTION = true;

    default void checkNotDisposed() {
        if (THROWING_EXCEPTION && isDisposed()) {
            throw new ResourceException(this.getClass().getSimpleName() + " is already disposed");
        }
    }

    void dispose();

    boolean isDisposed();
}
