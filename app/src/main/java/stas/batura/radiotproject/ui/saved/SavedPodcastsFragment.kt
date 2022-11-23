package stas.batura.radiotproject.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.FragmentSavedPodcastsBinding

class SavedPodcastsFragment: Fragment() {

    lateinit var savedPodcastViewModel: SavedPodcastViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        savedPodcastViewModel = ViewModelProvider(this).get(SavedPodcastViewModel::class.java)

        val bindings: FragmentSavedPodcastsBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_saved_podcasts,
            container,
            false)

        bindings.viewmodel = savedPodcastViewModel

        bindings.lifecycleOwner = viewLifecycleOwner

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}