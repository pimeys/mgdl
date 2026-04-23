package com.pimeys.mgdl

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : Activity() {
    private lateinit var mgdlInput: EditText
    private lateinit var mmolInput: EditText
    private var updating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(LIGHT_BLUE)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(72), dp(24), dp(24))
        }
        scrollView.addView(
            root,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(20), dp(24), dp(20))
            background = cardBackground()
        }
        root.addView(
            card,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        val icon = TextView(this).apply {
            text = "●"
            setTextColor(BLUE)
            textSize = 24f
            gravity = Gravity.CENTER_HORIZONTAL
        }
        card.addView(icon, matchWrap())

        val title = TextView(this).apply {
            text = "Blood glucose"
            setTextColor(TEXT)
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 28f
            gravity = Gravity.CENTER_HORIZONTAL
        }
        card.addView(title, matchWrap())

        val subtitle = TextView(this).apply {
            text = "Convert between mg/dL and mmol/L"
            setTextColor(MUTED)
            textSize = 16f
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val subtitleParams = matchWrap().apply {
            setMargins(0, dp(6), 0, dp(18))
        }
        card.addView(subtitle, subtitleParams)

        mgdlInput = createInput("mg/dL", "Milligrams per deciliter")
        mmolInput = createInput("mmol/L", "Millimoles per liter")

        card.addView(createField("mg/dL", mgdlInput))
        card.addView(createField("mmol/L", mmolInput))
        setContentView(scrollView)

        mgdlInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateMmol(s.toString())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        mmolInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateMgdl(s.toString())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun createField(unit: String, input: EditText): LinearLayout {
        val field = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(14), dp(18), dp(10))
            background = fieldBackground()
        }

        val label = TextView(this).apply {
            text = unit
            setTextColor(BLUE)
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
        }
        field.addView(label, matchWrap())
        field.addView(input, matchWrap())

        field.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            setMargins(0, 0, 0, dp(12))
        }
        return field
    }

    private fun createInput(hint: String, description: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            contentDescription = description
            setTextColor(TEXT)
            setHintTextColor(Color.rgb(144, 164, 174))
            textSize = 34f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setSingleLine(true)
            setSelectAllOnFocus(false)
            imeOptions = EditorInfo.IME_ACTION_DONE
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            filters = arrayOf(InputFilter.LengthFilter(12))
            setPadding(0, 0, 0, 0)
            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun updateMmol(text: String) {
        if (updating) {
            return
        }

        val mgdl = parse(text)
        if (mgdl == null) {
            setText(mmolInput, "")
            return
        }

        setText(
            mmolInput,
            mgdl.divide(CONVERSION_FACTOR, 2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
        )
    }

    private fun updateMgdl(text: String) {
        if (updating) {
            return
        }

        val mmol = parse(text)
        if (mmol == null) {
            setText(mgdlInput, "")
            return
        }

        setText(
            mgdlInput,
            mmol.multiply(CONVERSION_FACTOR).setScale(0, RoundingMode.HALF_UP).toPlainString(),
        )
    }

    private fun parse(text: String): BigDecimal? {
        if (text.isEmpty() || text == ".") {
            return null
        }

        return try {
            BigDecimal(text)
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun setText(input: EditText, text: String) {
        updating = true
        input.setText(text)
        input.setSelection(input.text.length)
        updating = false
    }

    private fun cardBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(SURFACE)
            cornerRadius = dp(28).toFloat()
        }
    }

    private fun fieldBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.rgb(248, 251, 255))
            cornerRadius = dp(18).toFloat()
            setStroke(dp(1), Color.rgb(187, 222, 251))
        }
    }

    private fun matchWrap(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private companion object {
        val CONVERSION_FACTOR: BigDecimal = BigDecimal("18.0182")
        const val BLUE: Int = 0xFF1565C0.toInt()
        const val LIGHT_BLUE: Int = 0xFFE3F2FD.toInt()
        const val SURFACE: Int = Color.WHITE
        const val TEXT: Int = 0xFF18202B.toInt()
        const val MUTED: Int = 0xFF5C6A7A.toInt()
    }
}
