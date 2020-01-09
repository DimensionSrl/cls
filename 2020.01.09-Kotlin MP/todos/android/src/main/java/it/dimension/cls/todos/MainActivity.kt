package it.dimension.cls.todos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import it.dimension.cls.todos.api.Result
import it.dimension.todo.data.ToDo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

    private val mainModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment, MainFragment(), "tag:main").commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun addToDo() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment, CreateFragment(), "tag:create").addToBackStack(null).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                addToDo()
                return true
            }
            R.id.refresh -> {
                mainModel.refresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    sealed class UiState {
        data class Ok(val todos: List<ToDo>): UiState()
        object Loading : UiState()
        data class Error(val error: Exception): UiState()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    class MainViewModel : ViewModel() {

        private val refreshChannel = ConflatedBroadcastChannel<Unit>()

        fun refresh() {
            refreshChannel.offer(Unit)
        }

        fun toggle(todo: ToDo) {
            viewModelScope.launch {
                Repository.client.update(todo.copy(done = todo.done.not()))
                refreshChannel.offer(Unit)
            }
        }

        fun create(title: String) {
            viewModelScope.launch {
                Repository.client.create(title)
                refreshChannel.offer(Unit)
            }
        }

        private fun all(): LiveData<UiState> = liveData {
            emit(UiState.Loading)
            when (val result: Result<List<ToDo>, Exception> = Repository.client.getAll()) {
                is Result.Success -> emit(UiState.Ok(result.value))
                is Result.Error -> emit(UiState.Error(result.exception))
            }
        }

        val uiState: LiveData<UiState> = liveData {
            emitSource(all())
            refreshChannel.asFlow().collect {
                emitSource(all())
            }
        }
    }
}
