package stas.batura.radiotproject.ui.online

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import stas.batura.musicproject.ui.control.ControlViewModel
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.FragmentControlBinding
import stas.batura.radiotproject.databinding.FragmentOnlineBinding

class OnlineFragment: Fragment() {

    private lateinit var mainViewModel: MainActivityViewModel

    private lateinit var onlineViewModel: OnlineViewModel

    private lateinit var bindings: FragmentOnlineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        onlineViewModel = ViewModelProvider(this).get(OnlineViewModel::class.java)

//        val view = inflater.inflate(R.layout.control_fragment_new, container, false)
        bindings = DataBindingUtil.inflate(inflater,
            R.layout.fragment_online,
            container,
            false)

//        bindings.
//        bindings.ma
        bindings.mainActViewModel = mainViewModel
        bindings.onlineViewModel = onlineViewModel
        bindings.lifecycleOwner = viewLifecycleOwner

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onlineViewModel.timerValues.observe(viewLifecycleOwner, { time ->
            Log.d("test", "onViewCreated: $time")
        })
    }

}