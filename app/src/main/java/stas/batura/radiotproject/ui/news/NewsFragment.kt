package stas.batura.radiotproject.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.FragmentNewsBinding
import timber.log.Timber

class NewsFragment: Fragment() {

    private lateinit var newsViewModel: NewsViewModel

    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        newsViewModel = ViewModelProvider(this).get()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_news,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        newsViewModel.news.observe(viewLifecycleOwner, { news ->
            Timber.d("$news")
        })

    }
}