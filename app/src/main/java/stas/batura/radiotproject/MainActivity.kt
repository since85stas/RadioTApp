package stas.batura.radiotproject

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
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
import stas.batura.data.Year
import stas.batura.download.DownloadService
import stas.batura.radiotproject.player.MusicService

private lateinit var appBarConfiguration: AppBarConfiguration

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val music = MusicService()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
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

//        val bindings: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        bindings.lifecycleOwner = this
//        bindings.mainViewModel = mainActivityViewModel

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

        // нициализируем сервис
        mainActivityViewModel.initMusicService()

        // описываем nav drawer
        createSectionsInMenu()


        // Start the download service if it should be running but it's not currently.
        // Starting the service in the foreground causes notification flicker if there is no scheduled
        // action. Starting it in the background throws an exception if the app is in the background too
        // (e.g. if device screen is locked).
//        try {
//            DownloadService.start(this, PodcastDownloadService::class.java)
//            Log.i(TAG, "onCreate: starting download serv")
//            testDownload()
//        } catch (e: IllegalStateException) {
//            DownloadService.startForeground(this, PodcastDownloadService::class.java)
//            Log.i(TAG, "onCreate: $e")
//        }
        startDownloadService()
    }


    private fun testDownload() {
////        val downloadRequest: DownloadRequest = DownloadRequest.Builder(
////            "test3",
////            Uri.parse("http://cdn.radio-t.com/rt_podcast669.mp3")
////        ).build()
//        val mediaItem = MediaItem.fromUri(Uri.parse("http://cdn.radio-t.com/rt_podcast669.mp3"))
//        val helper = DownloadHelper.forMediaItem(
//            applicationContext,
//            mediaItem)
//
//        val request = helper.getDownloadRequest(Util.getUtf8Bytes(mediaItem.mediaId))
//        DownloadService.sendAddDownload(
//            this,
//            PodcastDownloadService::class.java,
//            request,
//            /* foreground= */ false
//        )
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
        val intent = Intent(applicationContext!!, DownloadService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    /**
     * создает отображение списка секций в меню
     */
    private fun createSectionsInMenu() {
//        // устанавливаем слушатель на нажатие клавиш
        nav_view.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_fav -> {
                    mainActivityViewModel.setPrefsListType(ListViewType.FAVORITE)
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


}