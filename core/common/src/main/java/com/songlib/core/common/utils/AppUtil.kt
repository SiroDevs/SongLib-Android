package com.songlib.core.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun refineTitle(textTitle: String): String {
    return textTitle.replace("''", "'")
}

fun refineContent(contentText: String): String {
    return contentText.replace("''", "'").replace("#", " ")
}

fun songItemTitle(number: Int, title: String): String {
    return if (number != 0) {
        "$number. ${refineTitle(title)}"
    } else {
        refineTitle(title)
    }
}

fun getSongVersesX(songContent: String): List<String> {
    return songContent.split("#").map { it.trim() }.filter { it.isNotEmpty() }
}

fun getSongVerses(songContent: String): List<String> {
    return songContent.split("##").map { it.replace("#", "\n") }
}

fun songCopyString(title: String, content: String): String {
    return "$title\n\n$content"
}

fun bookCountString(title: String, count: Int): String {
    return "$title ($count)"
}

fun lyricsString(lyrics: String): String {
    return lyrics.replace("#", "\n").replace("''", "'")
}

fun songViewerTitle(number: Int, title: String, alias: String): String {
    var songTitle = "$number. ${refineTitle(title)}"

    if (alias.length > 2 && title != alias) {
        songTitle += " (${refineTitle(alias)})"
    }

    return songTitle
}

fun songShareString(title: String, content: String): String {
    return "$title\n\n$content\n\nvia #SongLib https://songlib.vercel.app"
}

fun verseOfString(number: String, count: Int): String {
    return "VERSE $number of $count"
}

fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val minutes = diff / 60000
    val hours = diff / (3600000)
    val days = diff / (86400000)

    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hr ago"
        else -> "$days d ago"
    }
}


fun openUrl(context: Context, url: String) {
    runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

/** Opens the app if installed, otherwise its Play Store page (browser as a last resort). */
fun openAppOrPlayStore(context: Context, packageName: String) {
    val launch = context.packageManager.getLaunchIntentForPackage(packageName)
    if (launch != null) {
        runCatching { context.startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            .onSuccess { return }
    }
    val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(market) }.onFailure {
        openUrl(context, "https://play.google.com/store/apps/details?id=$packageName")
    }
}
