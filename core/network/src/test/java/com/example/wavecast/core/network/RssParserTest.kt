package com.example.wavecast.core.network

import com.example.wavecast.core.network.utils.RssParser
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream

@RunWith(RobolectricTestRunner::class) // Robolectric 환경에서 테스트 실행
class RssParserTest {

    private val parser = RssParser()

    @Test
    fun `RSS XML 파싱 시 에피소드 정보를 정확히 추출한다`() {
        val xml = """
            <rss xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" version="2.0">
                <channel>
                    <item>
                        <title>Episode 1</title>
                        <enclosure url="https://example.com/audio1.mp3" length="12345" type="audio/mpeg"/>
                        <itunes:image href="https://example.com/image1.jpg"/>
                        <itunes:duration>00:30:00</itunes:duration>
                    </item>
                    <item>
                        <title>Episode 2</title>
                        <enclosure url="https://example.com/audio2.mp3" length="67890" type="audio/mpeg"/>
                        <itunes:duration>45:00</itunes:duration>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        val inputStream = ByteArrayInputStream(xml.toByteArray())
        val episodes = parser.parse(inputStream)

        assertEquals(2, episodes.size)
        
        // 첫 번째 에피소드 검증
        assertEquals("Episode 1", episodes[0].title)
        assertEquals("https://example.com/audio1.mp3", episodes[0].audioUrl)
        assertEquals("https://example.com/image1.jpg", episodes[0].imageUrl)
        assertEquals("00:30:00", episodes[0].duration)

        // 두 번째 에피소드 검증 (이미지 누락 케이스)
        assertEquals("Episode 2", episodes[1].title)
        assertEquals("https://example.com/audio2.mp3", episodes[1].audioUrl)
        assertEquals(null, episodes[1].imageUrl)
        assertEquals("45:00", episodes[1].duration)
    }

    @Test
    fun `잘못된 형식의 XML은 빈 리스트를 반환한다`() {
        val xml = "<invalid>xml</invalid>"
        val inputStream = ByteArrayInputStream(xml.toByteArray())
        val episodes = parser.parse(inputStream)

        assertEquals(0, episodes.size)
    }
}
