package com.example.clone_daum.config

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-31 <p/>
 */

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import brigitte.jsonParse
import com.example.clone_daum.MainApp
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.DbRepository
import com.example.clone_daum.model.local.*
import com.example.clone_daum.model.remote.DaumService
import com.example.clone_daum.model.remote.Sitemap
import com.example.clone_daum.util.mockReactiveX
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.slf4j.LoggerFactory


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-31 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class PreloadConfigTest {
    lateinit var config: PreloadConfig

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()
        initDb()

        config = PreloadConfig(daumService, dbRepository, disposable, app.assets, app)
    }

    @Test
    fun testSubmenu() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SUBMENU ${config.brsSubMenuList.size}")
        }

        val submenu = app.assets.open("res/brs_submenu.json").readBytes()
            .jsonParse<List<BrowserSubMenu>>()

        assertEquals(config.brsSubMenuList.size, submenu.size)

        var i = 0
        config.brsSubMenuList.forEach {
            assertEquals(it.icon, submenu[i].icon)
            assertEquals(it.name, submenu[i].name)

            ++i
        }
    }

    @Test
    fun testSitemap() {
        if (mLog.isDebugEnabled) {
            mLog.debug("SITEMAP ${config.naviSitemapList.size}")
        }

        val sitemap = app.assets.open("res/navi_sitemap.json").readBytes()
            .jsonParse<List<Sitemap>>()

        assertEquals(config.naviSitemapList.size, sitemap.size)

        var i = 0
        config.naviSitemapList.forEach {
            assertEquals(it.icon, sitemap[i].icon)
            assertEquals(it.name, sitemap[i].name)

            ++i
        }
    }

    @Test
    fun testFrequently() {
        if (mLog.isDebugEnabled) {
            mLog.debug("FREQUENTLY ${db.frequentlySiteDao().count()}")
        }

        val frequently = app.assets.open("res/frequently_site.json").readBytes()
            .jsonParse<List<FrequentlySite>>()

        val select = db.frequentlySiteDao().select()
        select.subscribe {
            assertEquals(it.size, frequently.size)
        }

        select.subscribe {
            var i = 0

            it.forEach {
                assertEquals(it.title, frequently[i].title)
                assertEquals(it.url, frequently[i].url)

                ++i
            }
        }
    }

    @Test
    fun testTab() {
        if (mLog.isDebugEnabled) {
            mLog.debug("TAB ${config.tabLabelList.size}")
        }

        val tab = app.assets.open("res/tab.json").readBytes().jsonParse<List<TabData>>()

        assertEquals(config.tabLabelList.size, tab.size)

        var i = 0
        config.tabLabelList.forEach {
            assertEquals(it.name, tab[i].name)

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
        private val mLog = LoggerFactory.getLogger(PreloadConfigTest::class.java)
    }

//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock lateinit var daumService: DaumService
    @Mock lateinit var disposable: CompositeDisposable

    private fun initMock() {
        MockitoAnnotations.initMocks(this)
        mockReactiveX()
    }

    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()

    // https://stackoverflow.com/questions/35031301/android-robolectric-unit-test-for-marshmallow-permissionhelper
    private val shadowApp = Shadows.shadowOf(app)
}