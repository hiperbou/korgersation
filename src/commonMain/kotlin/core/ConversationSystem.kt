package core

import com.hiperbou.conversation.*
import com.hiperbou.conversation.controller.*
import com.hiperbou.conversation.dsl.*

class ConversationSystem(
    private val conversationCPU:ConversationCPU,
    private val conversationEvents: ConversationEvents
) {
    constructor(conversationTalkController: ConversationTalkController,
                conversationOptionsController: ConversationOptionsController,
                conversationEvents: ConversationEvents
    ):this(ConversationCPU(conversationTalkController, conversationOptionsController), conversationEvents)
    private lateinit var conversationState: IConversationState

    fun start (conv: CompiledConversation) {
        conversationState = conv.state
        conversationCPU.reset(conv.program)
        conversationEvents.onConversationStarts(Unit)
        run()
    }

    fun run() {
        conversationCPU.run()
        if (conversationCPU.isHalted()) {
            conversationEvents.onConversationFinished(Unit)
        }
    }
    fun pause() = conversationCPU.pause()

    fun getAvailableOptions() = conversationState.getAvailableOptions()
    fun getText(index: Int) = conversationState.getText(index)
    fun setOptionEnabled(index: Int, value: Int) = conversationState.setOptionEnabled(index, value)
}
