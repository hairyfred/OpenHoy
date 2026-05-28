package uk.hairyfred.openhoy.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import uk.hairyfred.openhoy.model.Card
import java.util.Locale

class CardSpeaker(context: Context) {

    private var ready = false
    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.UK
                tts.setSpeechRate(0.9f)
                ready = true
            }
        }
    }

    fun speak(card: Card) {
        if (!ready) return
        tts.speak(card.spoken, TextToSpeech.QUEUE_FLUSH, null, card.spoken)
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
