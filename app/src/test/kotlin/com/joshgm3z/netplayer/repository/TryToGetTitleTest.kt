package com.joshgm3z.netplayer.repository

import junit.framework.TestCase.assertEquals
import org.junit.Test

class TryToGetTitleTest {
    @Test
    fun test_tryToGetTitle() {
        val url = "https://rd2.seedr.cc/ff_get/3931638/5959460378/The.Drama.2026.1080p." +
                "WEBRip.AAC5.1.10bits.x265-Rapta.mkv?st=z3otH1pk1wu9ZgrqqX-GxQ&e=1786223834"
        assertEquals(
            "The.Drama.2026.1080p.WEBRip.AAC5.1.10bits.x265-Rapta.mkv",
            url.tryToGetTitle()
        )
    }

    @Test
    fun test_tryToGetTitle2() {
        val url = "https://nw33.seedr.cc/ff_get/3931638/5959457683/Leon.the.Professional.Extended.1994.720p.BrRip.x264.YIFY.mp4?st=t50zNLne5f-Dm6e7R9xBdA&e=1786308161"
        assertEquals(
            "Leon.the.Professional.Extended.1994.720p.BrRip.x264.YIFY.mp4",
            url.tryToGetTitle()
        )
    }
}