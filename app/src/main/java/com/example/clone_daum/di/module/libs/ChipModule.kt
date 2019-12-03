package com.example.clone_daum.di.module.libs

import android.content.Context
import android.view.Gravity
import androidx.databinding.ObservableField
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import dagger.Module
import dagger.Provides

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 11. <p/>
 */

@Module
class ChipModule {
    @Provides
    fun provideChipsLayoutManager(context: Context): ChipsLayoutManager {
        return ChipsLayoutManager.newBuilder(context)
            .setChildGravity(Gravity.TOP)
            .setScrollingEnabled(false)
            .setMaxViewsInRow(3)
            .setGravityResolver {
                Gravity.CENTER
            }
//            .setRowBreaker { position ->
//                position == 6 || position == 11 || position == 2
//            }
            .setOrientation(ChipsLayoutManager.HORIZONTAL)
            .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
            .withLastRow(true)
            .build()
    }
}