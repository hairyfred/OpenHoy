package uk.hairyfred.openhoy.model

/** How many jokers (and of what flavour) to put in the shuffled deck. */
enum class JokerMode {
    /** No jokers — standard 52-card deck. */
    NONE,

    /** A single plain joker — 53 cards. */
    ONE,

    /** Two jokers, one red and one black — 54 cards. */
    TWO_COLOURS,
}
