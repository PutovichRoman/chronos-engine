package internal.data;

import internal.exceptions.ResourceException;

public abstract class Resource implements Disposable {
    boolean isDisposed;

    @Override
    public void dispose() {
        checkNotDisposed();
        isDisposed = true;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
