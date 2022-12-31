package stas.batura.radiotproject

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import stas.batura.data.ListViewType
import stas.batura.data.Year
import stas.batura.download.DownloadResult
import stas.batura.download.DownloadService
import stas.batura.download.DownloadServiceResult
import stas.batura.radiotproject.player.MusicService
import stas.batura.room.podcast.Podcast

private lateinit var appBarConfiguration: AppBarConfiguration

class MainActivity : AppCompatActivity(), RecieverResult {

    private val TAG = MainActivity::class.java.simpleName

    private val messageReceiver = MessageReceiver(this)

    lateinit var mainActivityViewModel: MainActivityViewModel

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {


//        val music = MusicService()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_podcastlist
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // загружаем хидер
        loadNavHeader()

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // слушаем когда запускать сервис
        mainActivityViewModel.createServiceListner.observe(this) { it ->
            if (it) {
                mainActivityViewModel.initMusicService()
            }
        }

        // слушаем когда сервис успешно коннектится
        mainActivityViewModel.serviceConnection.observe(this) { it ->
            if (it != null) {
                Log.d(TAG, "onCreate: " + it.toString())
                bindCurrentService(it)
            }
        }

        // слушаем текущее состояние плеера и меняем UI
        mainActivityViewModel.callbackChanges.observe(this, Observer {
            if (it != null) {
                if (it.state == PlaybackStateCompat.STATE_PLAYING) {
//                    Log.d(TAG, "onCreate: play spinner visible")
                    mainActivityViewModel.playAnimVisible()
                    mainActivityViewModel.redrawItemById()
                } else if (it.state == PlaybackStateCompat.STATE_PAUSED) {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel.playAnimNotVisible()
                    mainActivityViewModel.redrawItemById()
                } else if (it.state == PlaybackStateCompat.STATE_NONE) {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel.playAnimNotVisible()
                    mainActivityViewModel.redrawItemById()
                } else {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel.playAnimNotVisible()
                    mainActivityViewModel.redrawItemById()
                }
            }
        })

        mainActivityViewModel.downloadPodcastEvent.observe(this, Observer {
            it?.let { podcast ->
                downloadPodcast(podcast)
            }
        })

        // нициализируем сервис
        mainActivityViewModel.initMusicService()

        // описываем nav drawer
        createSectionsInMenu()


    }


    private fun testDownload() {
//        )
    }

    private fun downloadPodcast(podcast: Podcast) {

        val intent = Intent(this, DownloadService::class.java)

        val link = podcast.audioUrl
        intent.putExtra(DownloadService.LINK_KEY, link)
        intent.putExtra(DownloadService.PODCAST_ID, podcast.podcastId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        // начинаем слушать ответ от сервиса по загрузке
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageReceiver, IntentFilter(DownloadService.DOWNLOAD_RESULT))
    }


    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private fun loadNavHeader() { // name, website
        val navView = nav_view.getHeaderView(0)
        navView.name.text = ("Stanislav Batura")
        navView.website.text = ("stanislav.batura85@gmail.com")
        navView.img_header_bg.setImageResource(R.drawable.drawer_back)
        Glide.with(this).load(R.drawable.cat_my).transform(CircleTransform(this))
            .into(navView.img_profile)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)

        //        addTour()
        return true
    }

    /**
     * прописывает базовое нажатие на открытие NavView
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * привязываем сервис к активити
     */
    private fun bindCurrentService(serviceConnection: ServiceConnection) {
        // привязываем сервис к активити
        bindService(
            Intent(applicationContext!!, MusicService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun startDownloadService() {
    }

    /**
     * создает отображение списка секций в меню
     */
    private fun createSectionsInMenu() {
//        // устанавливаем слушатель на нажатие клавиш
        nav_view.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.navigation_podcastlist)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_fav -> {
                    mainActivityViewModel.setPrefsListType(ListViewType.FAVORITE)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_saved -> {
                    navController.navigate(R.id.navigation_savedpodcastlist)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_year_2022 -> {
                    mainActivityViewModel.getPodcasttsInYear(Year.Y2022)
                    mainActivityViewModel.setPrefsListType(ListViewType.YEAR)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_year_2021 -> {
                    mainActivityViewModel.getPodcasttsInYear(Year.Y2021)
                    mainActivityViewModel.setPrefsListType(ListViewType.YEAR)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_year_2020 -> {
                    mainActivityViewModel.getPodcasttsInYear(Year.Y2020)
                    mainActivityViewModel.setPrefsListType(ListViewType.YEAR)
                    drawer_layout.closeDrawers()
                    true
                }
                else -> false
            }
        }))

    }

    override fun donloadPodcastRsult(downloadServiceResult: DownloadServiceResult) {
        when (downloadServiceResult.status) {
            is DownloadResult.OK -> {
                Toast.makeText(this, "Загрузка прошла успешно", Toast.LENGTH_LONG).show()
                mainActivityViewModel.endDownloadPodcast(downloadServiceResult.entityId, downloadServiceResult.cachedPath)
            }
            is DownloadResult.Error -> {
                Toast.makeText(this, "Ошибка загрузки. Попробуйте еще раз", Toast.LENGTH_LONG).show()
            }
        }
    }
}