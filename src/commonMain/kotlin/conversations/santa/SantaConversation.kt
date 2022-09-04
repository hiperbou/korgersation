package conversations.demo

import com.hiperbou.conversation.controller.*
import com.hiperbou.conversation.dsl.*
import core.*

class SantaConversation {

    companion object {
        private fun ConversationBuilder.initializeCharacters() = listOf(
            character("Santa"),
            character("Bob"),
            character("Alice")
        )

        fun intro() = conversation {
            val (santa, bob, alice) = initializeCharacters()

            val optionHohoho = option("HO HO HO!", 1)
            val optionHey = option("Hey, hey, hey!", 1)
            val optionLeaving = option("I'm just leaving", 1)

            val optionOfCourse = option("Of course I am", 0)
            val optionNotReally = option("Not really", 0)
            //val optionHidden = option("I'm sorry for being rude", 0)
            //val optionAdios = option("hasta luego", 1)
            buildOptions()

            santa -"Hmm... this is a great night so far..."

            bob -"What's all that noise?"
            alice -"There's someone there!"

            santa -"Oh, no"

            showOptions()

            optionHohoho {
                santa -"HO HO HO!"

                bob - "*gasps*"
                alice - "..."
                bob - "Is that YOU?"
                alice - "Santa?"

                showOptions(optionOfCourse, optionNotReally)
            }
            optionHey {
                santa -"Hey, Hey, Hey!"
                alice - "No-o-o-o-oh, no way"
                bob - "You think that I'm a little baby?"
                it.disable()
                showOptions()
            }
            optionLeaving {
                santa -"I'm just leaving"
                santa -"Please go back to sleep"
                bob - "Don't move!"
                alice - "I'll call the police!"
                it.disable()
                showOptions()
            }

            optionOfCourse {
                santa - "Of course I am"
                -"I said HO HO HO..."
                halt()
            }

            optionNotReally {
                santa - "Hmmm... Not really"
                alice - "So who you are?"
                bob - "And what are you doing in OUR house?"
                halt()
            }

        }


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
