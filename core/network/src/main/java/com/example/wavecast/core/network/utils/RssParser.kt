package com.example.wavecast.core.network.utils

import android.util.Xml
import com.example.wavecast.core.network.model.EpisodeResponse
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class RssParser {
    fun parse(inputStream: InputStream): List<EpisodeResponse> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
        
        val episodes = mutableListOf<EpisodeResponse>()
        var eventType = parser.eventType
        
        var currentTitle: String? = null
        var currentAudioUrl: String? = null
        var currentImageUrl: String? = null
        
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
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (tagName == "item") {
                        if (currentTitle != null && currentAudioUrl != null) {
                            episodes.add(
                                EpisodeResponse(
                                    title = currentTitle,
                                    audioUrl = currentAudioUrl,
                                    imageUrl = currentImageUrl
                                )
                            )
                        }
                        currentTitle = null
                        currentAudioUrl = null
                        currentImageUrl = null
                    }
                }
            }
            eventType = parser.next()
        }
        return episodes
    }
}
