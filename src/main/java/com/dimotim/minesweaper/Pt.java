package com.dimotim.minesweaper;

public enum Pt{
    Zero{
        @Override
        public String toString() {
            return " ";
        }
    },
    One, Two, Three, Four, Five, Six, Seven, Eight,
    Mine{
        @Override
        public String toString() {
            return "M";
        }
    },
    Flag{
        @Override
        public String toString() {
            return "F";
        }
    },

    Unknown{
        @Override
        public String toString() {
            return " ";
        }
    };

    @Override
    public String toString() {
        return ordinal()+"";
    }
}
