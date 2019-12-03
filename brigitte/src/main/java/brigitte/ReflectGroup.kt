@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import org.slf4j.LoggerFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 19. <p/>
 */


//https://fossies.org/linux/kotlin/core/reflection.jvm/src/kotlin/reflect/jvm/internal/KDeclarationContainerImpl.kt

// Reflect.create<ClassName>(ClassName::class.java)

object Reflect {
    val mLog = LoggerFactory.getLogger(Reflect::class.java)

    /**
     * 클래스의 경로를 지정하고 인자 타입과 인자를 전달하여 클래스를 인스턴스 시킨다.
     *
     * ```
     * Reflect.create("com.hanwha.sample.TestClass")
     * ```
     * or
     *
     * ```
     * Reflect.create("com.hanwha.sample.TestClass", Params().apply {
     *      argTypes = arrayListOf(Activity.class),
     *      argv = arrayListOf(mActivity)
     * })
     *
     * ```
     */
//    inline fun <reified T> create(clazzPath: String, params: Params? = null): T {
//        try {
//            return create(params)
//        } catch (e: Exception) {
//            throw java.lang.RuntimeException(e)
//        }
//    }

    /**
     * 클래스형틀 지정하고 인자 타입과 인자를 전달하여 클래스를 인스턴스 시킨다.
     */
    inline fun <reified T> create(params: Params? = null): T {
        val clazz = T::class.java
        if (clazz.isInterface) {
            throw RuntimeException("Specified class is hsp4: " + clazz.name)
        }

        try {
            return params?.argv?.let {
                val constructor = clazz.getDeclaredConstructor(*params.argTypes!!.toTypedArray())

                constructor.newInstance(*it.toTypedArray())
            } ?: clazz.newInstance()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * 클래스 리스트를 전달하고 인스턴스 된 클래스 리스트를 전달 받는다.
     */
    inline fun <reified T> create(classes: List<Class<out T>>?, params: Params? = null): List<T> {
        val list: ArrayList<T> = arrayListOf()

        classes?.forEach { _ ->
            list.add(create(params))
        }

        return list
    }

    inline fun <reified K, reified T> create(classes: Map<K, Class<out T>>?, params: Params? = null): Map<K, T> {
        val map: HashMap<K, T> = hashMapOf()

        classes?.forEach { (k, _) -> map[k] = create(params) }

        return map
    }

    inline fun method(obj: Any, name: String, params: Params? = null) {
        try {
            obj.javaClass.apply {
                if (params == null) {
                    getDeclaredMethod(name).invoke(obj)
                } else {
                    params.let {
                        getDeclaredMethod(name, *it.argTypes!!.toTypedArray())
                            .invoke(obj, *it.argv!!.toTypedArray())
                    }
                }
            }
        } catch (e: Exception) {
            if (Reflect.mLog.isDebugEnabled) {
                e.printStackTrace()
            }

            mLog.error("ERROR: ${e.message}")
        }
    }

//    inline fun classType(obj: Any, index: Int) =
//        (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[index]

    inline fun <reified T> classType(obj: Any, index: Int) =
        (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[index] as T

    data class Params(val argTypes: List<Class<out Any>>? = null, val argv : List<Any>? = null) {
        constructor(argTypes: Class<out Any>, argv: Any) : this(listOf(argTypes), listOf(argv))
    }
}

