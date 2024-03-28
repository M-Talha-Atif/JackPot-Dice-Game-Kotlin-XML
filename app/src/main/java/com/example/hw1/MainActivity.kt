package com.example.hw1


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hw1.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    // Variables to be used for Activity
    private lateinit var binding: ActivityMainBinding
    private var currentPlayer = 1
    private var player1Score = 0
    private var player2Score = 0
    private var jackpot = 5
    private var availablePoints = 0
    private var isClicked = false
    private var isGuessMade = true
    private var dieResult = 0

    // On Create Function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Function roll a dice and update UI of screen

        rollDie()
        updateUI()

        // When user clicks, on roll die button

            binding.buttonRollDie.setOnClickListener {
                if( isClicked ) {

                    switchPlayer()
                    rollDie()


                    updateUI()
                    if(dieResult==6)
                        Toast.makeText(this, "Player $currentPlayer got a JACKPOT problem!", Toast.LENGTH_SHORT).show()

                    isClicked = false
                    isGuessMade = true

                }
                else {
                    Toast.makeText(this, "Please click on Guess Button.", Toast.LENGTH_SHORT).show()
                }
            }

        // Button Guess click event


        binding.buttonGuess.setOnClickListener {
            if( !isGuessMade ){
                Toast.makeText(this, "Click on Roll Button Now.", Toast.LENGTH_SHORT).show()

            }
            else {
                val answerText = binding.editTextAnswer.text.toString()
                if (answerText.isNotEmpty()) {
                    val answer = answerText.toInt()
                    // Call checkAnswer function with the answer
                    if(dieResult!=6)
                       checkAnswer(answer)
                    else   tryForJackpot()

                    updateUI()



                    // Clear the editTextAnswer field
                    binding.editTextAnswer.text.clear()

                    isClicked = true
                    // Disable the Guess button until the Roll Die button is clicked
                    isGuessMade = false




                } else {
                    // Show a message if the editTextAnswer field is empty
                    Toast.makeText(this, "Please enter your guess.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
    // Function to roll dice
    private fun rollDie() {

        // Get random value either 1,2,3,4,5,6
        dieResult = (1..6).random()
        // when statement
        when (dieResult) {
            1 -> { // add problem
                binding.imageViewDice.setImageResource(R.drawable.dice1)
                generateProblem("addition")
                availablePoints = 1

            }
            2 -> { // subtract problem
                binding.imageViewDice.setImageResource(R.drawable.dice2)
                generateProblem("subtraction")
                availablePoints = 2

            }
            3 -> { // multiplication problem
                binding.imageViewDice.setImageResource(R.drawable.dice3)
                generateProblem("multiplication")
                availablePoints = 3

            }
            4 -> { // Double Points
                binding.imageViewDice.setImageResource(R.drawable.dice4)
                Toast.makeText(this, "Player $currentPlayer points doubled!", Toast.LENGTH_SHORT).show()
                if (currentPlayer == 1) {
                    player1Score *= 2
                } else {
                    player2Score *= 2
                }
                lifecycleScope.launch {
                    delay(3000)

                }
                switchPlayer()
                generateProblem("loseturn") // random problem for other player

            }
            5 -> { // Lose your turn
                binding.imageViewDice.setImageResource(R.drawable.dice5)
                Toast.makeText(this, "Player $currentPlayer loses a turn!", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    delay(3000)

                }
                switchPlayer()
                generateProblem("loseturn")
            }
            6 -> { // Get a Jackpot chance

                binding.imageViewDice.setImageResource(R.drawable.dice6)
                lifecycleScope.launch {
                    delay(3000)

                }
                generateProblem("jackpot")

            }
        }
    }


    private fun generateProblem(problem: String) {
        var problemType = problem // store problem type there as problem passed in parameter is of val (immutable)
        // handle jackpot and lost turn case
        if (problemType == "jackpot" || problemType == "loseturn") {

            when ((0..2).random()) {
                0 -> {
                    problemType = "multiplication"
                    availablePoints = 3
                    binding.imageViewDice.setImageResource(R.drawable.dice3)
                }
                1 -> {
                    problemType = "addition"
                    availablePoints = 1
                    binding.imageViewDice.setImageResource(R.drawable.dice1);}
                else -> {
                    problemType = "subtraction"
                    availablePoints = 2
                    binding.imageViewDice.setImageResource(R.drawable.dice2)
                }
            }

        }
        // Make equations according to numbers defined below

            val number1 =
                if (problemType == "multiplication") (0..20).random() else (0..99).random()
            val number2 =
                if (problemType == "multiplication") (0..20).random() else (0..99).random()
            val operationSymbol = when (problemType) {
                "addition" -> "+"
                "subtraction" -> "-"
                "multiplication" -> "×"
                else -> ""
            }

            val problemMade = "$number1 $operationSymbol $number2 ="
            binding.textViewEquation.text = problemMade

        
    }
   // Check answer of a user
    private fun checkAnswer(answer: Int) {
        val problem = binding.textViewEquation.text.toString()
        if (problem == "Lose a turn") {
            Toast.makeText(this, "Player $currentPlayer loses a turn!", Toast.LENGTH_SHORT).show()
        } else {
            val parts = problem.split(" ")
            val number1 = parts[0].toInt()
            val operation = parts[1]
            val number2 = parts[2].toInt()

            val correctAnswer = when (operation) {
                "+" -> number1 + number2
                "-" -> number1 - number2
                "×" -> number1 * number2
                else -> 0 // Invalid operation
            }

            if (answer == correctAnswer) {
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
                // Update player scores based on the current player
                if (currentPlayer == 1) {
                    player1Score += availablePoints
                } else {
                    player2Score += availablePoints
                }
            } else {
                Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show()

            }

        }
        availablePoints = 0 // Reset available points for the next turn

    }

    // Function to check jackpot answer
    private fun tryForJackpot() {
        val problem = binding.textViewEquation.text.toString()
        val parts = problem.split(" ")
        val number1 = parts[0].toInt()
        val operation = parts[1]
        val number2 = parts[2].toInt()
        val correctAnswer = when (operation) {
            "+" -> number1 + number2
            "-" -> number1 - number2
            "×" -> number1 * number2
            else -> 0 // Invalid operation
        }

        // Check if the user's answer is correct
        val userAnswer = binding.editTextAnswer.text.toString().toIntOrNull()
        if (userAnswer != null && userAnswer == correctAnswer) {
            Toast.makeText(this, "Correct JACKPOT OBTAINED by Player: $currentPlayer!", Toast.LENGTH_SHORT).show()
            // Add the current jackpot points to the current player's score
            if (currentPlayer == 1) {
                player1Score += jackpot
            } else {
                player2Score += jackpot
            }
            // Reset the jackpot points to 5
            jackpot = 5
            availablePoints = 0 // available to zero
        } else {
            jackpot += availablePoints
            Toast.makeText(this, "InCorrect JACKPOT Chance Lost by Player: $currentPlayer!", Toast.LENGTH_SHORT).show()

        }

        // Update the UI
        updateUI()
    }
   // Switch turn of players
    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == 1) 2 else 1
    }


    // Update Ui function to update user interface
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        // Update current player turn
        binding.textViewCurrentPlayerTurn.text = "Current Player Turn: Player $currentPlayer"

        // Update player scores
        binding.textViewPlayer1Score.text = "Player 1 total: $player1Score"
        binding.textViewPlayer2Score.text = "Player 2 total: $player2Score"

        // Update jackpot amount
        binding.textViewJackpotScore.text = "Current Jackpot: $jackpot"

        if(player1Score >= 20 || player2Score >= 20){
            Toast.makeText(this, "Congrats!!! Player $currentPlayer won!!! (Restarting wait 5 seconds)", Toast.LENGTH_LONG).show()
            lifecycleScope.launch {
                delay(5000)

            }
            // reset variables
            currentPlayer = 1
            player1Score = 0
            player2Score = 0
            jackpot = 5
            availablePoints = 0
            isClicked = false
            isGuessMade = true
            dieResult = 0
            // restart game after 5 seconds
            Toast.makeText(this, "Game Restarted after 5 seconds!!!", Toast.LENGTH_SHORT).show()
            updateUI()

        }

    }



}


