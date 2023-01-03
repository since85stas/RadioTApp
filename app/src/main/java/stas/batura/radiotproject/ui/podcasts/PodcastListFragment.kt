package stas.batura.radioproject.ui.podcastlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ConcatAdapter
import kotlinx.android.synthetic.main.fragment_podcast_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import stas.batura.radiotproject.MainActivity
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.FragmentPodcastListBinding
import stas.batura.radiotproject.ui.podcasts.FooterAdapter
import stas.batura.radiotproject.ui.podcasts.PodcastListViewModel
import stas.batura.radiotproject.ui.podcasts.PodcastsAdapter
import stas.batura.room.podcast.Podcast
import timber.log.Timber

class PodcastListFragment : Fragment() {

    private val TAG = PodcastListFragment::class.java.simpleName

    private lateinit var podcastListViewModel: PodcastListViewModel

    private lateinit var mainviewModel: MainActivityViewModel

    private lateinit var adapter: PodcastsAdapter

    private lateinit var concatAdapter: ConcatAdapter

    private lateinit var bindings: FragmentPodcastListBinding

    private var shouldScroll = true

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

        val footerAdapter = FooterAdapter(podcastListViewModel ::addMorePodcasts)

        concatAdapter = ConcatAdapter(adapter)

        bindings.podcastRecycler.adapter = concatAdapter

        podcastListViewModel.newPodcastList.observe(viewLifecycleOwner) {podcasts ->
            if (podcasts != null) {
                adapter.submitList(podcasts)

                // добавляем футер
                concatAdapter.addAdapter(footerAdapter)

                Timber.d("list changes: observe")

                // в начале скроллим до активного подкаста
                podcastListViewModel.activeNumPref.value?.let {
                    if (shouldScroll) {
                        shouldScroll = false
                        scrollToPosition(podcasts, it)
                    }
                }
            } else {
                Log.d(TAG, "onViewCreated: podcasts is null")
            }
        }

        podcastListViewModel.spinner.observe(viewLifecycleOwner) {
            val toolbar = (requireActivity() as MainActivity).toolbar
            val toolspinner = toolbar.findViewById<ProgressBar>(R.id.toolbarProgress)
            if (it) {
                toolspinner.visibility = View.VISIBLE
            } else {
                toolspinner.visibility = View.GONE
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

        CoroutineScope(Dispatchers.Default).launch {
            var find = 0
            var count = 0
            for (p in podcastList) {
                if (p.podcastId == podcastId) {
                    find = count
                    break
                }
                count++
            }

            CoroutineScope(Dispatchers.Main).launch {
                bindings.podcastRecycler.scrollToPosition(find)
            }
        }
    }

    /**
     * starting observing a viewModel when fragment is active
     */
    @ExperimentalCoroutinesApi
    private fun addObservers() {

//        podcastListViewModel.activeNumPref.observe(viewLifecycleOwner) {
//            Log.d(TAG, "activeNumberPref: $it")
//            mainviewModel.updateActivePodcast(it)
//        }

    }

    /**
     * stoping observing a viewModel
     */
    private fun removeObservers() {

    }
}