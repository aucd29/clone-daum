package com.example.clone_daum.di.migration

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.*
import dagger.internal.Beta
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Beta
object AndroidXInjection {
    private val mLog = LoggerFactory.getLogger(AndroidInjection::class.java)

    /**
     * Injects `activity` if an associated [AndroidInjector] implementation can be found,
     * otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] doesn't implement [     ].
     */
    fun inject(activity: Activity) {
        checkNotNull(activity) { "activity" }

        val application = activity.application
        if (application !is HasActivityInjector) {
            throw RuntimeException(
                String.format(
                    "%s does not implement %s",
                    application.javaClass.canonicalName,
                    HasActivityInjector::class.java.canonicalName
                )
            )
        }

        val activityInjector = (application as HasActivityInjector).activityInjector()
        checkNotNull(activityInjector) {
            "${application.javaClass}.activityInjector() returned null"
        }

        activityInjector.inject(activity)
    }

    /**
     * Injects `fragment` if an associated [AndroidInjector] implementation can be found,
     * otherwise throws an [IllegalArgumentException].
     *
     *
     * Uses the following algorithm to find the appropriate `AndroidInjector<Fragment>` to
     * use to inject `fragment`:
     *
     *
     *  1. Walks the parent-fragment hierarchy to find the a fragment that implements [       ], and if none do
     *  1. Uses the `fragment`'s [activity][Fragment.getActivity] if it implements
     * [HasFragmentInjector], and if not
     *  1. Uses the [android.app.Application] if it implements [HasFragmentInjector].
     *
     *
     * If none of them implement [HasFragmentInjector], a [IllegalArgumentException] is
     * thrown.
     *
     * @throws IllegalArgumentException if no parent fragment, activity, or application implements
     * [HasFragmentInjector].
     */
    fun inject(fragment: Fragment) {
        checkNotNull(fragment) { "fragment" }
        val hasFragmentInjector = findHasXFragmentInjector(fragment)

        if (mLog.isDebugEnabled) {
            mLog.debug(String.format("An injector for %s was found in %s",
                    fragment.javaClass.canonicalName,
                    hasFragmentInjector.javaClass.canonicalName))
        }

        val fragmentInjector = hasFragmentInjector.xFragmentInjector()
        checkNotNull(fragmentInjector) {
            "${hasFragmentInjector.javaClass}.fragmentInjector() returned null"
        }

        fragmentInjector.inject(fragment)
    }

    private fun findHasXFragmentInjector(fragment: Fragment): HasXFragmentInjector {
        var parentFragment = fragment
        parentFragment = parentFragment.parentFragment!!

        while (parentFragment != null) {
            if (parentFragment is HasXFragmentInjector) {
                return parentFragment
            }

            parentFragment = parentFragment.parentFragment!!
        }

        val activity = fragment.activity!!
        if (activity is HasXFragmentInjector) {
            return activity
        }

        if (activity.application is HasXFragmentInjector) {
            return activity.application as HasXFragmentInjector
        }

        throw IllegalArgumentException(
            String.format("No injector was found for %s", fragment.javaClass.canonicalName)
        )
    }

    /**
     * Injects `service` if an associated [AndroidInjector] implementation can be found,
     * otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] doesn't implement [     ].
     */
    fun inject(service: Service) {
        checkNotNull(service) { "service" }
        val application = service.application
        if (application !is HasServiceInjector) {
            throw RuntimeException(
                String.format(
                    "%s does not implement %s",
                    application.javaClass.canonicalName,
                    HasServiceInjector::class.java.canonicalName
                )
            )
        }

        val serviceInjector = (application as HasServiceInjector).serviceInjector()
        checkNotNull(serviceInjector) {
            "${application.javaClass}.serviceInjector() returned null"
        }

        serviceInjector.inject(service)
    }

    /**
     * Injects `broadcastReceiver` if an associated [AndroidInjector] implementation can
     * be found, otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] from [     ][Context.getApplicationContext] doesn't implement [HasBroadcastReceiverInjector].
     */
    fun inject(broadcastReceiver: BroadcastReceiver, context: Context) {
        checkNotNull(broadcastReceiver) { "broadcastReceiver" }
        checkNotNull(context) { "context" }
        val application = context.applicationContext as Application
        if (application !is HasBroadcastReceiverInjector) {
            throw RuntimeException(
                String.format(
                    "%s does not implement %s",
                    application.javaClass.canonicalName,
                    HasBroadcastReceiverInjector::class.java.canonicalName
                )
            )
        }

        val broadcastReceiverInjector = (application as HasBroadcastReceiverInjector).broadcastReceiverInjector()
        checkNotNull(broadcastReceiverInjector) {
            "${application.javaClass}.broadcastReceiverInjector() returned null"
        }

        broadcastReceiverInjector.inject(broadcastReceiver)
    }

    /**
     * Injects `contentProvider` if an associated [AndroidInjector] implementation can be
     * found, otherwise throws an [IllegalArgumentException].
     *
     * @throws RuntimeException if the [Application] doesn't implement [     ].
     */
    fun inject(contentProvider: ContentProvider) {
        checkNotNull(contentProvider) { "contentProvider" }
        val application = contentProvider.context!!.applicationContext as Application
        if (application !is HasContentProviderInjector) {
            throw RuntimeException(
                String.format(
                    "%s does not implement %s",
                    application.javaClass.canonicalName,
                    HasContentProviderInjector::class.java.canonicalName
                )
            )
        }

        val contentProviderInjector = (application as HasContentProviderInjector).contentProviderInjector()
        checkNotNull(contentProviderInjector) {
            "${application.javaClass}.contentProviderInjector() returned null"
        }

        contentProviderInjector.inject(contentProvider)
    }
}
