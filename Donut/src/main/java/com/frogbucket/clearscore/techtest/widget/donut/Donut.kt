package com.frogbucket.clearscore.techtest.widget.donut

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.frogbucket.clearscore.techtest.R
import com.frogbucket.clearscore.techtest.model.CreditReportInfo

/**
 * View representing a donut view of a number within a range.
 *
 * Currently tied to the Clearscore credit report info model.
 */
class Donut @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {
    var creditReportInfo: CreditReportInfo? = null
        set(value) {
            field = value
            this.invalidate()
        }

    var largeText = 0f
    var smallText = 0f
    var messageAbove = ""
    var messageBelow = ""
    var minColor = 0
    var maxColor = 0
    var barWidth = 0f

    private var radius = 0f
    private var donutRadius = 0f

    init {
        // Initialise values from layout attributes
        context.theme?.let {
            it.obtainStyledAttributes(
                attrs,
                R.styleable.Donut,
                0, 0).apply {
                try {
                    largeText = getDimension(R.styleable.Donut_valueSize, 80f)
                    smallText = getDimension(R.styleable.Donut_descriptionSize, 24f)
                    messageAbove = getString(R.styleable.Donut_header) ?: ""
                    messageBelow = getString(R.styleable.Donut_footer) ?: ""
                    minColor = getColor(R.styleable.Donut_minColor, Color.BLACK)
                    maxColor = getColor(R.styleable.Donut_maxColor, Color.BLACK)
                    barWidth = getDimension(R.styleable.Donut_barWidth, 8f)
                } finally {
                    recycle()
                }
            }
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = Math.min(width, height).toFloat() / 2f * 0.95f
        donutRadius = radius - barWidth * 1.5f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        canvas?.drawCircle(centerX, centerY, radius, circlePaint)
        canvas?.drawCircle(centerX, centerY, radius, outlinePaint)

        val gradient = SweepGradient(centerX, centerY, minColor, maxColor).apply {
            val matrix = Matrix()
            matrix.preRotate(-90f, centerX, centerY)
            setLocalMatrix(matrix)
        }
        val donutShader = Paint().apply {
            shader = gradient
            strokeWidth = barWidth;
            style = Paint.Style.STROKE
        }

        creditReportInfo?.let { creditRating ->
            val donutScale = if (creditRating.maxScoreValue == creditRating.minScoreValue) {
                0f
            } else {
                (creditRating.score - creditRating.minScoreValue).toFloat() / (creditRating.maxScoreValue - creditRating.minScoreValue).toFloat()
            }
            canvas?.drawArc(centerX - donutRadius, centerY - donutRadius, centerX + donutRadius, centerY + donutRadius, -90f, donutScale * 360f, false, donutShader)

            val textPaint = Paint().apply {
                val evaluator = ArgbEvaluator()
                color = evaluator.evaluate(donutScale, minColor, maxColor) as Int
                textSize = largeText
                textAlign = Paint.Align.CENTER
            }

            val smallTextPaint = Paint().apply {
                color = Color.BLACK
                textSize = smallText
                textAlign = Paint.Align.CENTER
            }

            val largeTextBaseline = centerY - ((textPaint.descent() + textPaint.ascent()) / 2f)
            canvas?.drawText("${creditRating.score}", centerX, largeTextBaseline, textPaint)
            canvas?.drawText(processText(messageAbove, creditRating), centerX, largeTextBaseline - largeText, smallTextPaint)
            canvas?.drawText(processText(messageBelow, creditRating), centerX, largeTextBaseline + smallText + textPaint.descent(), smallTextPaint)
        }
    }

    /**
     * Process text to insert values from the credit report info.
     *
     * %%m is replaced with the minimum score value
     *
     * %%M is replaced with the maximum score value
     */
    private fun processText(text: String, creditReportInfo: CreditReportInfo?): String {
        return if (creditReportInfo == null) {
            text
        } else {
            text
                .replace(Regex("%%M"), creditReportInfo.maxScoreValue.toString())
                .replace(Regex("%%m"), creditReportInfo.minScoreValue.toString())
        }
    }

    companion object {
        val outlinePaint: Paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 0f
            style = Paint.Style.STROKE
        }
        val circlePaint: Paint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }
}