package com.example.myapplication

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity

class ImpostorActivity : ComponentActivity() {

    private lateinit var game: ImpostorGame
    private var selectedMode: GameMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.impostor_screen)

        game = ImpostorGame()

        val modeScreen = findViewById<LinearLayout>(R.id.inputScreen_original)
        val inputScreen = findViewById<LinearLayout>(R.id.inputScreen)
        val squaresScreen = findViewById<LinearLayout>(R.id.squaresScreen)

        val inputNumber = findViewById<EditText>(R.id.inputNumber)
        val generateBtn = findViewById<Button>(R.id.generateBtn)
        val grid = findViewById<GridLayout>(R.id.grid)
        val resetBtn = findViewById<Button>(R.id.reset)

        val btnFaraAjutor = findViewById<Button>(R.id.btnFaraAjutor)
        val btnCuAjutor = findViewById<Button>(R.id.btnCuAjutor)
        val btnCuvantSimilar = findViewById<Button>(R.id.btnCuvantSimilar)

        // ====== SELECTARE MOD ======
        btnFaraAjutor.setOnClickListener {
            selectedMode = GameMode.FARA_AJUTOR
            modeScreen.visibility = View.GONE
            inputScreen.visibility = View.VISIBLE
        }

        btnCuAjutor.setOnClickListener {
            selectedMode = GameMode.CU_AJUTOR
            modeScreen.visibility = View.GONE
            inputScreen.visibility = View.VISIBLE
        }

        btnCuvantSimilar.setOnClickListener {
            selectedMode = GameMode.CUVANT_SIMILAR
            modeScreen.visibility = View.GONE
            inputScreen.visibility = View.VISIBLE
        }

        // ====== START JOC ======
        generateBtn.setOnClickListener {
            val players = inputNumber.text.toString().toInt()

            game.start(players, selectedMode!!)

            inputScreen.visibility = View.GONE
            squaresScreen.visibility = View.VISIBLE

            grid.removeAllViews()
            createSquares(grid, players)
        }


        // ====== RESET ======
        resetBtn.setOnClickListener {
            grid.removeAllViews()
            squaresScreen.visibility = View.GONE
            inputScreen.visibility = View.GONE
            modeScreen.visibility = View.VISIBLE
        }
    }

    private fun createSquares(grid: GridLayout, count: Int) {
        val context = grid.context

        fun dp(value: Int): Int =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value.toFloat(),
                context.resources.displayMetrics
            ).toInt()

        val size = dp(120)
        val margin = dp(10)

        repeat(count) { index ->
            val cellIndex = index + 1

            val cell = FrameLayout(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    setMargins(margin, margin, margin, margin)
                    setGravity(Gravity.CENTER)
                }
                setBackgroundColor(0xFFF44336.toInt())
            }
            val label = TextView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
                textSize = 18f
                setTextColor(0xFFFFFFFFa.toInt())
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                visibility = View.GONE
            }


            cell.addView(label)

            var state = 0


            cell.setOnClickListener {
                    when (state) {
                        0 -> {
                            label.text = game.getTextForPlayer(cellIndex)
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
