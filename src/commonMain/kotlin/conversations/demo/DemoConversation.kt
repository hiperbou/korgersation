package conversations.demo

import com.hiperbou.conversation.dsl.*
import core.*

class DemoConversation {
    companion object{
        val singleLineTest = say("This is fine")

        val test1 = conversation {
            //0 - set character
            //1 - say
            //2-19 OptionsDevice
            val variableAlreadyTalked = MemoryAddress(32)
            val variableMoreConversation = MemoryAddress(33)

            val labelAlreadyTalked = Label()
            val labelMoreConversation = Label()

            val santa = character("Santa")
            val bob = character("Bob")
            val alice = character("Alice")

            -santa
            -"Hi Alice. How are you?"

            labelMoreConversation.gotoIfTrue(variableMoreConversation)
            labelAlreadyTalked.gotoIfTrue(variableAlreadyTalked)

            -alice
            -"Hi Bob! I'm fine."
            -"Thank you!"

            variableAlreadyTalked.set(1)
            halt()

            labelAlreadyTalked {
                alice {
                    -"We already talked"
                    -"Leave me alone"
                }
                variableMoreConversation.set(true)
                halt()
            }

            labelMoreConversation {
                alice - "What do you want now?"
                bob - "Well, this is just another branch"
                alice - "So?"
                bob - "That it's interesting enough how this can be used to make some conversations"
                alice {
                    -"But it's difficult"
                    -"You have to write so much code to make different characters talk."
                }
                bob - "What could I do about that?"
                alice - "Just think on some Kotlin magic."
                bob - "what about this?"
                alice - "So many methods, just decide one."
            }

            bob - "Uh... ok. :("
        }

        val test2 = conversation {
            val bob = character("Bob")
            val alice = character("Alice")

            val labelAlreadyTalked = Label()
            val variableAlreadyTalked = MemoryAddress(34)
            labelAlreadyTalked.gotoIfTrue(variableAlreadyTalked)

            val optionSaludar = option("hola", 1)
            val optionRobar = option("dame el oro", 1)
            val optionRobarConViolencia = option("dame el oro ahora", 0)
            val optionHidden = option("I'm sorry for being rude", 0)
            val optionAdios = option("hasta luego", 1)
            buildOptions()

            - bob
            -"Ahem..."

            showOptions()

            optionSaludar {
                bob - "¿Hola que pasa?"
                alice - "Buenos días."

                optionSaludar.disable()
                showOptions()
            }

            optionRobar {
                bob - "¡Oh, vaya! ¿Qué es eso de allí?"
                alice - "hmm?"
                bob - "Esto no va a ser fácil..."

                optionRobarConViolencia.enable()
                showOptions()
            }

            optionRobarConViolencia {
                bob - "¡Dame el oro o te quemo con el mechero!"
                alice - "911"
                bob - "¡Oh no!"
                halt()
            }

            optionAdios {
                bob - "Hasta luego."
                alice - "Piérdete."
                variableAlreadyTalked.set(true)
                halt()
            }

            optionHidden {
                bob - "I'm sorry for being rude"
                alice - "That's ok."
                alice - "Now go away."
                variableAlreadyTalked.set(false)
                halt()
            }

            labelAlreadyTalked {
                bob - "Hey..."
                alice - "We have nothing else to discuss"

                showOptions(optionHidden, optionAdios)
            }
        }
    }
}
