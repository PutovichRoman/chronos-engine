package internal;

import internal.objects.Camera2D;
import org.lwjgl.glfw.*;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public abstract class Input {
    private static final boolean[] KEYS_PRESSED = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] KEYS_RELEASED = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] MOUSE_BUTTON_PRESSED = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] MOUSE_BUTTON_RELEASED = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private static GLFWKeyCallback keyCallback;
    private static GLFWMouseButtonCallback mouseCallback;
    private static GLFWCursorPosCallback cursorCallback;
    private static GLFWCharCallback charCallback;

    private static GLFWScrollCallback scrollCallback;
    private static float scrollX;
    private static float scrollY;

    private static final StringBuilder input = new StringBuilder(16);
    private static String inputText;

    private static float cursorX, cursorY;
    private static float localCursorX, localCursorY;

    static void setupCallbacks() {
        keyCallback = glfwSetKeyCallback(Engine.window.id, (windowHandle, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                KEYS_PRESSED[key] = true;
            } else if (action == GLFW_RELEASE) {
                KEYS_RELEASED[key] = true;
            }
        });
        mouseCallback = glfwSetMouseButtonCallback(Engine.window.id, (windowHandle, button, action, mods) -> {
            if (action == GLFW_PRESS) {
                MOUSE_BUTTON_PRESSED[button] = true;
            } else if (action == GLFW_RELEASE) {
                MOUSE_BUTTON_RELEASED[button] = true;
            }
        });
        cursorCallback = glfwSetCursorPosCallback(Engine.window.id, (w, xpos, ypos) -> {
            var vp = Engine.window.viewport;
            var camera = Camera2D.getCurrent();

            Mouse.x = (float) xpos;
            Mouse.y = (float) ypos;

            float localX = (float) (xpos - vp.getX());
            float localY = (float) (ypos - vp.getY());
            float scaleX = camera.scale.x / (float) vp.getWidth();
            float scaleY = camera.scale.y / (float) vp.getHeight();

            localCursorX = Math.clamp(localX * scaleX - camera.scale.x / 2f, -camera.scale.x / 2f, camera.scale.x / 2f);
            localCursorY = -Math.clamp(localY * scaleY - camera.scale.y / 2f, -camera.scale.y / 2f, camera.scale.y / 2f);
        });
        scrollCallback = glfwSetScrollCallback(Engine.window.id, (windowHandle, xOffset, yOffset) -> {
            scrollX = (float) xOffset;
            scrollY = (float) yOffset;
        });
        charCallback = new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                input.append(Character.toChars(codepoint));
            }
        };
        glfwSetCharCallback(Engine.window.id, charCallback);

        /*GLFW.glfwSetJoystickCallback((joystickId, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                if (Engine.SHOW_DEBUG) {
                    System.out.println(Console.greenText("Joystick " + joystickId + " connected"));
                }
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                System.out.println(Console.greenText("Joystick " + joystickId + " disconnected"));
            }
        });*/
    }

    public static GLFWCursorPosCallback getCursorCallback() {
        return cursorCallback;
    }

    public static GLFWMouseButtonCallback getMouseCallback() {
        return mouseCallback;
    }

    public static GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    public static GLFWCharCallback getCharCallback() {
        return charCallback;
    }

    public static GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    static void update() {
        Arrays.fill(KEYS_PRESSED, false);
        Arrays.fill(KEYS_RELEASED, false);
        Arrays.fill(MOUSE_BUTTON_PRESSED, false);
        Arrays.fill(MOUSE_BUTTON_RELEASED, false);
        input.setLength(0);
        inputText = null;
        scrollX = 0;
        scrollY = 0;

        var camera = Camera2D.getCurrent();
        cursorX = getLocalCursorX() * (1f / camera.zoom);
        cursorY = getLocalCursorY() * (1f / camera.zoom);
    }

    public static String getText() {
        if (inputText == null) {
            inputText = input.toString();
            return inputText;
        }
        return inputText;
    }

    public static boolean isPressed(Key key) {
        return KEYS_PRESSED[key.id];
    }

    public static boolean isHeld(Key key) {
        return glfwGetKey(Engine.window.id, key.id) == GLFW_PRESS;
    }

    public static boolean isHeld(MouseButton button) {
        return glfwGetKey(Engine.window.id, button.id) == GLFW_PRESS;
    }

    public static boolean isHeld(String key) {
        key = key.toUpperCase();
        if (key.equals("MOUSE_LEFT")) {
            return glfwGetKey(Engine.window.id, MouseButton.LEFT.id) == GLFW_PRESS;
        } else if (key.equals("MOUSE_RIGHT")) {
            return glfwGetKey(Engine.window.id, MouseButton.RIGHT.id) == GLFW_PRESS;
        } else if (key.equals("MOUSE_MIDDLE")) {
            return glfwGetKey(Engine.window.id, MouseButton.MIDDLE.id) == GLFW_PRESS;
        }
        return KEYS_RELEASED[Key.valueOf(key).id];
    }

    public static boolean isReleased(Key key) {
        return KEYS_RELEASED[key.id];
    }

    public static boolean isPressed(MouseButton button) {
        return MOUSE_BUTTON_PRESSED[button.id];
    }

    public static boolean isPressed(String key) {
        key = key.toUpperCase();
        if (key.equals("MOUSE_LEFT")) {
            return MOUSE_BUTTON_PRESSED[MouseButton.LEFT.id];
        } else if (key.equals("MOUSE_RIGHT")) {
            return MOUSE_BUTTON_PRESSED[MouseButton.RIGHT.id];
        } else if (key.equals("MOUSE_MIDDLE")) {
            return MOUSE_BUTTON_PRESSED[MouseButton.MIDDLE.id];
        }
        return KEYS_PRESSED[Key.valueOf(key).id];
    }

    public static boolean isReleased(String key) {
        key = key.toUpperCase();
        if (key.equals("MOUSE_LEFT")) {
            return MOUSE_BUTTON_RELEASED[MouseButton.LEFT.id];
        } else if (key.equals("MOUSE_RIGHT")) {
            return MOUSE_BUTTON_RELEASED[MouseButton.RIGHT.id];
        } else if (key.equals("MOUSE_MIDDLE")) {
            return MOUSE_BUTTON_RELEASED[MouseButton.MIDDLE.id];
        }
        return KEYS_RELEASED[Key.valueOf(key).id];
    }

    public static boolean isReleased(MouseButton button) {
        return MOUSE_BUTTON_RELEASED[button.id];
    }

    public static float getCursorX() {
        return cursorX;
    }

    public static float getCursorY() {
        return cursorY;
    }

    public static float getLocalCursorX() {
        return localCursorX + Camera2D.getCurrent().position.x;
    }

    public static float getLocalCursorY() {
        return localCursorY + Camera2D.getCurrent().position.y;
    }

    public static float getScrollX() {
        return scrollX;
    }

    public static float getScrollY() {
        return scrollY;
    }

    public enum Key {
        UP(GLFW.GLFW_KEY_UP),
        DOWN(GLFW.GLFW_KEY_DOWN),
        LEFT(GLFW.GLFW_KEY_LEFT),
        RIGHT(GLFW.GLFW_KEY_RIGHT),
        A(GLFW.GLFW_KEY_A),
        B(GLFW.GLFW_KEY_B),
        C(GLFW_KEY_C),
        D(GLFW_KEY_D),
        E(GLFW_KEY_E),
        F(GLFW_KEY_F),
        G(GLFW_KEY_G),
        H(GLFW_KEY_H),
        I(GLFW_KEY_I),
        J(GLFW_KEY_J),
        K(GLFW_KEY_K),
        L(GLFW_KEY_L),
        M(GLFW_KEY_M),
        N(GLFW_KEY_N),
        O(GLFW_KEY_O),
        P(GLFW_KEY_P),
        Q(GLFW_KEY_Q),
        R(GLFW_KEY_R),
        S(GLFW_KEY_S),
        T(GLFW_KEY_T),
        U(GLFW_KEY_U),
        V(GLFW_KEY_V),
        W(GLFW_KEY_W),
        X(GLFW_KEY_X),
        Y(GLFW_KEY_Y),
        Z(GLFW_KEY_Z),

        ENTER(GLFW_KEY_ENTER),
        ESCAPE(GLFW_KEY_ESCAPE),
        BACKSPACE(GLFW_KEY_BACKSPACE),
        LEFT_SHIFT(GLFW_KEY_LEFT_SHIFT),
        RIGHT_SHIFT(GLFW_KEY_RIGHT_SHIFT);

        final int id;

        Key(int id) {
            this.id = id;
        }
    }

    public enum MouseButton {
        LEFT(GLFW_MOUSE_BUTTON_LEFT),
        RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
        MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE);

        final int id;

        MouseButton(int id) {
            this.id = id;
        }
    }
}
