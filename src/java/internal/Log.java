package internal;

import org.fusesource.jansi.Ansi;

import java.util.List;

public final class Log {
    public static final boolean DEBUG_ENABLE = Boolean.parseBoolean(System.getProperty("chronos.log.debug", "false"));
    public static final boolean ERROR_ENABLE = Boolean.parseBoolean(System.getProperty("chronos.log.error", "true"));
    public static final boolean INFO_ENABLE = Boolean.parseBoolean(System.getProperty("chronos.log.info", "false"));
    public static final boolean WARN_ENABLE = Boolean.parseBoolean(System.getProperty("chronos.log.warn", "true"));

    public static final Log global = new Log(null);
    private final Class<?> clazz;

    private Ansi infoAnsi() {
        return Ansi.ansi().fgRgb(51, 155, 250);
    }

    private Ansi errorAnsi() {
        return Ansi.ansi().fg(Ansi.Color.RED);
    }

    private Ansi warnAnsi() {
        return Ansi.ansi().fg((Ansi.Color.YELLOW));
    }

    private Ansi debugAnsi() {
        return Ansi.ansi().fgRgb(128, 128, 128);
    }

    public Log(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Log() {
        this(null);
    }

    public void info(String message, Object... args) {
        if (INFO_ENABLE) {
            System.out.println(infoAnsi().a("[INFO] ").a(format(message, args)).reset());
        }
    }

    public void warn(String message, Object... args) {
        if (WARN_ENABLE) {
            System.out.println(warnAnsi().a("[WARNING] ").a(format(message, args)).reset());
        }
    }

    public void error(String message, Object... args) {
        if (ERROR_ENABLE) {
            System.out.println(errorAnsi().a("[ERROR] ").a(format(message, args)).reset());
        }
    }

    public void debug(String message, Object... args) {
        if (DEBUG_ENABLE) {
            System.out.println(debugAnsi().a("[DEBUG] ").a(format(message, args)).reset());
        }
    }

    private String format(String message, Object... args) {
        int length = message.length();
        for (Object arg : args) {
            length += arg.toString().length();
        }
        if (clazz != null) {
            length += clazz.getSimpleName().length() + ": ".length();
        }

        StringBuilder stringBuilder = new StringBuilder(length);
        if (clazz != null) {
            stringBuilder.append(clazz.getName());
            stringBuilder.append(": ");
        }

        int argIndex = 0;
        for (int charIndex = 0; charIndex < message.length(); charIndex++) {
            if (message.charAt(charIndex) == '{') {
                int indexLength = 0;
                if (Character.isDigit(message.charAt(charIndex + 1))) {
                    charIndex++;
                    while (charIndex < message.length() && Character.isDigit(message.charAt(charIndex))) {
                        charIndex++;
                        indexLength++;
                    }
                }
                if (indexLength == 0) {
                    stringBuilder.append(args[argIndex++]);
                    charIndex++;
                } else {
                    final int index = getArgIndex(message, indexLength, charIndex);
                    stringBuilder.append(args[index]);
                }
            } else {
                stringBuilder.append(message.charAt(charIndex));
            }
        }

        return stringBuilder.toString();
    }

    private int getArgIndex(String message, int indexLength, int indexChar) {
        int index;
        if (indexLength == 1) {
            index = Character.digit(message.charAt(indexChar - 1), 10);
        } else {
            final StringBuilder sb = new StringBuilder(indexLength);
            for (int i = 0; i < indexLength; i++) {
                sb.append(message.charAt(indexChar - indexLength + i));
            }
            index = Integer.parseInt(sb.toString());
        }
        return index;
    }

    private String format(String message, List<Object> args) {
        return format(message, args.toArray());
    }

    public void info(String message, List<Object> args) {
        if (INFO_ENABLE) {
            System.out.println(infoAnsi().a("[INFO] ").a(format(message, args)).reset());
        }
    }

    public void warn(String message, List<Object> args) {
        if (WARN_ENABLE) {
            System.out.println((warnAnsi().a("[WARNING] ").a(format(message, args)).reset()));
        }
    }

    public void error(String message, List<Object> args) {
        if (ERROR_ENABLE) {
            System.out.println(errorAnsi().a("[ERROR] ").a(format(message, args)).reset());
        }
    }

    public void debug(String message, List<Object> args) {
        if (DEBUG_ENABLE) {
            System.out.println(debugAnsi().a("[DEBUG] ").a(format(message, args)).reset());
        }
    }

    public void error(String message) {
        if (ERROR_ENABLE) {
            if (clazz != null) {
                System.out.println(errorAnsi().a("[ERROR] ").a(clazz.getName()).a(": ").a(message).reset());
            } else {
                System.out.println(errorAnsi().a("[ERROR] ").a(message).reset());
            }
        }
    }

    public void info(String message) {
        if (INFO_ENABLE) {
            if (clazz != null) {
                System.out.println(infoAnsi().a("[INFO] ").a(clazz.getName()).a(": ").a(message).reset());
            } else {
                System.out.println(infoAnsi().a("[INFO] ").a(message).reset());
            }
        }
    }

    public void warn(String message) {
        if (WARN_ENABLE) {
            if (clazz != null) {
                System.out.println(warnAnsi().a("[WARNING] ").a(clazz.getName()).a(": ").a(message).reset());
            } else {
                System.out.println(warnAnsi().a("[WARNING] ").a(message).reset());
            }
        }
    }

    public void debug(String message) {
        if (DEBUG_ENABLE) {
            if (clazz != null) {
                System.out.println(debugAnsi().a("[DEBUG] ").a(clazz.getName()).a(": ").a(message).reset());
            } else {
                System.out.println(debugAnsi().a("[DEBUG] ").a(message).reset());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Logger[");
        sb.append("class=");
        if (clazz != null) {
            sb.append(clazz.getName());
        } else {
            sb.append("null");
        }
        sb.append("]");
        return sb.toString();
    }
}
