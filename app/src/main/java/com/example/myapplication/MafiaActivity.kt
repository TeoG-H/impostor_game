package com.example.myapplication

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity

class MafiaActivity : ComponentActivity() {

    private lateinit var game: MafiaGame
    private var players = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mafia_screen)

        game = MafiaGame()

        val inputScreen = findViewById<LinearLayout>(R.id.inputScreen)
        val squaresScreen = findViewById<LinearLayout>(R.id.squaresScreen)

        val inputNumber = findViewById<EditText>(R.id.inputNumber)
        val generateBtn = findViewById<Button>(R.id.generateBtn)
        val grid = findViewById<GridLayout>(R.id.grid)
        val resetBtn = findViewById<Button>(R.id.reset)

        generateBtn.setOnClickListener {
            players = inputNumber.text.toString().toIntOrNull() ?: return@setOnClickListener
            if (players < 4) return@setOnClickListener

            game.start(players)
            inputScreen.visibility = View.GONE
            squaresScreen.visibility = View.VISIBLE

            grid.removeAllViews()
            createSquares(grid, players)
        }

        resetBtn.setOnClickListener {
            game.start(players)
            grid.removeAllViews()
            createSquares(grid, players)
        }
    }

    private fun createSquares(grid: GridLayout, count: Int) {
        val context = grid.context

        fun dp(v: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), context.resources.displayMetrics).toInt()

        repeat(count) { index ->
            val cellIndex = index + 1
            val cell = FrameLayout(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = dp(120)
                    height = dp(120)
                    setMargins(dp(10), dp(10), dp(10), dp(10))
                    setGravity(Gravity.CENTER)
                }
                setBackgroundColor(0xFFF44336.toInt())
            }

            val label = TextView(context).apply {
                textSize = 18f
                setTextColor(0xFFFFFFFF.toInt())
                gravity = Gravity.CENTER
                visibility = View.GONE
            }

            cell.addView(label)

            var state = 0
            cell.setOnClickListener {
                when (state) {
                    0 -> {
                        label.text = game.getRoleForPlayer(cellIndex)
                        label.visibility = View.VISIBLE
                        state = 1
                    }
                    1 -> {
                        cell.setBackgroundColor(0xFF2196F3.toInt())
                        label.visibility = View.GONE
                        cell.isClickable = false
                    }
                }
            }

            grid.addView(cell)
        }
    }
}
