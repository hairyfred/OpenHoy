package uk.hairyfred.openhoy.model

/**
 * A card in the deck. Most cards are [SuitedCard]; some packs also include
 * [Joker]s, which have no suit.
 */
sealed interface Card {
    /** What text-to-speech should announce when this card is drawn. */
    val spoken: String

    /** Stable key for use in a Compose `items(...) { key = }` lambda. */
    val historyKey: String

    companion object {
        /** All 52 suited cards, plus any jokers implied by [jokerMode]. */
        fun fullDeck(jokerMode: JokerMode): List<Card> {
            val suited: List<Card> = Suit.entries.flatMap { suit ->
                Rank.entries.map { rank -> SuitedCard(rank, suit) }
            }
            val jokers: List<Card> = when (jokerMode) {
                JokerMode.NONE -> emptyList()
                JokerMode.ONE -> listOf(Joker(JokerVariant.PLAIN))
                JokerMode.TWO_COLOURS -> listOf(Joker(JokerVariant.RED), Joker(JokerVariant.BLACK))
            }
            return suited + jokers
        }
    }
}

data class SuitedCard(val rank: Rank, val suit: Suit) : Card {
    override val spoken: String get() = "${rank.spokenName} of ${suit.spokenName}"
    override val historyKey: String get() = "${rank.name}-${suit.name}"
}

/** Distinct flavours of joker the deck can contain. */
enum class JokerVariant(val spokenName: String) {
    PLAIN("joker"),
    RED("red joker"),
    BLACK("black joker"),
}

data class Joker(val variant: JokerVariant) : Card {
    override val spoken: String get() = variant.spokenName
    override val historyKey: String get() = "joker-${variant.name}"
}
