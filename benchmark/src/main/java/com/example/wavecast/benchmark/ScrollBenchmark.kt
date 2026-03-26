package com.example.wavecast.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScrollBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollListCompilationNone() = scrollList(CompilationMode.None())

    @Test
    fun scrollListCompilationPartial() = scrollList(CompilationMode.Partial())

    private fun scrollList(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.wavecast",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()

        val list = device.findObject(By.res("home:podcastList"))
        
        // Wait until the list is visible
        device.wait(Until.hasObject(By.res("home:podcastList")), 5000)

        if (list != null) {
            list.setGestureMargin(device.displayWidth / 5)
            list.fling(Direction.DOWN)
        }
    }
}
