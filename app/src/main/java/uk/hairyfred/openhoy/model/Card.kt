package uk.hairyfred.openhoy.model

data class Card(val rank: Rank, val suit: Suit) {
    val spoken: String get() = "${rank.spokenName} of ${suit.spokenName}"

    companion object {
        val fullDeck: List<Card> =
            Suit.entries.flatMap { suit -> Rank.entries.map { rank -> Card(rank, suit) } }
    }
}
