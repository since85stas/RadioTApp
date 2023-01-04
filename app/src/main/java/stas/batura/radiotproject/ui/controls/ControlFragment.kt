package stas.batura.musicproject.ui.control

import android.graphics.ColorFilter
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.control_fragment_new.*
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.ControlFragmentNewBinding


class ControlFragment () : Fragment() {

    private val TAG = "controlfragment"

//    private var isPlayButtonClicked = false

    companion object {
        fun newInstance() = ControlFragment()
    }

    private lateinit var mainViewModel: MainActivityViewModel

    /**
     * создаем фрагмент
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

//        val view = inflater.inflate(R.layout.control_fragment_new, container, false)
        val bindings: ControlFragmentNewBinding = DataBindingUtil.inflate(inflater,
            R.layout.control_fragment_new,
            container,
            false)
        bindings.mainActViewModel = mainViewModel
        bindings.lifecycleOwner = viewLifecycleOwner

        return bindings.root
    }

    /**
     * после зоздания фрагмента смотрим за действиями
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {

        play_pause_button.setOnClickListener( object : View.OnClickListener {
            override fun onClick(view: View) {
//                isPlayButtonClicked = true
                mainViewModel.changePlayState()
            }
        })

        super.onActivityCreated(savedInstanceState)
    }

    private fun addObservers() {
        // наблюдаем за нажатием кнопок
        mainViewModel.callbackChanges.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                    play_pause_button.setImageResource(R.drawable.ic_pause_black_24dp)
                } else if (it.state == PlaybackStateCompat.STATE_PAUSED ) {
                    play_pause_button.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                } else if (it.state == PlaybackStateCompat.STATE_NONE) {
                    Log.d(TAG, "none")
//                    play_pause_button.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
                } else {
                    Log.d(TAG, "else state")
                }
            }
        })

    }

    private fun removeObservers() {
        mainViewModel.callbackChanges.removeObservers(viewLifecycleOwner)
    }

    override fun onStart() {
        addObservers()
        super.onStart()
    }

    override fun onPause() {
        removeObservers()
        super.onPause()
    }

    override fun onStop() {
//        isPlayButtonClicked = false

        super.onStop()
    }

}
