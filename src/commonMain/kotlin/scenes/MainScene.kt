package scenes

import com.soywiz.klock.*
import com.soywiz.korge.component.docking.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.atlas.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import conversations.demo.*
import core.*
import ui.*

class MainScene(private val conversationSystem: ConversationSystem, private val conversationEvents: ConversationEvents) : Scene() {
    override suspend fun SContainer.sceneMain() {
        val background = resourcesVfs["background4.png"].readBitmap()
        image(background){
            anchor(Anchor.RIGHT)
            dockedTo(Anchor.RIGHT, ScaleMode.FIT)
            scaleX = 1.0
        }
        image(background){
            anchor(Anchor.RIGHT)
            dockedTo(Anchor.LEFT, ScaleMode.FIT)
            scaleX = -1.0
        }
        /*val background = resourcesVfs["background.png"].readBitmap()
        image(background){
            anchor(Anchor.CENTER)
            dockedTo(Anchor.CENTER, ScaleMode.FIT)
        }*/

        val boyAtlas = resourcesVfs["boy.atlas.json"].readAtlas()
        val girlAtlas = resourcesVfs["girl.atlas.json"].readAtlas()
        val santaAtlas = resourcesVfs["santa.atlas.json"].readAtlas()

        val korge = resourcesVfs["korge.png"].readBitmap()
        val minDegrees = (-16).degrees
        val maxDegrees = (+16).degrees

        val imagePositions = mutableListOf(Point(300, 150), Point(720, 150), Point(880, 150))
        val images = mutableListOf<Image>()

        val characterScaleOnMouseOver = 1.1

        fun characterSprite(atlas: Atlas, x:Int, y:Int, initialScaleX:Double, conversation:CompiledConversation):Sprite {
            val char = sprite(atlas.getSpriteAnimation()) {
                position(x, y)
                //anchor(70,250)
                //anchor(Anchor.BOTTOM_CENTER)
                scaleX = initialScaleX
                playAnimationLooped()
            }
            image(korge) {
                rotation = maxDegrees
                anchor(.5, .5)
                scale(0.1)
                position(imagePositions.removeFirst())

                onOver {
                    char.scale(initialScaleX * characterScaleOnMouseOver, characterScaleOnMouseOver)
                }
                onOut {
                    char.scale(initialScaleX, 1.0)
                }
                onClick {
                    conversationSystem.start(conversation)
                }

                images.add(this)
            }
            return char
        }

        val offsetY = 180

        val santa = characterSprite(santaAtlas, 128, 0 + offsetY, 1.0, DemoConversation.singleLineTest)
        val boy = characterSprite(boyAtlas, 800, 32 + offsetY, -1.0, DemoConversation.test2)
        val girl = characterSprite(girlAtlas, 1000, 64 + offsetY, -1.0, DemoConversation.test1)

        conversationEvents.apply {
            onConversationStarts {
                images.forEach { it.visible = false }
            }
            onConversationFinished {
                images.forEach { it.visible = true }
            }
            onChangeCharacter {
                currentCharacterTextColor = when (it) {
                    0 -> Colors.WHITE
                    1 -> RGBA(0x69,0xD2,0xE7, 0xFF)
                    2 -> RGBA(0xFF,0x66,0x66, 0xFF)
                    else -> Colors.WHITE
                }
                currentTextPosition = images[it].pos
            }
        }

        conversationSystem.start(DemoConversation.singleLineTest)

        while (true) {
            images.forEach {
                it.tween(it::rotation[minDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
                it.tween(it::rotation[maxDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
            }
        }
    }
}
