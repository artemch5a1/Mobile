package com.example.ninovafisha.Presentation.Screens.ListEventsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.zIndex
import com.example.ninovafisha.Domain.States.ActualState
import com.example.ninovafisha.Presentation.Screens.Components.EventCard
import com.example.ninovafisha.Presentation.Screens.Components.TypeBut
import com.example.ninovafisha.Presentation.Screens.Components.myFieldSearch


@Composable
fun ListEventsScreen(controlNav: NavHostController, viewModelListEventsScreen: ViewModelListEventsScreen = viewModel()) {
    val textSearch = remember { mutableStateOf("") }
    val actualState by viewModelListEventsScreen.actualState.collectAsState()
    val events = viewModelListEventsScreen.events.observeAsState(emptyList())
    val types = viewModelListEventsScreen.types.observeAsState(emptyList())
    val eventsState by viewModelListEventsScreen.eventsState.collectAsState()
    val filtType = viewModelListEventsScreen.filtType


    when(eventsState){
        true, false ->{
            viewModelListEventsScreen.filtevent(textSearch.value)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .zIndex(1f)
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(top = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    myFieldSearch(
                        myText = "Найти",
                        text = textSearch.value,
                        onValueChange = {
                            textSearch.value = it
                            viewModelListEventsScreen.filtevent(it)
                        },
                        OnClick = {viewModelListEventsScreen.RememberFiltState(textSearch.value);
                            viewModelListEventsScreen.refresh()}
                    )
                }

                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(types.value.size) { index ->
                        TypeBut(
                            typeEv = types.value[index].copy(),
                            OnClick = { viewModelListEventsScreen.toggleType(typeId = types.value[index].id, textSearch = textSearch.value) },
                            isSelected = viewModelListEventsScreen.filtType.contains(types.value[index].id)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when(actualState) {
                is ActualState.Initialized, is ActualState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(events.value.size) { index ->
                            EventCard(event = events.value[index].copy(), controlNav)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                is ActualState.Error -> {
                    Text(
                        text = (actualState as ActualState.Error).message,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is ActualState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}