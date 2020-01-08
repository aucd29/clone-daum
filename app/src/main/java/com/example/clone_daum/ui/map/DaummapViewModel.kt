package com.example.clone_daum.ui.map

import android.app.Application
import androidx.lifecycle.MutableLiveData
import brigitte.viewmodel.CommandEventViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.zipWith
import net.daum.mf.map.api.MapPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-30 <p/>
 */

class DaummapViewModel @Inject constructor(
    app: Application
) : CommandEventViewModel(app) {

    val polyLine = MutableLiveData<MapPoint>()
    val dp = CompositeDisposable()
    val dummyList = arrayOf(
        MapPoint.mapPointWithWCONGCoord(475334.0, 1101210.0),
        MapPoint.mapPointWithWCONGCoord(474300.0, 1104123.0),
        MapPoint.mapPointWithWCONGCoord(474300.0, 1104123.0),
        MapPoint.mapPointWithWCONGCoord(473873.0, 1105377.0),
        MapPoint.mapPointWithWCONGCoord(473302.0, 1107097.0),
        MapPoint.mapPointWithWCONGCoord(473126.0, 1109606.0),
        MapPoint.mapPointWithWCONGCoord(473063.0, 1110548.0),
        MapPoint.mapPointWithWCONGCoord(473435.0, 1111020.0),
        MapPoint.mapPointWithWCONGCoord(474068.0, 1111714.0),
        MapPoint.mapPointWithWCONGCoord(475475.0, 1112765.0),
        MapPoint.mapPointWithWCONGCoord(476938.0, 1113532.0),
        MapPoint.mapPointWithWCONGCoord(478725.0, 1114391.0),
        MapPoint.mapPointWithWCONGCoord(479453.0, 1114785.0),
        MapPoint.mapPointWithWCONGCoord(480145.0, 1115145.0),
        MapPoint.mapPointWithWCONGCoord(481280.0, 1115237.0),
        MapPoint.mapPointWithWCONGCoord(481777.0, 1115164.0),
        MapPoint.mapPointWithWCONGCoord(482322.0, 1115923.0),
        MapPoint.mapPointWithWCONGCoord(482832.0, 1116322.0),
        MapPoint.mapPointWithWCONGCoord(483384.0, 1116754.0),
        MapPoint.mapPointWithWCONGCoord(484401.0, 1117547.0),
        MapPoint.mapPointWithWCONGCoord(484893.0, 1117930.0),
        MapPoint.mapPointWithWCONGCoord(485016.0, 1118034.0))

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            ITN_POLYLINE -> {
                dp.add(Observable.fromArray(dummyList).zipWith(
                    Observable.interval(1000, TimeUnit.MILLISECONDS))
                        { item, interval -> item[interval.toInt()] }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { polyLine.value = it }
                )
            }
            else ->  super.command(cmd, data)
        }
    }

    companion object {
        const val CMD_TILE_MODE_STANDARD = "mode-standard"
        const val CMD_TILE_MODE_HD2X     = "mode-hd2x"
        const val CMD_TILE_MODE_HD       = "mode-hd"

        const val CMD_MAP_MOVETO   = "map-move"
        const val CMD_ZOOM_LEVEL_7 = "zoom-level-7"
        const val CMD_CMD_MAP_MOVETO_AND_ZOOM_LEVEL_7 = "map-move-and-zoom-level-7"

        const val CMD_ROTATE_60 = "rotate-60"
        const val CMD_ROTATE_0  = "rotate-0"

        const val ITN_POLYLINE = "polyline"
    }
}