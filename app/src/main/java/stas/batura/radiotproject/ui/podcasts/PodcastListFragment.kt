package stas.batura.radioproject.ui.podcastlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_podcast_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.FragmentPodcastListBinding
import stas.batura.radiotproject.ui.podcasts.PodcastListViewModel
import stas.batura.radiotproject.ui.podcasts.PodcastsAdapter
import stas.batura.room.podcast.Podcast

class PodcastListFragment : Fragment() {

    private val TAG = PodcastListFragment::class.java.simpleName

    private lateinit var podcastListViewModel: PodcastListViewModel

    private lateinit var mainviewModel: MainActivityViewModel

    private lateinit var adapter: PodcastsAdapter

    private lateinit var bindings: FragmentPodcastListBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        podcastListViewModel =
                ViewModelProvider(requireActivity()).get(PodcastListViewModel::class.java)

        // TODO: проверить состояние модели после перезапуска активити
        mainviewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        bindings  = DataBindingUtil.inflate(inflater,
        R.layout.fragment_podcast_list,
        container,
        false)
        bindings.podacstListViewModel = podcastListViewModel
        bindings.mainViewModel = mainviewModel

        bindings.lifecycleOwner = requireActivity()

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // адаптер для списка
        adapter = PodcastsAdapter(mainActivityViewModel = mainviewModel, listModel = podcastListViewModel)

//        val concatAdapter = ConcatAdapter(headerAdapter, adapter)
        bindings.podcastRecycler.adapter = adapter

        podcastListViewModel.newPodcastList.observe(viewLifecycleOwner) {podcasts ->
            if (podcasts != null) {
                adapter.submitList(podcasts)

                podcastListViewModel.activeNumPref.value?.let {
                    scrollToPosition(podcasts, it)
                }
            } else {
                Log.d(TAG, "onViewCreated: podcasts is null")
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        addObservers()
        super.onStart()
    }

    override fun onPause() {
        removeObservers()
        super.onPause()
    }

    private fun scrollToPosition(podcastList: List<Podcast>, podcastId: Int) {
        var find = 0
        var count = 0
        for (p in podcastList) {
            if (p.podcastId == podcastId) {
                find = count
                break
            }
            count ++
        }

        bindings.podcastRecycler.smoothScrollToPosition(find)
    }

    /**
     * starting observing a viewModel when fragment is active
     */
    @ExperimentalCoroutinesApi
    private fun addObservers() {

        podcastListViewModel.activeNumPref.observe(viewLifecycleOwner) {
            Log.d(TAG, "activeNumberPref: $it")
            mainviewModel.updateActivePodcast(it)
        }

    }

    /**
     * stoping observing a viewModel
     */
    private fun removeObservers() {

    }
}