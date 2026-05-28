package uk.hairyfred.openhoy.model

import androidx.compose.ui.graphics.Color

enum class Suit(
    val displayName: String,
    val spokenName: String,
    val fourColour: Color,
    val classicColour: Color,
) {
    SPADES(
        displayName = "SPADES",
        spokenName = "spades",
        fourColour = Color(0xFF111111),
        classicColour = Color(0xFF111111),
    ),
    HEARTS(
        displayName = "HEARTS",
        spokenName = "hearts",
        fourColour = Color(0xFFD32F2F),
        classicColour = Color(0xFFD32F2F),
    ),
    DIAMONDS(
        displayName = "DIAMONDS",
        spokenName = "diamonds",
        fourColour = Color(0xFF1565C0),
        classicColour = Color(0xFFD32F2F),
    ),
    CLUBS(
        displayName = "CLUBS",
        spokenName = "clubs",
        fourColour = Color(0xFF2E7D32),
        classicColour = Color(0xFF111111),
    );

    fun colour(fourColourDeck: Boolean): Color =
        if (fourColourDeck) fourColour else classicColour
}
