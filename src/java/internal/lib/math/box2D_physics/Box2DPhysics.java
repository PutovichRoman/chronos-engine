package internal.lib.math.box2D_physics;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public abstract class Box2DPhysics {
    private static boolean isInitialized;

    static final MethodHandle B2_CREATE_WORLD = Linker.nativeLinker().downcallHandle(
            SymbolLookup.loaderLookup().find("b2CreateWorld").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS)
    );

    public static void init() {
        if (isInitialized) return;

    }
}
