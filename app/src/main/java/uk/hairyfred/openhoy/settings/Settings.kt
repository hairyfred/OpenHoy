package uk.hairyfred.openhoy.settings

data class Settings(
    val ttsEnabled: Boolean = true,
    val autoAdvanceEnabled: Boolean = false,
    val autoAdvanceSeconds: Int = 5,
    val fourColourDeck: Boolean = true,
    val showSuitName: Boolean = true,
) {
    companion object {
        const val MIN_SECONDS = 1
        const val MAX_SECONDS = 20
    }
}
