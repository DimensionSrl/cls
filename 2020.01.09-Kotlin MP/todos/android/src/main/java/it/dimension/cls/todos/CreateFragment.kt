package it.dimension.cls.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class CreateFragment : Fragment() {

    val mainViewModel by activityViewModels<MainActivity.MainViewModel>()
    lateinit var name: EditText
    lateinit var button: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        name = view.findViewById(R.id.name)
        button = view.findViewById<Button>(R.id.button).apply {
            setOnClickListener {
                val title = name.text.toString()
                mainViewModel.create(when {
                    title.isBlank() -> "Comprare il latte"
                    else -> title
                })
                parentFragmentManager.popBackStack()
            }
        }
    }
}