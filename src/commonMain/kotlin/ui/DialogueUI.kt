package ui

import com.soywiz.klock.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.text.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import core.*

var currentTextPosition:IPoint = Point(256, 64)
var currentCharacterTextColor = Colors.WHITE

private suspend fun Container.say(what:String) {
    val textSize = 32.0
    val textColor = currentCharacterTextColor
    val position = currentTextPosition
    val container = dialogueText(what, textSize, position, textColor)
    delay(2.seconds)
    container.removeFromParent()
}

private fun Container.showOptions(
    options: List<String>, block:suspend Container.(Int)->Unit
) {
    val textSize = 32.0
    val textColor = Colors.WHITE
    val sceneWidth = 1280
    val sceneHeight = 720
    val rectHeight = sceneHeight / 3
    val rectYPositioin = sceneHeight - rectHeight

    val optionsPosition = Point(10, rectYPositioin)
    container {
        solidRect(sceneWidth, rectHeight, Colors.BLACK.withA(128)).position(0, rectYPositioin )
        options.forEachIndexed { index, text ->
            dialogueText(
                text,
                textSize,
                optionsPosition.plus(Point(0.0, textSize * index)),
                textColor,
                TextAlignment.TOP_LEFT
            ) {
                onOver {
                    color = Colors.YELLOW
                }
                onOut {
                    color = textColor
                }
                onClick {
                    this@container.removeFromParent()
                    this@showOptions.block(index)
                }
            }
        }
    }
}

private fun Container.dialogueText(
    what: String,
    textSize: Double,
    position: IPoint,
    textColor: RGBA,
    align: TextAlignment = TextAlignment.CENTER,
    block: @ViewDslMarker Text.() -> Unit = {}
) = container() {
    text(what, textSize, Colors.BLACK, alignment = align) { position(position.x - 1, position.y) }
    text(what, textSize, Colors.BLACK, alignment = align) { position(position.x + 1, position.y) }
    text(what, textSize, Colors.BLACK, alignment = align) { position(position.x - 1, position.y - 1) }
    text(what, textSize, Colors.BLACK, alignment = align) { position(position.x + 1, position.y + 1) }
    text(what, textSize, Colors.BLACK, alignment = align) { position(position.x, position.y - 1) }
    text(what, textSize, Colors.BLACK, alignment = align) { position(position.x, position.y + 1) }
    text(what, textSize, textColor, alignment = align) { position(position.x, position.y); block() }
}

private fun String.isRandom() = startsWith("rnd:")
private fun String.isNotRandom() = !isRandom()
private fun String.getRandom():String{
    return removePrefix("rnd:").split("|").random()
}

fun Stage.dialogueUIComponent(
    conversationEvents: ConversationEvents,
    conversationSystem: ConversationSystem
) = container {
    conversationEvents.apply {
        onSay {
            val what = conversationSystem.getText(it)
            conversationSystem.pause()
            launch {
                say(what.takeIf { it.isNotRandom() } ?: what.getRandom())
                conversationSystem.run()
            }
        }

        onUpdateOption { index, value ->
            conversationSystem.setOptionEnabled(index, value)
        }

        onShowOptions {
            val availableOptions = conversationSystem.getAvailableOptions()
            conversationSystem.pause()

            showOptions(availableOptions.map { it.text }) {
                selectedOption = availableOptions[it].id
                conversationSystem.run()
            }
        }
    }
}
