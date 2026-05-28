package uk.hairyfred.openhoy.model

enum class Rank(val symbol: String, val spokenName: String) {
    ACE("A", "ace"),
    TWO("2", "two"),
    THREE("3", "three"),
    FOUR("4", "four"),
    FIVE("5", "five"),
    SIX("6", "six"),
    SEVEN("7", "seven"),
    EIGHT("8", "eight"),
    NINE("9", "nine"),
    TEN("10", "ten"),
    JACK("J", "jack"),
    QUEEN("Q", "queen"),
    KING("K", "king");

    val isFaceCard: Boolean get() = this == JACK || this == QUEEN || this == KING
}
