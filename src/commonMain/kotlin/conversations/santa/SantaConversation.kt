package conversations.demo

import com.hiperbou.conversation.dsl.*
import com.soywiz.korio.util.length
import core.*

class SantaConversation {

    companion object {
        object variables {
            private var currentVariableIndex = 64
            private fun variable() = MemoryAddress(currentVariableIndex++)

            //Intro
            val saidHey = variable()
            val sentToSleep = variable()

            //Bob
            val guessedName = variable()
            val santaKnowsBobName = variable()
            val santaToldBobHisName = variable()
            val santaToldBobHisAge = variable()

            //Alice
            val ALICE_QUESTIONS = (1..10)
            val talkedToAlice = ALICE_QUESTIONS.map { variable() }
        }

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

            val optionIAmSanta = option("I am Santa! Merry Christmas!", 0)
            val optionGoToSleep = option("Please go back to sleep", 0)

            buildOptions()

            val labelSaidHey = Label()
            val labelConvinceIAmSanta = Label()

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
                variables.saidHey.set(true)
                it.disable()
                showOptions()
            }
            optionLeaving {
                santa -"I'm just leaving"
                santa -"Please go back to sleep"
                bob - "Don't move!"
                alice - "I'll call the police!"
                variables.sentToSleep.set(true)
                it.disable()
                showOptions()
            }

            optionOfCourse {
                santa - "Of course I am"
                -"I said HO HO HO..."
                labelSaidHey.gotoIfTrue(variables.saidHey)
                bob - "*GASPS*"
                alice - "..."
                showOptions(optionIAmSanta, optionGoToSleep)
            }

            optionNotReally {
                santa - "Hmmm... Not really"
                alice - "So who you are?"
                bob - "And what are you doing in OUR house?"
                showOptions(optionIAmSanta, optionGoToSleep)
            }

            labelSaidHey {
                bob - "But you said: Hey, hey, hey!"
                alice - "That's definitely not something Santa would say"
                showOptions(optionIAmSanta, optionGoToSleep)
            }

            optionIAmSanta {
                santa - "I am Santa! Merry..."
                - alice
                - "Wait..."
                - "Is this even Christmas today?"
                bob - "I don't think so!"
                santa - "Well.. actually..."
                alice - "I think you're just a burglar"
                bob - "He DOES look like one to me"

                labelConvinceIAmSanta.goto()
            }

            optionGoToSleep {
                - santa
                - "Please go back to sleep, kids."
                - "You will open your presents tomorrow morning"
                alice - "Don't touch those presents!"
                bob - "Yeah, keep your hands where we can see them"
                variables.sentToSleep.set(true)
                labelConvinceIAmSanta.goto()
            }

            labelConvinceIAmSanta {
                santa - "I can prove I am Santa if you're not convinced..."
                bob - "Hmmm..."
                alice - "Ok, we'll give you a chance."
            }
        }

        fun santaThoughts() = conversation {
            val santa = character("Santa")

            val labelKidsNaughty = Label()
            val labelHaveToGuessBobName = Label()
            val labelHaveToGuessBobAge = Label()

            labelHaveToGuessBobAge.gotoIfTrue(variables.santaToldBobHisName)
            labelHaveToGuessBobName.gotoIfTrue(variables.guessedName)
            labelKidsNaughty.gotoIfTrue(variables.sentToSleep)
            santa -"Well, I guess I should prove who I am to those nice kids"
            halt()

            labelKidsNaughty {
                -santa
                -"Well, I guess I must prove who I am to those naughty kids"
                -"Usually they just go back to sleep when I ask to"
                -"But these ones seem so stubborn"
                halt()
            }

            labelHaveToGuessBobName {
                -santa
                -"Probably I can guess this kid's name"
                halt()
            }

            labelHaveToGuessBobAge {
                -santa
                -"I guess I need to keep convincing them"
                halt()
            }
        }

        fun bobConversation() = conversation {
            val (santa, bob, alice) = initializeCharacters()

            val optionLooks = option("What do you think about my looks?", 1)
            val optionKnowledge = option("There are things only Santa would know", 1)
            val optionLeave = option("Leave", 1)

            val optionCostume = option("Did you ever see someone dressed like this?", 0)
            val optionBeard = option("What do you think about this beard?", 0)
            val optionSound = option("Did the chimes sound awaken you?", 0)

            val optionNameRightName = option("Your name is Bob", 0)
            val optionNameWrongName = option("Your name is...", 0)
            val optionNameWrongName2 = option("I obviously know your name", 0)
            val optionNameLeave = option("I'll be back in a moment", 0)

            val optionYears = (5..10).map { option("You're $it years old", 0) }.toTypedArray()

            buildOptions()

            showOptions()

            val labelShowMainOptions = Label()
            val labelAskName = Label()
            val labelAskNameAfterKnowingIt = Label()
            val labelAskAboutBobsAge = Label()
            val labelAskAboutGift = Label()

            labelShowMainOptions {
                showOptions(optionLooks, optionKnowledge, optionLeave)
            }

            optionLooks {
                showOptions(optionCostume, optionBeard, optionSound)
            }

            optionKnowledge {
                santa - "Ask me something I only could know"
                labelAskAboutGift.gotoIfTrue(variables.santaToldBobHisAge)
                labelAskAboutBobsAge.gotoIfTrue(variables.santaToldBobHisName)
                labelAskNameAfterKnowingIt.gotoIfTrue(variables.santaKnowsBobName)
                labelAskName.goto()
            }

            labelAskName {
                bob - "Well... What's my name?"
                variables.guessedName.set(true)
                showOptions(optionNameWrongName, optionNameWrongName2, optionNameLeave)
            }

            labelAskNameAfterKnowingIt {
                bob - "Well... What's my name?"
                showOptions(optionNameRightName, optionNameWrongName2, optionNameLeave)
            }

            labelAskAboutBobsAge {
                bob - "So, how old am I?"
                showOptions(*optionYears)
                halt()
            }

            optionYears.forEach { option ->
                option {
                    santa - it.text
                    if (it.text.contains("6")) {
                        bob - "I'm about to be 7!"
                        variables.santaToldBobHisAge.set(true)
                    } else {
                        bob - "You failed!"
                        alice - "What kind of Santa would not know that?"
                    }
                }
                labelShowMainOptions.goto()
            }

            labelAskAboutGift {
                bob - "What did I write in the letter I sent YOU this year?"
                santa - "You wrote all nonesense"
                //TODO: write a proper ending
                halt()
            }

            optionLeave {
                halt()
            }

            optionCostume {
                santa - "Did you ever see someone dressed like this?"
                - bob
                - "I know who dresses like that"
                - "But it doesn't mean nothing"
                - alice
                - "Anyone could buy that costume somewhere"
                it.disable() //TODO: disable forever
                labelShowMainOptions.goto()
            }

            optionBeard {
                santa - "What do you think about this beard?"
                - bob
                - "It looks fluffy"
                - alice
                - "Not like I imagine a 100 years old someone's beard"
                it.disable()//TODO: disable forever
                labelShowMainOptions.goto()
            }

            optionSound {
                santa - "Did the chimes sound awaken you?"
                bob - "I heard something like that"
                alice - "It could have been whatever"
                it.disable()//TODO: disable forever
                labelShowMainOptions.goto()
            }

            optionNameWrongName {
                santa - "Your name is..."
                santa - "rnd:John|William|James|Benjamin|Henry|Michael|Alexander|Daniel|Logan|Matthew|Samuel|Christopher|Luke|Ryan|Nathan|Caleb|Jonathan|Aaron|Charles|Thomas|Jordan|Jose|Kevin|Nolan|Cooper|Elias|Juan|Luis|Miles|Vincent|Leonardo|Max|Wesley|Antonio|Victor|Carlos|Alan|Oscar|Warren|Graham"
                bob - "rnd:Ha! Nice try|Not even close|Almost|Not really|Are you sure?"
                labelShowMainOptions.goto()
            }

            optionNameWrongName2 {
                santa - "I obviously know your name"
                santa - "I got everyone in my list and I could look it up if I wanted"
                bob - "Oh, that's fair"
                alice - "No! He's lying!"
                bob - "Not fair!"
                labelShowMainOptions.goto()
            }

            optionNameLeave {
                santa - "I'll be back in a moment"
                halt()
            }

            optionNameRightName {
                santa - "Your name is..."
                santa - "Bob"
                bob - "Woah!"
                alice - "That was impressive, honestly."
                variables.santaToldBobHisName.set(true)
                labelShowMainOptions.goto()
            }
        }

        fun aliceConversation() = conversation {
            val (santa, bob, alice) = initializeCharacters()

            val optionAliceQuestioning = option("So...", 1)
            val optionAskAboutBobsName = option("Bob's name", 0)

            //How do you deliver all the presents in one night
            val optionQuestion0 = arrayOf(
                option("Magic", 0),
                option("Timezones", 0),
                option("I have some help from others", 0),
                option("I dont't", 0)
            )
            //Has anyone ever seen you?
            val optionQuestion1 = arrayOf(
                option("All the time", 0),
                option("Sometimes", 0),
                option("This is the first time", 0),
            )
            //Do all the presents for all the children in the world fit on the sleigh?
            val optionQuestion2 = arrayOf(
                option("It's Magic", 0),
                option("It's bigger on the inside", 0),
                option("I need to go home to stock up", 0),
            )
            //How can you come down the chimney and not get burned?
            val optionQuestion3 = arrayOf(
                option("Did you heard about magic?", 0),
                option("I got other ways to get in", 0),
                option("I actually get burned", 0),
            )
            //How do you really know if kids have been bad or good?
            val optionQuestion4 = arrayOf(
                option("I see and hear everything", 0),
                option("I got reports for each one of you", 0),
                option("I don't really know all the times", 0),
            )
            //How old is Santa Claus?
            //option("break",0)
            //Have you ever been convicted of breaking & entering?
            //How many carrots do your reindeer have?
            //I don't know what else to ask

            val optionLeave = option("Leave", 1)
            buildOptions()

            val labelAliceQuestions = variables.ALICE_QUESTIONS.map { Label() }
            val labelShowOptions = Label()

            optionAskAboutBobsName.enableIfTrue(variables.guessedName)

            variables.talkedToAlice.reversed().forEachIndexed { index, it ->
                labelAliceQuestions[variables.ALICE_QUESTIONS.length - index - 1].gotoIfTrue(it)
            }

            santa - "Hello"
            - alice
            - "I'm not easily fooled, unlike my brother"
            - "So you better defend yourself right now"
            variables.talkedToAlice[0].set(true)

            //labelShowOptions.goto()
            labelAliceQuestions.first().goto()

            optionQuestion0.forEachIndexed{ index, option ->
                when(index) {
                    0 -> option {
                        santa - "Magic!"
                        alice - "Sure. As if such thing existed."
                        labelShowOptions.goto()
                    }
                    1 -> option {
                        santa - "Have you ever heard of timezones?"
                        alice - "They're so annoying."
                        santa - "Yes, but I have 24 hours to deliver the presents to everyone."
                        alice - "I see..."
                        labelShowOptions.goto()
                    }
                    2 -> option {
                        santa - it.text
                        alice - "How is that?"
                        santa - "Sometimes it's difficult for me to deliver EVERY present myself"
                        santa - "I am old, you know?"
                        labelShowOptions.goto()
                    }
                    3 -> option {
                        santa - it.text
                        alice - "..."
                        bob - "What?"
                        alice - "Nothing"
                        labelShowOptions.goto()
                    }
                    else -> option {
                        santa - it.text
                        alice - "Uh-huh?"
                        labelShowOptions.goto()
                    }
                }
            }
            optionQuestion1.forEachIndexed{ index, option ->
                when(index) {
                    0 -> option {
                        santa - "All the time!"
                        santa - "How else I am going to be known if I wont let people see me from time to time?"
                        alice - "Makes sense"
                        labelShowOptions.goto()
                    }
                    1 -> option {
                        santa - "Sometimes, although I try not to..."
                        alice - "I see..."
                        labelShowOptions.goto()
                    }
                    2 -> option {
                        santa - it.text
                        bob - "We caught you!"
                        alice - "It was so foolish of you"
                        labelShowOptions.goto()
                    }
                    else -> option {
                        santa - it.text
                        alice - "Uh-huh?"
                        labelShowOptions.goto()
                    }
                }
            }
            optionQuestion2.forEachIndexed{ index, option ->
                when(index) {
                    0 -> option {
                        santa - "Magic!"
                        alice - "Is that a spell I could learn?"
                        labelShowOptions.goto()
                    }
                    1 -> option {
                        santa - it.text
                        alice - "What kind of technology is that?"
                        santa - "It's some kind of dimensionally transcendental one"
                        labelShowOptions.goto()
                    }
                    2 -> option {
                        santa - it.text
                        alice - "Wouldn't that take you too much time?"
                        labelShowOptions.goto()
                    }
                    else -> option {
                        santa - it.text
                        alice - "Uh-huh?"
                        labelShowOptions.goto()
                    }
                }
            }
            optionQuestion3.forEachIndexed{ index, option ->
                when(index) {
                    0 -> option {
                        santa - it.text
                        santa - "It also helps when I get stuck inside the chimney"
                        alice - "I'm sure that happens a lot to you"
                        labelShowOptions.goto()
                    }
                    1 -> option {
                        santa - it.text
                        alice - "Breaking the windows?"
                        santa - "Do you see any broken window around?"
                        labelShowOptions.goto()
                    }
                    2 -> option {
                        santa - it.text
                        alice - "Sure"
                        labelShowOptions.goto()
                    }
                    else -> option {
                        santa - it.text
                        alice - "Uh-huh?"
                        labelShowOptions.goto()
                    }
                }
            }
            optionQuestion4.forEachIndexed{ index, option ->
                when(index) {
                    0 -> option {
                        santa - it.text
                        alice - "Like... Superman?"
                        santa - "Y.. Yes!"
                        bob - "Woah"
                        labelShowOptions.goto()
                    }
                    1 -> option {
                        santa - it.text
                        alice - "I knew it"
                        alice - "It's our parents or our teachers?"
                        santa - "Everyone can report to me anonymously"
                        labelShowOptions.goto()
                    }
                    2 -> option {
                        santa - it.text
                        alice - "So it doesn't matter if we are good or bad?"
                        labelShowOptions.goto()
                    }
                    else -> option {
                        santa - it.text
                        alice - "Uh-huh?"
                        labelShowOptions.goto()
                    }
                }
            }

            labelAliceQuestions.forEachIndexed { index, label ->
                when(index) {
                    0 -> label {
                        alice - "How do you deliver all the presents in one night?"
                        variables.talkedToAlice[index+1].set(true)
                        showOptions(*optionQuestion0 +  optionLeave)
                    }
                    1 -> label {
                        alice - "Has anyone ever seen you?"
                        variables.talkedToAlice[index+1].set(true)
                        showOptions(*optionQuestion1 +  optionLeave)
                    }
                    2 -> label {
                        alice - "Do all the presents for all the children in the world fit on the sleigh?"
                        variables.talkedToAlice[index+1].set(true)
                        showOptions(*optionQuestion2 +  optionLeave)
                    }
                    3 -> label {
                        alice - "How can you come down the chimney and not get burned?"
                        variables.talkedToAlice[index+1].set(true)
                        showOptions(*optionQuestion3 +  optionLeave)
                    }
                    4 -> label {
                        alice - "How do you really know if kids have been bad or good?"
                        variables.talkedToAlice[index+1].set(true)
                        showOptions(*optionQuestion4 +  optionLeave)
                    }
                    5 -> label {
                        alice - "How old is Santa Claus?"
                        variables.talkedToAlice[index+1].set(true)
                        halt()
                    }
                    6 -> label {
                        alice - "Have you ever been convicted of breaking & entering?"
                        variables.talkedToAlice[index+1].set(true)
                        halt()
                    }
                    7 -> label {
                        alice - "How many carrots do your reindeer have?"
                        variables.talkedToAlice[index+1].set(true)
                        halt()
                    }
                    8 -> label {
                        alice - "How many elves do you have?"
                        variables.talkedToAlice[index+1].set(true)
                        halt()
                    }
                    9 -> label {
                        alice - "I don't know what else to ask"
                        variables.talkedToAlice.drop(1).forEach { it.set(false) }
                        halt()
                    }
                }
            }

            labelShowOptions {
                showOptions()
            }

            optionAliceQuestioning {
                alice - "This option"
            }

            optionAskAboutBobsName {
                alice - "the name is bob"
                variables.santaKnowsBobName.set(true)
            }

            optionLeave {

            }


        }

        fun debugConversation() = conversation {
            val (santa, bob, alice) = initializeCharacters()

            val optionDebugZero = option("Guessed Bob's name", 1)
            val optionDebugOne = option("Knows Bob's name", 1)
            val optionLeave = option("Leave", 1)
            buildOptions()

            showOptions()

            optionDebugZero{
                santa - "guessedName bob's name"
                variables.guessedName.set(true)
                halt()
            }
            optionDebugOne {
                santa - "The name is bob"
                variables.santaKnowsBobName.set(true)
                halt()
            }
            optionLeave {
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
