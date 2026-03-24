package com.example.wavecast.core.network.utils

import android.util.Xml
import com.example.wavecast.core.network.model.EpisodeResponse
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class RssParser {
    fun parse(inputStream: InputStream): List<EpisodeResponse> {
        val parser = Xml.newPullParser()
        // itunes:duration 등을 읽기 위해 네임스페이스 활성화 가능하지만, 
        // 간단하게 처리하기 위해 태그 이름으로 접근합니다.
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
        
        val episodes = mutableListOf<EpisodeResponse>()
        var eventType = parser.eventType
        
        var currentTitle: String? = null
        var currentAudioUrl: String? = null
        var currentImageUrl: String? = null
        var currentDuration: String? = null
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "title" -> currentTitle = parser.nextText()
                        "enclosure" -> {
                            currentAudioUrl = parser.getAttributeValue(null, "url")
                        }
                        "itunes:image" -> {
                            currentImageUrl = parser.getAttributeValue(null, "href")
                        }
                        "itunes:duration" -> {
                            currentDuration = parser.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (tagName == "item") {
                        if (currentTitle != null && currentAudioUrl != null) {
                            episodes.add(
                                EpisodeResponse(
                                    title = currentTitle,
                                    audioUrl = currentAudioUrl,
                                    imageUrl = currentImageUrl,
                                    duration = currentDuration
                                )
                            )
                        }
                        currentTitle = null
                        currentAudioUrl = null
                        currentImageUrl = null
                        currentDuration = null
                    }
                }
            }
            eventType = parser.next()
        }
        return episodes
    }
}
