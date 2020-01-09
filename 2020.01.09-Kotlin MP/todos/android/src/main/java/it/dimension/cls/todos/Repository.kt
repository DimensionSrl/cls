package it.dimension.cls.todos

import Client
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
object Repository {

    val client = Client("http://10.10.1.54:8080")

}