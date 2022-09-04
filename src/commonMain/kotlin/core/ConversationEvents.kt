package core

import com.soywiz.korio.async.*

class ConversationEvents {
    val onChangeCharacter = Signal<Int>()
    val onSay = Signal<Int>()

    var selectedOption = 0
    val onShowOptions = Signal<Unit>()
    val onUpdateOption = Signal2<Int, Int>()

    val onConversationStarts = Signal<Unit>()
    val onConversationFinished = Signal<Unit>()
}
