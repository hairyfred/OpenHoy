package uk.hairyfred.openhoy.model

/**
 * A player's Hoy sheet — a randomly-chosen set of suited cards the player must
 * mark off as the caller announces them. Jokers are never on a sheet.
 */
data class HoySheet(
    val cards: List<SuitedCard>,
    val marked: Set<SuitedCard>,
) {
    val markedCount: Int get() = cards.count { it in marked }
    val total: Int get() = cards.size
    val isComplete: Boolean get() = cards.isNotEmpty() && cards.all { it in marked }

    fun toggle(card: SuitedCard): HoySheet =
        copy(marked = if (card in marked) marked - card else marked + card)

    companion object {
        const val DEFAULT_SIZE = 9

        fun random(size: Int = DEFAULT_SIZE): HoySheet {
            val all: List<SuitedCard> = Suit.entries.flatMap { suit ->
                Rank.entries.map { rank -> SuitedCard(rank, suit) }
            }
            return HoySheet(
                cards = all.shuffled().take(size),
                marked = emptySet(),
            )
        }
    }
}
