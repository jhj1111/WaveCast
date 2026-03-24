package com.example.wavecast.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@RequiresApi(Build.VERSION_CODES.P)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.example.wavecast",
        includeInStartupProfile = true
    ) {
        pressHome()
        startActivityAndWait()

        // 1. 앱의 메인 콘텐츠(리스트)가 나타날 때까지 최대 15초 대기 (사양 낮은 PC 배려)
        val podcastList = device.wait(
            Until.findObject(By.res("home:podcastList")),
            15_000
        )

        // 2. 리스트가 발견되었을 때만 인터랙션 수행
        if (podcastList != null) {
            // 스크롤 동작을 통해 관련 클래스들이 프로파일에 포함되도록 유도
            podcastList.setGestureMargin(device.displayWidth / 5)
            podcastList.fling(Direction.DOWN)
            
            // 잠시 대기하여 렌더링 로직 기록
            device.waitForIdle()
        }
    }
}
