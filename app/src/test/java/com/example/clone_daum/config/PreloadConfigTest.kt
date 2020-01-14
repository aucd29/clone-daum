package com.example.clone_daum.config

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-31 <p/>
 */

import androidx.room.Room
import brigitte.jsonParse
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.*
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.Sitemap
import brigitte.shield.BaseRoboTest
import brigitte.shield.assertEquals
import brigitte.shield.mockReactiveX
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-31 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class PreloadConfigTest: BaseRoboTest() {
    lateinit var config: PreloadConfig

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()
        initDb()

        config = PreloadConfig(daumService, dbRepository, disposable, app.assets, app)
    }

    @Test
    fun submenuTest() {
        if (logger.isDebugEnabled) {
            logger.debug("SUBMENU ${config.brsSubMenuList.size}")
        }

        val submenu = app.assets.open("res/brs_submenu.json").readBytes()
            .jsonParse<List<BrowserSubMenu>>()

        config.brsSubMenuList.size.assertEquals(submenu.size)

        var i = 0
        config.brsSubMenuList.forEach {
            it.icon.assertEquals(submenu[i].icon)
            it.name.assertEquals(submenu[i].name)

            ++i
        }
    }

    @Test
    fun sitemapTest() {
        if (logger.isDebugEnabled) {
            logger.debug("SITEMAP ${config.naviSitemapList.size}")
        }

        val sitemap = app.assets.open("res/navi_sitemap.json").readBytes()
            .jsonParse<List<Sitemap>>()

        config.naviSitemapList.size.assertEquals(sitemap.size)

        var i = 0
        config.naviSitemapList.forEach {
            it.icon.assertEquals(sitemap[i].icon)
            it.name.assertEquals(sitemap[i].name)

            ++i
        }
    }

    @Test
    fun frequentlyTest() {
        if (logger.isDebugEnabled) {
            logger.debug("FREQUENTLY ${db.frequentlySiteDao().count()}")
        }

        val frequently = app.assets.open("res/frequently_site.json").readBytes()
            .jsonParse<List<FrequentlySite>>()

        val select = db.frequentlySiteDao().select()
        select.subscribe {
            it.size.assertEquals(frequently.size)
        }

        select.subscribe {
            var i = 0

            it.forEach {
                it.title.assertEquals(frequently[i].title)
                it.url.assertEquals(frequently[i].url)

                ++i
            }
        }
    }

    @Test
    fun tabTest() {
        if (logger.isDebugEnabled) {
            logger.debug("TAB ${config.tabLabelList.size}")
        }

        val tab = app.assets.open("res/tab.json").readBytes().jsonParse<List<TabData>>()

        config.tabLabelList.size.assertEquals(tab.size)

        var i = 0
        config.tabLabelList.forEach {
            it.name.assertEquals(tab[i].name)

            ++i
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // DB
    //
    ////////////////////////////////////////////////////////////////////////////////////

    lateinit var db: LocalDb
    lateinit var dbRepository: DbRepository

    // https://stackoverflow.com/questions/54327392/roboelectric-with-room-database-for-android
    private fun initDb() {
        db = Room.inMemoryDatabaseBuilder(app, LocalDb::class.java).allowMainThreadQueries().build()
        dbRepository = DbRepository(db.frequentlySiteDao())
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val logger = LoggerFactory.getLogger(PreloadConfigTest::class.java)
    }

    @Mock lateinit var daumService: DaumService
    @Mock lateinit var disposable: CompositeDisposable

    override fun initMock() {
        super.initMock()
        mockReactiveX()
    }
}