package com.example.clone_daum.ui.map

import android.graphics.Color
import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.common.widget.DaumMapView
import com.example.clone_daum.databinding.DaummapFragmentBinding
import dagger.android.ContributesAndroidInjector
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-30 <p/>
 */

class DaummapFragment @Inject constructor(
): BaseDaggerFragment<DaummapFragmentBinding, DaummapViewModel>() {
    override val layoutId = R.layout.daummap_fragment

    override fun initViewBinding() {
        binding.daummapLayout.apply {
            initMapLayout(requireActivity())
            callback = { code, data ->
                if (logger.isDebugEnabled) {
                    logger.debug("CODE : $code, ")
                }

                when (code) {
                    DaumMapView.INIT                -> {}
                    DaumMapView.CENTER_POINT_MOVED  -> {}
                    DaumMapView.DOUBLE_TAPPED       -> {}
                    DaumMapView.LONG_PRESSED        -> {}
                    DaumMapView.SINGLE_TAPPED       -> {}
                    DaumMapView.DRAG_STARTED        -> {}
                    DaumMapView.DRAG_ENDED          -> {}
                    DaumMapView.MOVE_FINISHED       -> {}
                    DaumMapView.ZOOM_LEVEL_CHANGED  -> {}
                    DaumMapView.AUTH_RESULT         -> {}
                }
            }
        }
    }

    override fun initViewModelEvents() {
        observe(viewModel.polyLine) {
            binding.daummapLayout.mapView?.addPolyline(MapPolyline().apply {
                lineColor = Color.argb(128, 255, 51, 0)
                addPoint(it)
            })
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        DaummapViewModel.apply {
            binding.daummapLayout.mapView?.let {
                when (cmd) {
                    CMD_TILE_MODE_STANDARD ->
                        it.mapTileMode = MapView.MapTileMode.Standard

                    CMD_TILE_MODE_HD ->
                        it.mapTileMode = MapView.MapTileMode.HD

                    CMD_TILE_MODE_HD2X ->
                        it.mapTileMode = MapView.MapTileMode.HD2X

                    CMD_MAP_MOVETO ->
                        it.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true)

                    CMD_ZOOM_LEVEL_7 ->
                        it.setZoomLevel(7, true)

                    CMD_CMD_MAP_MOVETO_AND_ZOOM_LEVEL_7 ->
                        it.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true)

                    CMD_ROTATE_0 ->
                        it.setMapRotationAngle(0.0f, true)

                    CMD_ROTATE_60 ->
                        it.setMapRotationAngle(60.0f, true)
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [DaummapFragmentModule::class])
        abstract fun contributeMapFragmentInjector(): DaummapFragment
    }

    @dagger.Module
    abstract class DaummapFragmentModule {
//        @Binds
//        abstract fun bindMapFragment(fragment: DaummapFragment): Fragment
//
//        @dagger.Module
//        companion object {
//        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DaummapFragment::class.java)
    }
}