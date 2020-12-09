package com.foreveross.atwork.infrastructure.newmessage.messageEnum;

public enum TemplateAlign {
    left {
        @Override
        public String stringValue() {
            return LEFT;
        }
    },
    right {
        @Override
        public String stringValue() {
            return RIGHT;
        }
    },
    center {
        @Override
        public String stringValue() {
            return CENTER;
        }
    };

    public abstract String stringValue();

    public static TemplateAlign fromStringValue(String value) {
        if (LEFT.equalsIgnoreCase(value)) {
            return left;
        }

        if (RIGHT.equalsIgnoreCase(value)) {
            return right;
        }

        if (CENTER.equalsIgnoreCase(value)) {
            return center;
        }
        return left;
    }

    public static final String LEFT = "left";

    public static final String RIGHT = "right";

    public static final String CENTER = "center";

}