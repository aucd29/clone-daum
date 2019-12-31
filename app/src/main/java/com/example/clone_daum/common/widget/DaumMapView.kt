package com.example.clone_daum.common.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import net.daum.mf.map.api.MapLayout
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-31 <p/>
 *
 * 기존 MapLayout 이 사용하기 좀 =_ = 구려서 해서 wrapping 함
 */

class DaumMapView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attr, defStyle),
    MapView.OpenAPIKeyAuthenticationResultListener,
    MapView.MapViewEventListener {

    private var mMapLayout: MapLayout? = null

    val mapView: MapView?
        get() = mMapLayout?.mapView

    var callback: ((type: Int, mapPoint: Any?) -> Unit)? = null

    fun initMapLayout(activity: Activity) {
        MapLayout(activity).let {
            it.mapView.apply {
                setOpenAPIKeyAuthenticationResultListener(this@DaumMapView)
                setMapViewEventListener(this@DaumMapView)
                mapType = MapView.MapType.Standard
            }

            addView(it, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

            mMapLayout = it
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MapView.OpenAPIKeyAuthenticationResultListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onDaumMapOpenAPIKeyAuthenticationResult(view: MapView?, code: Int, message: String?) {
        if (mLog.isInfoEnabled) {
            mLog.info("Open API Key Authentication Result : code=$code, message=$message")
        }

        callback?.invoke(AUTH_RESULT, code to message)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MapView.MapViewEventListener
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onMapViewInitialized(map: MapView?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("MapView had loaded. Now, MapView APIs could be called safely")
        }
        map?.apply {
            setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.537229,127.005515), 2, true)
        }

        callback?.invoke(INIT, null)
    }

    override fun onMapViewCenterPointMoved(map: MapView?, mapPoint: MapPoint?) {
        mapPoint?.mapPointGeoCoord?.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("MapView onMapViewCenterPointMoved ($latitude, $longitude)")
            }

//            toast("MapView onMapViewCenterPointMoved ($latitude, $longitude)")
        }

        callback?.invoke(CENTER_POINT_MOVED, mapPoint)
    }

    override fun onMapViewDoubleTapped(map: MapView?, mapPoint: MapPoint?) {
        mapPoint?.mapPointGeoCoord?.apply {
            val message = "Double-tab on ($latitude, $longitude)"

            if (mLog.isDebugEnabled) {
                mLog.debug(message)
            }

        }

        callback?.invoke(DOUBLE_TAPPED, mapPoint)
    }

    override fun onMapViewLongPressed(map: MapView?, mapPoint: MapPoint?) {
        mapPoint?.mapPointGeoCoord?.apply {
            val message = "Long Pressed on ($latitude, $longitude)"

            if (mLog.isDebugEnabled) {
                mLog.debug(message)
            }
        }

        callback?.invoke(LONG_PRESSED, mapPoint)
    }

    override fun onMapViewSingleTapped(map: MapView?, mapPoint: MapPoint?) {
        mapPoint?.mapPointGeoCoord?.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("MapView onMapViewSingleTapped ($latitude, $longitude)")
            }

//            toast("MapView onMapViewSingleTapped ($latitude, $longitude)")
        }

        callback?.invoke(SINGLE_TAPPED, mapPoint)
    }

    override fun onMapViewDragStarted(map: MapView?, mapPoint: MapPoint?) {
        mapPoint?.mapPointGeoCoord?.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("MapView onMapViewDragStarted ($latitude, $longitude)")
            }

//            toast("MapView onMapViewDragStarted ($latitude, $longitude)")
        }

        callback?.invoke(DRAG_STARTED, mapPoint)
    }

    override fun onMapViewDragEnded(map: MapView?, mapPoint: MapPoint?) {
        mapPoint?.mapPointGeoCoord?.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("MapView onMapViewDragEnded ($latitude, $longitude)")
            }

//            toast("MapView onMapViewDragEnded ($latitude, $longitude)")
        }

        callback?.invoke(DRAG_ENDED, mapPoint)
    }

    override fun onMapViewMoveFinished(map: MapView?, mapPoint: MapPoint?) {
        mapPoint?.mapPointGeoCoord?.apply {
            if (mLog.isDebugEnabled) {
                mLog.debug("MapView onMapViewMoveFinished ($latitude, $longitude)")
            }

//            toast("MapView onMapViewMoveFinished ($latitude, $longitude)")
        }

        callback?.invoke(MOVE_FINISHED, mapPoint)
    }

    override fun onMapViewZoomLevelChanged(map: MapView?, zoomLevel: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("MapView onMapViewZoomLevelChanged ($zoomLevel)")
        }

//        toast("MapView onMapViewZoomLevelChanged ($zoomLevel)")

        callback?.invoke(ZOOM_LEVEL_CHANGED, zoomLevel)
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(DaumMapView::class.java)

        const val INIT                  = 0
        const val CENTER_POINT_MOVED    = 1
        const val DOUBLE_TAPPED         = 2
        const val LONG_PRESSED          = 3
        const val SINGLE_TAPPED         = 4
        const val DRAG_STARTED          = 5
        const val DRAG_ENDED            = 6
        const val MOVE_FINISHED         = 7
        const val ZOOM_LEVEL_CHANGED    = 8
        const val AUTH_RESULT           = 9
    }
}