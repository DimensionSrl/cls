package it.dimension.cls.todos

import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.dimension.todo.data.ToDo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class MainFragment : Fragment() {

    private val mainViewModel by activityViewModels<MainActivity.MainViewModel>()

    private val adapter = TodosAdapter().apply {
        onToDoSelectListener = object : OnToDoSelectListener {
            override fun onToDoSelected(toDo: ToDo) {
                mainViewModel.toggle(toDo)
            }
        }
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.uiState.observe(this, Observer { state ->
            adapter.state = state
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            adapter = this@MainFragment.adapter
        }
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.title)

        lateinit var todo: ToDo

        fun bind(todo: ToDo) {
            this.todo = todo
            this.title.text = when (todo.done) {
                true -> SpannableStringBuilder(todo.title).apply {
                    setSpan(StrikethroughSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                else -> todo.title
            }
        }
    }

    class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        fun bind(error: Exception) {
            subtitle.text = error.localizedMessage
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnToDoSelectListener {
        fun onToDoSelected(toDo: ToDo)
    }

    class TodosAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var onToDoSelectListener: OnToDoSelectListener? = null

        var state: MainActivity.UiState = MainActivity.UiState.Loading
        set(value) {
            field = value
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TODO -> TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_todo, parent, false)).apply {
                    itemView.setOnClickListener {
                        onToDoSelectListener?.onToDoSelected(todo)
                    }
                }
                ERROR -> ErrorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_error, parent, false))
                LOADING -> LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_loading, parent, false))
                else -> throw IllegalArgumentException("Unknown view type $viewType")
            }
        }

        override fun getItemCount(): Int = when(val s = state) {
            is MainActivity.UiState.Error,
            is MainActivity.UiState.Loading -> 1
            is MainActivity.UiState.Ok -> s.todos.size
        }

        override fun getItemViewType(position: Int): Int = when(state) {
            is MainActivity.UiState.Error -> ERROR
            is MainActivity.UiState.Loading -> LOADING
            is MainActivity.UiState.Ok -> TODO
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder.itemViewType) {
                TODO -> (holder as TodoViewHolder).bind((state as MainActivity.UiState.Ok).todos[position])
                ERROR -> (holder as ErrorViewHolder).bind((state as MainActivity.UiState.Error).error)
            }
        }

        companion object {
            const val LOADING = 0
            const val TODO = 1
            const val ERROR = 2
        }
    }
}