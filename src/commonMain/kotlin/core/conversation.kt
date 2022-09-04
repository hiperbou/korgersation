package core

import com.hiperbou.conversation.compiler.*
import com.hiperbou.conversation.dsl.*

data class CompiledConversation(val state: IConversationState, val program:IntArray)

private val asmConversationWriter = AsmConversationWriter()

fun conversation(block: ConversationBuilder.() -> Unit): CompiledConversation {
    val conversationBuilder = ConversationBuilder(asmConversationWriter)
    try {
        val program = conversationBuilder.start { block() }
        return CompiledConversation(conversationBuilder.conversationState, program)
    } catch (e:Throwable) {
        println(conversationBuilder.conversationWriter.toString())
        throw e
    }
}

fun say(what:String) = conversation {
    val ego = character("ego")
    ego - what
    halt()
}
