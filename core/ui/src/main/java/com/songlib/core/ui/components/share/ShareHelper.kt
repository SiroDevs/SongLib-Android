package com.songlib.core.ui.components.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** Shared helper for sharing text/images and copying to the clipboard. */
object ShareHelper {

    /** Share plain text via the Android share sheet. */
    fun shareText(context: Context, text: String, chooserTitle: String = "Share") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }

    /** Save [bitmap] to app cache then fire a share intent. */
    fun shareBitmap(context: Context, bitmap: Bitmap, fileName: String = "songlib_share.png") {
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share"))
    }

    /** Copy [text] to the system clipboard. */
    fun copyText(context: Context, text: String, label: String = "SongLib") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }
}
