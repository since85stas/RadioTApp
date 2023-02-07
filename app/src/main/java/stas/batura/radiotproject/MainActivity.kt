package stas.batura.radiotproject

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Menu
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import stas.batura.data.ListViewType
import stas.batura.di.ServiceLocator
import stas.batura.download.DownloadResult
import stas.batura.download.DownloadService
import stas.batura.download.DownloadServiceResult
import stas.batura.radiotproject.databinding.ActivityMainBinding
import stas.batura.radiotproject.player.MusicService
import stas.batura.data.Podcast

private lateinit var appBarConfiguration: AppBarConfiguration

class MainActivity : AppCompatActivity(), RecieverResult {

    private val messageReceiver = MessageReceiver(this)

    lateinit var mainActivityViewModel: MainActivityViewModel

    lateinit var navController: NavController

    lateinit var binding: ActivityMainBinding

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
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


//        // слушаем когда сервис успешно коннектится
//        mainActivityViewModel.serviceConnection.observe(this) { it ->
//            if (it != null) {
//                Log.d(TAG, "onCreate: " + it.toString())
//                bindCurrentService(it)
//            }
//        }

        // слушаем текущее состояние плеера и меняем UI
        mainActivityViewModel.callbackChanges.observe(this, Observer {
            if (it != null) {
                if (it.state == PlaybackStateCompat.STATE_PLAYING) {
//                    Log.d(TAG, "onCreate: play spinner visible")
                    mainActivityViewModel.playAnimVisible()
//                    mainActivityViewModel.redrawItemById()
                } else if (it.state == PlaybackStateCompat.STATE_PAUSED) {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel.playAnimNotVisible()
//                    mainActivityViewModel.redrawItemById()
                } else if (it.state == PlaybackStateCompat.STATE_NONE) {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel.playAnimNotVisible()
//                    mainActivityViewModel.redrawItemById()
                } else {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel.playAnimNotVisible()
//                    mainActivityViewModel.redrawItemById()
                }
            }
        })

        mainActivityViewModel.downloadPodcastEvent.observe(this, Observer {
            it?.let { podcast ->
                downloadPodcast(podcast)
            }
        })

        mainActivityViewModel.playClicked.observe(this, { clicked ->
            clicked?.let {
                if (clicked) {
                    startAndBindMusicService()
                }
            }
        })


//        // привязываем сервис к активити
//        startAndBindMusicService(RadioApp.ServiceHelper.getServiceConnection())

        // описываем nav drawer
        createSectionsInMenu()

        configureStatusBar()

        ServiceLocator.provideAnalitic().appOpenEvent()
    }


    private fun configureStatusBar() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.contrBackColor)
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
        val navView = binding.navView.getHeaderView(0)
        navView.name.text = ("Stanislav Batura")
        navView.website.text = ("Open Source: github.com/since85stas/RadioTApp")
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
    private fun startAndBindMusicService() {
        val serviceConnection = RadioApp.ServiceHelper.getServiceConnection()
        // запускаем сервис для проигрывания
        val musicServiceIntent  = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(musicServiceIntent)
        } else {
            startService(musicServiceIntent)
        }

        // привязываем сервис к активити
        bindService(
            Intent(this, MusicService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
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
                    mainActivityViewModel.setPrefsListType(ListViewType.NORMAL)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_fav -> {
                    navController.navigate(R.id.navigation_podcastlist)
                    mainActivityViewModel.setPrefsListType(ListViewType.FAVORITE)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_saved -> {
                    navController.navigate(R.id.navigation_savedpodcastlist)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_online -> {
                    navController.navigate(R.id.navigation_online)
                    drawer_layout.closeDrawers()
                    true
                }
                R.id.nav_news -> {
                    navController.navigate(R.id.navigation_news)
                    drawer_layout.closeDrawers()
                    ServiceLocator.provideAnalitic().newsEvent()
                    true
                }
//                R.id.nav_year_2022 -> {
//                    mainActivityViewModel.getPodcasttsInYear(Year.Y2022)
//                    mainActivityViewModel.setPrefsListType(ListViewType.YEAR)
//                    drawer_layout.closeDrawers()
//                    true
//                }
//                R.id.nav_year_2021 -> {
//                    mainActivityViewModel.getPodcasttsInYear(Year.Y2021)
//                    mainActivityViewModel.setPrefsListType(ListViewType.YEAR)
//                    drawer_layout.closeDrawers()
//                    true
//                }
//                R.id.nav_year_2020 -> {
//                    mainActivityViewModel.getPodcasttsInYear(Year.Y2020)
//                    mainActivityViewModel.setPrefsListType(ListViewType.YEAR)
//                    drawer_layout.closeDrawers()
//                    true
//                }
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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
    }
}