package uk.hairyfred.openhoy.model

data class DeckState(
    val order: List<Card>,
    val drawnCount: Int,
) {
    val current: Card? get() = if (drawnCount == 0) null else order[drawnCount - 1]
    val history: List<Card> get() = order.subList(0, drawnCount).asReversed()
    val total: Int get() = order.size
    val remaining: Int get() = total - drawnCount
    val finished: Boolean get() = drawnCount >= order.size

    fun drawNext(): DeckState =
        if (finished) this else copy(drawnCount = drawnCount + 1)

    companion object {
        fun freshShuffled(jokerMode: JokerMode): DeckState =
            DeckState(order = Card.fullDeck(jokerMode).shuffled(), drawnCount = 0)
    }
}
