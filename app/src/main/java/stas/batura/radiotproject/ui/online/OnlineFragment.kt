package stas.batura.radiotproject.ui.online

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.FragmentOnlineBinding
import timber.log.Timber


class OnlineFragment : Fragment() {

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
        bindings = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_online,
            container,
            false
        )

//        bindings.
//        bindings.ma
        bindings.mainActViewModel = mainViewModel
        bindings.onlineViewModel = onlineViewModel
        bindings.lifecycleOwner = viewLifecycleOwner

        setupTelegramChatTextview()

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onlineViewModel.timerValues.observe(viewLifecycleOwner, { time ->
            Log.d("test", "onViewCreated: $time")
        })
    }

    private fun setupTelegramChatTextview() {

        val spannableString = SpannableString(bindings.chatTextField.text)
        spannableString.setSpan(
            UnderlineSpan(),
            0, bindings.chatTextField.text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        bindings.chatTextField.text = spannableString

        bindings.chatTextField.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=radio_t_chat"))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Timber.e(e.toString())
            }
        }
    }

}