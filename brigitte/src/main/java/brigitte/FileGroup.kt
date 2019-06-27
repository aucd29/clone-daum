package brigitte

import android.content.res.AssetManager
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */


/**
 * 파일이나 디렉토리의 전체 크기를 반환 한다
 */
fun File.totalLength(): Long {
    if (!exists()) {
        return 0
    }

    var total: Long = 0
    if (isDirectory) {
        listFiles().forEach {
            total += if (it.isDirectory) {
                it.totalLength()
            } else {
                it.length()
            }
        }
    } else {
        total = this.length()
    }

    return total
}

/**
 * 파일이나 디렉토리를 복사 한다 (하위 폴더 포함)
 */
fun File.copy(target: File, listener:FileListener? = null) {
    val log = LoggerFactory.getLogger(File::class.java)

    if (!this.exists()) {
        listener?.finish(FileListener.NOT_FOUND)
        return
    }

    listener?.apply {
        if (total == 0L) {
            total = totalLength()
        }
    }

    if (isDirectory) {
        listFiles().forEach {
            if (it.isDirectory) {
                it.copy(File(target, "${it.parentFile.name}/${it.name}"), listener)
            } else {
                if (!target.exists()) {
                    target.mkdirs()
                }

                if (log.isTraceEnabled) {
                    log.trace("${it.absolutePath} -> ${target.absolutePath}/${it.name}")
                }

                it.inputStream().use { ism ->
                    ism.copyFile(File(target, it.name), listener)
                }
            }
        }
    } else {
        if (!target.exists()) {
            target.mkdirs()
        }

        if (log.isTraceEnabled) {
            log.trace("${this.absolutePath} -> ${target.absolutePath}/${this.name}")
        }

        this.inputStream().use { ism ->
            ism.copyFile(File(target, this.name), listener)
        }
    }
}

private fun InputStream.copyFile(target: File, listener: FileListener? = null) {
    if (target.exists()) {
        target.delete()
    }

    target.outputStream().use { osm ->
        val buff  = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = read(buff)

        while (bytes >= 0) {
            osm.write(buff, 0, bytes)

            listener?.apply {
                current += bytes
                progress()
                trace()
            }

            // 이거 이쁘게 하는 방법이 없나. -_ -;
            // jump 는 while 갈 수가 없고...
            if (listener != null && listener.cancel) {
                // stop file copy commandEvent
                listener.finish(FileListener.CANCELED)
                break
            }

            bytes = read(buff)
        }

        listener?.apply {
            if (cancel) {
                return
            }

            if (total == 0L) {
                finish(FileListener.DONE)
            } else {
                if (percent == 100) {
                    finish(FileListener.DONE)
                }
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// ASSETS
//
////////////////////////////////////////////////////////////////////////////////////

fun AssetManager.copy(srcPath: String, destPath: String, listener: FileListener? = null) {
    val list = this.list(srcPath)
    listener?.let {
        if (it.total == 0L) {
            it.total = this.totalLength(srcPath)
        }
    }

    if (list?.size == 0) {
        this.open(srcPath).use {
            val f = File(destPath, srcPath)
            if (!f.parentFile.exists()) {
                f.parentFile.mkdirs()
            }

            this.open(srcPath).copyFile(f, listener)
        }
    } else {
        list?.forEach {
            this.copy("$srcPath/$it", destPath, listener)
        }
    }
}

fun AssetManager.totalLength(srcPath: String): Long {
    val list = this.list(srcPath)
    var length: Long = 0
    if (list?.size == 0) {
        this.open(srcPath).use {
            val buff  = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = 0

            do {
                length += bytes
                bytes = it.read(buff)
            } while (bytes >= 0)
        }

        return length
    } else {
        list?.forEach {
            length += this.totalLength("$srcPath/$it")
        }
    }

    return length
}

////////////////////////////////////////////////////////////////////////////////////
//
// FileListener
//
////////////////////////////////////////////////////////////////////////////////////

abstract class FileListener {
    private val log = LoggerFactory.getLogger(FileListener::class.java)

    companion object {
        const val DONE = 1
        const val NOT_FOUND = -1
        const val CANCELED  = -2
    }

    var cancel = false
    var percent: Int = 0
    var current: Long = 0
    var total: Long = 0
    val ignore: Boolean = false

    open fun progress() {
        if (total != 0L && !ignore) {
            percent = (current.toDouble() / total.toDouble() * 100.0).toInt()
        }
    }

    fun trace() {
        if (log.isTraceEnabled) {
            log.trace("$percent% (${current.toFileSizeString()} / ${total.toFileSizeString()})")
        }
    }

    abstract fun finish(code: Int)
}