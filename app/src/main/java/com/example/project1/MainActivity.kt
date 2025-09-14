package com.example.project1

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.toString

class MainActivity : AppCompatActivity() {

    // Variables declared here:
    private var wordToGuess = FourLetterWordList.getRandomFourLetterWord()
    private val limit: Int = 3
    private var attemptCount: Int = 0
    private var gameEnded: Boolean = false

    // UI components
    private lateinit var guessContainer: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var textView: TextView
    private lateinit var inputDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize UI components
        textView = findViewById(R.id.textView)
        inputEditText = findViewById(R.id.editText)
        inputDisplay = findViewById(R.id.inputDisplay)
        guessContainer = findViewById(R.id.guessContainer)
        submitButton = findViewById(R.id.submitButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the submit button click listener
        setupSubmitButton()
    }

    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            if (gameEnded) {
                // Reset the game
                resetGame()
            } else {
                // Process the guess
                handleGuess()
            }
        }
    }

    private fun handleGuess() {
        val userGuess = inputEditText.text.toString()

        // Update attempt counter and display
        attemptCount++
        textView.text = "Attempts: $attemptCount/$limit"

        // Update input display
        inputDisplay.text = userGuess

        // Validate input length
        if (userGuess.length != 4) {
            Toast.makeText(this, "Please enter a 4-letter word", Toast.LENGTH_SHORT).show()
            attemptCount-- // Don't count invalid attempts
            textView.text = "Attempts: $attemptCount/$limit"
            return
        }

        // Process the guess
        processGuess(userGuess, wordToGuess, guessContainer, this)
        inputEditText.text.clear()

        // Check if user won
        if (userGuess.uppercase() == wordToGuess.uppercase()) {
            endGame(true)
            return
        }

        // Check if attempts are exhausted
        if (attemptCount >= limit) {
            endGame(false)
        }
    }

    private fun endGame(won: Boolean) {
        gameEnded = true

        if (won) {
            Toast.makeText(this, "Congratulations! You guessed it!", Toast.LENGTH_LONG).show()
            textView.text = "You Won! Word was: $wordToGuess"
        } else {
            Toast.makeText(this, "Game Over! The word was: $wordToGuess", Toast.LENGTH_LONG).show()
            textView.text = "Game Over! Word was: $wordToGuess"
        }

        // Change button text to indicate reset option
        submitButton.text = "Play Again"
        inputEditText.isEnabled = false
    }

    private fun resetGame() {
        // Reset game variables
        wordToGuess = FourLetterWordList.getRandomFourLetterWord()
        attemptCount = 0
        gameEnded = false

        // Reset UI
        textView.text = "Attempts: 0/$limit"
        inputDisplay.text = ""
        inputEditText.text.clear()
        inputEditText.isEnabled = true
        submitButton.text = "Submit"

        // Clear all previous guesses
        guessContainer.removeAllViews()

        Toast.makeText(this, "New game started! Word: ${wordToGuess.length} letters", Toast.LENGTH_SHORT).show()
    }

    // Main function to handle word comparison and UI update
    private fun processGuess(userGuess: String, targetWord: String, container: LinearLayout, context: Context) {
        // Validate input
        if (userGuess.length != 4 || targetWord.length != 4) {
            return // Handle error case
        }

        val normalizedGuess = userGuess.uppercase()
        val normalizedTarget = targetWord.uppercase()

        // Create the result string with X's for wrong letters
        val result = compareWords(normalizedGuess, normalizedTarget)

        // Create and add new TextView to show both the guess and result
        createResultTextView(normalizedGuess, result, container, context)
    }

    // Core comparison logic
    private fun compareWords(guess: String, target: String): String {
        val result = StringBuilder()

        for (i in 0 until 4) {
            if (guess[i] == target[i]) {
                // Correct letter in correct position
                result.append(guess[i])
            } else {
                // Wrong letter - mark with X
                result.append('X')
            }
        }

        return result.toString()
    }

    // Create and style the result TextView with both guess and result displayed
    private fun createResultTextView(userGuess: String, resultText: String, container: LinearLayout, context: Context) {
        // Create a container for this guess-result pair
        val guessResultContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 4, 8, 4)
        }

        // Create TextView for the user's guess
        val guessTextView = TextView(context).apply {
            text = "Guess: $userGuess"
            textSize = 16f
            setPadding(16, 4, 16, 4)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.NORMAL)
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            setBackgroundResource(android.R.color.transparent)
        }

        // Create TextView for the result
        val resultTextView = TextView(context).apply {
            text = "Result: $resultText"
            textSize = 18f
            setPadding(16, 4, 16, 8)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            setBackgroundResource(android.R.color.white)
        }

        // Add both TextViews to the container
        guessResultContainer.addView(guessTextView)
        guessResultContainer.addView(resultTextView)

        // Add margin between different attempts
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 8, 0, 8)
        guessResultContainer.layoutParams = layoutParams

        // Add to main container
        container.addView(guessResultContainer)
    }
}