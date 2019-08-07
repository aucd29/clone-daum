package brigitte

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.module.LibraryGlideModule
import java.io.InputStream

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
 */

// 사용하기전에 읽어야할 문서
// http://bumptech.github.io/glide/doc/getting-started.html

// https://github.com/bumptech/glide/issues/1945

@GlideModule
class GlideModule: AppGlideModule()


/**
 * 어머낫...
 *
 * Avoid AppGlideModule in libraries
 * Libraries must not include AppGlideModule implementations.
 * Doing so will prevent any applications that depend on the library from
 * managing their dependencies or configuring options like
 * Glide’s cache sizes and locations.
 */

// https://bumptech.github.io/glide/doc/configuration.html#applications
@GlideModule
class OkhttpLibraryGlideModule: LibraryGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }
}