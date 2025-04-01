package com.example.ninovafisha.Presentation.Screens.MainScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ninovafisha.Domain.Constant
import com.example.ninovafisha.Domain.Models.Event
import com.example.ninovafisha.Domain.Models.Profile
import com.example.ninovafisha.Domain.Models.typeEvent
import com.example.ninovafisha.Domain.States.ActualState
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelMainScreen: ViewModel() {

    /*viewModelScope.launch {
        val prof = Constant.supabase.postgrest.from("public", "Profile").select().decodeList<Profile>()
        val filt = prof.firstOrNull { p -> p.id == Constant.supabase.auth.currentUserOrNull()?.id}
        var name = filt?.name
    }*/
    private val _actualState = MutableStateFlow<ActualState>(ActualState.Initialized)
    val actualState: StateFlow<ActualState> = _actualState.asStateFlow()

    private val _types = MutableLiveData<List<typeEvent>>()
    val types: LiveData<List<typeEvent>> get() = _types

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private var FiltEvent: List<Event> = listOf()
    private var filtType: MutableList<Int> = mutableListOf()

    fun RemoveType(id:Int){
        filtType.remove(id)
    }
    fun AddType(id:Int){
        filtType.add(id)
    }

    fun refresh() {
        loadEvents()
        loadTypes()
    }

    init {
        loadEvents()
        loadTypes()
    }

    fun loadEvents(){
        _actualState.value = ActualState.Loading
        viewModelScope.launch {
            try{
                FiltEvent = Constant.supabase.postgrest.from("Events").select().decodeList()
                _events.value = FiltEvent
                _actualState.value = ActualState.Initialized
            }
            catch (ex: Exception) {
                _actualState.value = ActualState.Error(ex.message ?: "Ошибка получения данных")
            }
        }
    }

    fun loadTypes(){
        _actualState.value = ActualState.Loading
        viewModelScope.launch {
            try{
                _types.value = Constant.supabase.postgrest.from("type_event").select().decodeList()
                _actualState.value = ActualState.Initialized
            }
            catch (ex: Exception) {
                _actualState.value = ActualState.Error(ex.message ?: "Ошибка получения данных")
            }
        }
    }

    fun filtevent(filtString:String){
        if(filtType.isNotEmpty()){
            var filtered = FiltEvent.filter { x -> x.title.contains(filtString) || x.desc.contains(filtString) }
            filtered = filtered.filter { x -> filtType.contains(x.typeEvent)}
            _events.value = filtered
        }
        else{
            val filtered = FiltEvent.filter { x -> x.title.contains(filtString) || x.desc.contains(filtString) }
            _events.value = filtered
        }
    }



    /*suspend fun GetName(): String{
        return withContext(Dispatchers.IO) {
            try {
                var name = Constant.supabase.postgrest.from("public", "Profile").select().decodeList<Profile>().firstOrNull { p -> p.id == Constant.supabase.auth.currentUserOrNull()!!.id }!!.name
                name
            } catch (ex: AuthRestException) {
                "Ошибка: " + ex.message
            }
            catch (ex: Exception){
                "авторизация не выполнена"
            }
        }
    }*/
    fun GetOut(){
        viewModelScope.launch {
            Constant.supabase.auth.signOut()
        }
    }
}