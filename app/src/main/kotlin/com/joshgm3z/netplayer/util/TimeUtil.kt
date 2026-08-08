package com.joshgm3z.netplayer.util

import java.time.Duration
import java.time.Instant

fun Long.relativeTime(now: Instant = Instant.now()): String {
    println("target time = $this")
    val target = Instant.ofEpochMilli(this)
    val duration = Duration.between(target, now)

    val seconds = duration.seconds
    return when {
        seconds < 60 -> "Just now"
        seconds < 3600 -> {
            val mins = seconds / 60
            "${mins}m ago"
        }

        seconds < 86400 -> {
            val hours = seconds / 3600
            "${hours}h ago"
        }

        seconds < 604800 -> { // Less than 1 week
            val days = seconds / 86400
            "${days}d ago"
        }

        seconds < 2592000 -> { // Less than 30 days
            val weeks = seconds / 604800
            "${weeks}w ago"
        }

        seconds < 31536000 -> { // Less than 1 year (365 days)
            val months = seconds / 2592000
            if (months == 1L) "1 month ago" else "$months months ago"
        }

        else -> {
            val years = seconds / 31536000
            "${years}y ago"
        }
    }
}

fun Long.toTextTime(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}m")
    }.trim()
}