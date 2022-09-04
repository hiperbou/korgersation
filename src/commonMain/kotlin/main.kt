import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import core.*
import scenes.*
import ui.*

suspend fun main() = Korge(width = 1280, height = 720, bgcolor = Colors.LIGHTGRAY) {

    val conversationEvents = ConversationEvents()

    val conversationSystem = ConversationSystem(
        EventsConversationTalkController(conversationEvents),
        EventsConversationOptionsController(conversationEvents),
        conversationEvents
    )

    val sceneContainer = sceneContainer()

    dialogueUIComponent(conversationEvents, conversationSystem).addTo(this)

	sceneContainer.changeTo({ MainScene(conversationSystem, conversationEvents) })
}



