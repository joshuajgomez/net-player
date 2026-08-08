package com.joshgm3z.netplayer.ui.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.joshgm3z.netplayer.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ApkInstaller
@Inject constructor(
    @param:ApplicationContext
    private val context: Context,
) {
    fun installApk(apkFile: File) {
        Logger.debug("apkFile = [${apkFile.path}]")
        if (apkFile.exists()) {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setDataAndType(contentUri, "application/vnd.android.package-archive")
            }
            context.startActivity(installIntent)
        }
    }
}