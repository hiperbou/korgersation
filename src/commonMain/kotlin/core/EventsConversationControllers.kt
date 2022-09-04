package core

import com.hiperbou.conversation.controller.*
import com.soywiz.korio.async.*

class EventsConversationTalkController(private val conversationEvents:ConversationEvents): ConversationTalkController {
    override fun updateCharacter(index: Int) {
        conversationEvents.onChangeCharacter(index)
    }

    override fun updateText(index: Int) {
        conversationEvents.onSay(index)
    }
}

class EventsConversationOptionsController(private val conversationEvents:ConversationEvents):
    ConversationOptionsController {
    override fun updateOption(index: Int, value: Int) {
        conversationEvents.onUpdateOption(index, value)
    }

    override fun getSelectedOption(): Int {
        return conversationEvents.selectedOption
    }

    override fun showOptions() {
        conversationEvents.onShowOptions()
    }
}
