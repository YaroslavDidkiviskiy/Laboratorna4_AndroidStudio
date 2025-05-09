package com.example.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var spinnerGridSize: Spinner
    private lateinit var resetButton: Button
    private lateinit var newGameButton: Button
    private lateinit var showScoreButton: Button

    private var gridSize = 3
    private var buttons = ArrayList<Button>()
    private var currentPlayer = "X"
    private var scoreX = 0
    private var scoreO = 0
    private var gameActive = true

    private var timer: CountDownTimer? = null
    private var timeLeftInMillis = 10000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupSpinner()
        setupButtons()
        startNewRound()
    }

    private fun initViews() {
        gridLayout = findViewById(R.id.gridLayout)
        scoreTextView = findViewById(R.id.scoreTextView)
        timerTextView = findViewById(R.id.timerTextView)
        spinnerGridSize = findViewById(R.id.spinnerGridSize)
        resetButton = findViewById(R.id.resetButton)
        newGameButton = findViewById(R.id.newGameButton)
        showScoreButton = findViewById(R.id.showScoreButton)
    }

    private fun setupSpinner() {
        val gridSizes = arrayOf("3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gridSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGridSize.adapter = adapter

        spinnerGridSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                gridSize = gridSizes[position].toInt()
                startNewRound()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        resetButton.setOnClickListener { startNewRound() }
        newGameButton.setOnClickListener {
            scoreX = 0
            scoreO = 0
            updateScore()
            startNewRound()
        }
        showScoreButton.setOnClickListener {
            Toast.makeText(this, "X: $scoreX | O: $scoreO", Toast.LENGTH_LONG).show()
        }
    }

    private fun startNewRound() {
        gameActive = true
        timer?.cancel()
        timeLeftInMillis = 10000L
        currentPlayer = "X"
        updateTimerDisplay()

        gridLayout.removeAllViews()
        buttons.clear()
        createGridButtons()
        startTimer()
    }

    private fun createGridButtons() {
        gridLayout.rowCount = gridSize
        gridLayout.columnCount = gridSize

        for (i in 0 until gridSize * gridSize) {
            val button = Button(this).apply {
                textSize = 24f
                isEnabled = true
                setOnClickListener { onGridButtonClick(this) }
            }
            buttons.add(button)
            gridLayout.addView(button)
        }
    }

    private fun onGridButtonClick(button: Button) {
        if (!gameActive || button.text.isNotEmpty()) return

        button.text = currentPlayer
        when {
            checkWin() -> handleWin()
            isBoardFull() -> handleDraw()
            else -> switchPlayer()
        }
    }

    private fun handleWin() {
        if (currentPlayer == "X") scoreX++ else scoreO++
        updateScore()
        Toast.makeText(this, "Гравець $currentPlayer переміг!", Toast.LENGTH_SHORT).show()
        endGame()
    }

    private fun handleDraw() {
        Toast.makeText(this, "Нічия!", Toast.LENGTH_SHORT).show()
        endGame()
    }

    private fun endGame() {
        gameActive = false
        timer?.cancel()
        buttons.forEach { it.isEnabled = false }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                handleTimeOut()
            }
        }.start()
    }

    private fun handleTimeOut() {
        Toast.makeText(this, "Час вичерпано!", Toast.LENGTH_SHORT).show()
        endGame()
    }

    private fun updateTimerDisplay() {
        timerTextView.text = "Час: ${timeLeftInMillis / 1000}"
    }

    private fun updateScore() {
        scoreTextView.text = "X: $scoreX | O: $scoreO"
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == "X") "O" else "X"
    }

    private fun isBoardFull(): Boolean {
        return buttons.all { it.text.isNotEmpty() }
    }


    private fun checkWin(): Boolean {
        val board = List(gridSize) { row ->
            List(gridSize) { col ->
                buttons[row * gridSize + col].text.toString()
            }
        }

        // Check rows and columns
        for (i in 0 until gridSize) {
            if (board[i].all { it == currentPlayer }) return true
            if (board.all { row -> row[i] == currentPlayer }) return true
        }

        // Check diagonals
        val diag1 = (0 until gridSize).map { board[it][it] }
        val diag2 = (0 until gridSize).map { board[it][gridSize - it - 1] }

        return diag1.all { it == currentPlayer } || diag2.all { it == currentPlayer }
    }
}