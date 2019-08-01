@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-31 <p/>
 */

inline fun Activity.singleTimer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Single.timer(delay, unit).subscribeOn(Schedulers.io())

inline fun Fragment.singleTimer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Single.timer(delay, unit).subscribeOn(Schedulers.io())

inline fun ViewModel.singleTimer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Single.timer(delay, unit).subscribeOn(Schedulers.io())

inline fun Activity.timer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Observable.timer(delay, unit).subscribeOn(Schedulers.io())

inline fun Fragment.timer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Observable.timer(delay, unit).subscribeOn(Schedulers.io())

inline fun ViewModel.timer(delay: Long = 300, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    Observable.timer(delay, unit).subscribeOn(Schedulers.io())

//

inline fun <T> Single<T>.subscribeOnIo() =
    subscribeOn(Schedulers.io())

inline fun <T> Single<T>.subscribeOnMain() =
    subscribeOn(AndroidSchedulers.mainThread())


inline fun <T> Observable<T>.subscribeOnIo() =
    subscribeOn(Schedulers.io())

inline fun <T> Observable<T>.subscribeOnMain() =
    subscribeOn(AndroidSchedulers.mainThread())


inline fun <T> Flowable<T>.subscribeOnIo() =
    subscribeOn(Schedulers.io())

inline fun <T> Flowable<T>.subscribeOnMain() =
    subscribeOn(AndroidSchedulers.mainThread())

inline fun Completable.subscribeOnIo() =
    subscribeOn(Schedulers.io())

inline fun Completable.subscribeOnMain() =
    subscribeOn(AndroidSchedulers.mainThread())

//


inline fun <T> Single<T>.observeOnIo() =
    observeOn(Schedulers.io())

inline fun <T> Single<T>.observeOnMain() =
    observeOn(AndroidSchedulers.mainThread())


inline fun <T> Observable<T>.observeOnIo() =
    observeOn(Schedulers.io())

inline fun <T> Observable<T>.observeOnMain() =
    observeOn(AndroidSchedulers.mainThread())


inline fun <T> Flowable<T>.observeOnIo() =
    observeOn(Schedulers.io())

inline fun <T> Flowable<T>.observeOnMain() =
    observeOn(AndroidSchedulers.mainThread())


inline fun Completable.observeOnIo() =
    observeOn(Schedulers.io())

inline fun Completable.observeOnMain() =
    observeOn(AndroidSchedulers.mainThread())

//

inline fun <T> Single<T>.subscribeOnIoAndObserveOnMain() =
    subscribeOnIo().observeOnMain()

inline fun <T> Observable<T>.subscribeOnIoAndObserveOnMain() =
    subscribeOnIo().observeOnMain()

inline fun <T> Flowable<T>.subscribeOnIoAndObserveOnMain() =
    subscribeOnIo().observeOnMain()

inline fun Completable.subscribeOnIoAndObserveOnMain() =
    subscribeOnIo().observeOnMain()

inline fun <T> Single<T>.subscribeOnIoAndObserveOnIo() =
    subscribeOnIo().observeOnIo()

inline fun <T> Observable<T>.subscribeOnIoAndObserveOnIo() =
    subscribeOnIo().observeOnIo()

inline fun <T> Flowable<T>.subscribeOnIoAndObserveOnIo() =
    subscribeOnIo().observeOnIo()

inline fun Completable.subscribeOnIoAndObserveOnIo() =
    subscribeOnIo().observeOnIo()

//
//
//
//// CompositeDisposable 를 강제화 하고 코드를 줄이기 위해 inline 추가
//
//inline fun <T> Single<T>.subscribe(dp: CompositeDisposable,
//                                   noinline callback: () -> Unit,
//                                   noinline error: ((Throwable) -> Unit)? = null) =
//    dp.add(error?.let { errorCallback ->
//        subscribe({ callback.invoke() }, { errorCallback.invoke(it) })
//    } ?: run {
//        subscribe { _ -> callback.invoke() }
//    })
//
//inline fun <T> Observable<T>.subscribe(dp: CompositeDisposable,
//                                       noinline callback: (T) -> Unit,
//                                       noinline error: ((Throwable) -> Unit)? = null) =
//    dp.add(error?.let { errorCallback ->
//        subscribe({ callback.invoke(it) }, { errorCallback.invoke(it) })
//    } ?: run {
//        subscribe { callback.invoke(it) }
//    })
//
//inline fun <T> Flowable<T>.subscribe(dp: CompositeDisposable,
//                                     noinline callback: (T) -> Unit,
//                                     noinline error: ((Throwable) -> Unit)? = null) =
//    dp.add(error?.let { errorCallback ->
//        subscribe({ callback.invoke(it) }, { errorCallback.invoke(it) })
//    } ?: run {
//        subscribe { callback.invoke(it) }
//    })

//

//inline fun <T> Single<T>.ioThreadSubscribe(dp: CompositeDisposable,
//                                           noinline callback: () -> Unit ,
//                                           noinline error: ((Throwable) -> Unit)? = null) =
//    observeOnIo().subscribe(dp, callback, error)
//
//inline fun <T> Single<T>.mainThreadSubscribe(dp: CompositeDisposable,
//                                             noinline callback: () -> Unit,
//                                             noinline error: ((Throwable) -> Unit)? = null) =
//    observeOnMain().subscribe(dp, callback, error)
//
//inline fun <T> Observable<T>.ioThreadSubscribe(dp: CompositeDisposable,
//                                               noinline callback: (T) -> Unit,
//                                               noinline error: ((Throwable) -> Unit)? = null) =
//    observeOnIo().subscribe(dp, callback, error)
//
//inline fun <T> Observable<T>.mainThreadSubscribe(dp: CompositeDisposable,
//                                                 noinline callback: (T) -> Unit,
//                                                 noinline error: ((Throwable) -> Unit)? = null) =
//    observeOnMain().subscribe(dp, callback, error)
//
//
//inline fun <T> Flowable<T>.ioThreadSubscribe(dp: CompositeDisposable,
//                                             noinline callback: (T) -> Unit,
//                                             noinline error: ((Throwable) -> Unit)? = null) =
//    observeOnIo().subscribe(dp, callback, error)
//
//inline fun <T> Flowable<T>.mainThreadSubscribe(dp: CompositeDisposable,
//                                               noinline callback: (T) -> Unit,
//                                               noinline error: ((Throwable) -> Unit)? = null) =
//    observeOnMain().subscribe(dp, callback, error)

