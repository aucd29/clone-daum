package com.example.clone_daum.di.module

import android.app.Application
import androidx.room.Room
import com.example.clone_daum.model.local.LocalDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module
class DbModule {
    companion object {
        val DB_NAME = "local.db"
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): LocalDb =
        Room.databaseBuilder(app, LocalDb::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideSearchHistoryDao(db: LocalDb)=
        db.searchHistoryDao()

//    @Singleton
//    @Provides
//    fun providePopularKeywordDao(db: LocalDb)=
//        db.popularKeywordDao()

    @Singleton
    @Provides
    fun provideUrlHistoryDao(db: LocalDb) =
        db.urlHistoryDao()

    @Singleton
    @Provides
    fun provideMyFavoriteDao(db: LocalDb) =
        db.myFavoriteDao()

    @Singleton
    @Provides
    fun provideFrequentlySiteDao(db: LocalDb) =
        db.frequentlySiteDao()
}