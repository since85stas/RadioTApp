package stas.batura.musicproject.ui.control

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.FragmentControlBinding


class ControlFragment () : Fragment() {

    private val TAG = "controlfragment"

//    private var isPlayButtonClicked = false

    companion object {
        fun newInstance() = ControlFragment()
    }

    private lateinit var mainViewModel: MainActivityViewModel

    private lateinit var bindings: FragmentControlBinding

    /**
     * создаем фрагмент
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        val controlViewModel = ViewModelProvider(this).get(ControlViewModel::class.java)

//        val view = inflater.inflate(R.layout.control_fragment_new, container, false)
        bindings = DataBindingUtil.inflate(inflater,
            R.layout.fragment_control,
            container,
            false)

        bindings.mainActViewModel = mainViewModel
        bindings.contolViewModel = controlViewModel
        bindings.lifecycleOwner = viewLifecycleOwner

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings.playPauseButton.setOnClickListener( object : View.OnClickListener {
            override fun onClick(view: View) {
//                isPlayButtonClicked = true
                mainViewModel.changePlayState()
            }
        })
    }

    private fun addObservers() {
        // наблюдаем за нажатием кнопок
        mainViewModel.callbackChanges.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                    bindings.playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)
                } else if (it.state == PlaybackStateCompat.STATE_PAUSED ) {
                    bindings.playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
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
