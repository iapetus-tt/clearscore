package com.frogbucket.clearscore.techtest.widget.donut

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.frogbucket.clearscore.techtest.model.CreditReportInfo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], manifest= Config.NONE)
internal class DonutTest {
    private lateinit var activity: Activity
    private lateinit var mockCanvas: Canvas

    @Before
    fun setUp() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        mockCanvas = mock(Canvas::class.java)
    }

    @After
    fun tearDownMockito() {
        validateMockitoUsage()
    }

    @Test
    public fun whenRatingSet_thenHeaderFooterDrawn() {
        testHeaderFooter("header", "footer")
    }

    @Test
    public fun whenHeaderFooterContainValues_thenHeaderFooterTranslated() {
        testHeaderFooter("minimum is %%m", "%%M is maximum", "minimum is 0", "400 is maximum")
    }

    @Test
    public fun whenCreditNull_thenNoContent() {
        val reportInfo : CreditReportInfo? = null
        val donut = Donut(activity, null, 0)
        donut.creditReportInfo = reportInfo
        callOnDraw(donut, mockCanvas)
        verify(mockCanvas, never()).drawArc(
            anyFloat(), anyFloat(), anyFloat(),
            anyFloat(), anyFloat(), anyFloat(),
            anyBoolean(), any(Paint::class.java)
        )
        verify(mockCanvas, never()).drawText(
            anyString(), anyFloat(), anyFloat(), any(Paint::class.java)
        )
    }

    @Test
    public fun whenCreditMatchesMinimum_thenZeroArc() {
        testArcLength(CreditReportInfo(0, 0, 500), 0f)
        testArcLength(CreditReportInfo(200, 200, 500), 0f)
    }

    @Test
    public fun whenCreditMatchesMaximum_thenFullArc() {
        testArcLength(CreditReportInfo(400, 0, 400), 360f)
        testArcLength(CreditReportInfo(400, 200, 400), 360f)
    }

    @Test
    public fun whenCreditInRange_thenIntermediateValues() {
        testArcLength(CreditReportInfo(100, 0, 400), 90f)
        testArcLength(CreditReportInfo(300,200, 400), 180f)
        testArcLength(CreditReportInfo(0, -300, 100), 270f)
    }

    // Test some pathological cases
    @Test
    public fun whenRangeZero_thenNoCrash() {
        val reportInfo : CreditReportInfo? = CreditReportInfo(0, 0, 0)
        val donut = Donut(activity, null, 0)
        donut.creditReportInfo = reportInfo
        callOnDraw(donut, mockCanvas)
    }

    @Test
    public fun whenScoreOutsideRange_thenNoCrash() {
        val reportInfo : CreditReportInfo? = CreditReportInfo(0, 50, 100)
        val donut = Donut(activity, null, 0)
        donut.creditReportInfo = reportInfo
        callOnDraw(donut, mockCanvas)
    }

    private fun testHeaderFooter(header: String, footer: String, outputHeader : String = header, outputFooter : String = footer) {
        reset(mockCanvas)
        val donut = Donut(activity, null, 0)
        donut.messageAbove = header
        donut.messageBelow = footer
        donut.creditReportInfo = CreditReportInfo(200, 0, 400)
        callOnDraw(donut, mockCanvas)
        verify(mockCanvas).drawText(eq("200"), anyFloat(), anyFloat(), any(Paint::class.java))
        verify(mockCanvas).drawText(eq(outputHeader), anyFloat(), anyFloat(), any(Paint::class.java))
        verify(mockCanvas).drawText(eq(outputFooter), anyFloat(), anyFloat(), any(Paint::class.java))
    }

    private fun testArcLength(reportInfo: CreditReportInfo, arc: Float) {
        reset(mockCanvas)
        val donut = Donut(activity, null, 0)
        donut.creditReportInfo = reportInfo
        callOnDraw(donut, mockCanvas)
        verify(mockCanvas).drawArc(
            anyFloat(), anyFloat(), anyFloat(),
            anyFloat(),  Matchers.eq(-90f), eq(arc),
            anyBoolean(), any(Paint::class.java)
        )
    }

    private fun callOnDraw(view: View, canvas: Canvas) {
        val method = view.javaClass.getDeclaredMethod("onDraw", Canvas::class.java)
        method.invoke(view, canvas)
    }
}