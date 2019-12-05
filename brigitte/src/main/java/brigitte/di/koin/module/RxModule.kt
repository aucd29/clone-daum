package brigitte.di.koin.module

import io.reactivex.disposables.CompositeDisposable
import org.koin.dsl.module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-05-29 <p/>
 */

val rxModule = module {
    val mComposite = CompositeDisposable()

    single { mComposite }
}

