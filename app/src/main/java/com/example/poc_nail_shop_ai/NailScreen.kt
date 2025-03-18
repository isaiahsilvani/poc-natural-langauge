package com.example.poc_nail_shop_ai

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.poc_nail_shop_ai.data.local.MockDB

@Composable
fun NailScreen(
  nailViewModel: NailViewModel = viewModel()
) {
  val placeholderPrompt = stringResource(R.string.prompt_placeholder)
  val placeholderResult = stringResource(R.string.results_placeholder)
  var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
  var result by rememberSaveable { mutableStateOf(placeholderResult) }
  val uiState by nailViewModel.uiState.collectAsState()
  val context = LocalContext.current

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    Text(
      text = stringResource(R.string.baking_title),
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(16.dp)
    )

    Row(
      modifier = Modifier.padding(all = 16.dp)
    ) {
      TextField(
        value = prompt,
        label = { Text(stringResource(R.string.label_prompt)) },
        onValueChange = { prompt = it },
        modifier = Modifier
          .weight(0.8f)
          .padding(end = 16.dp)
          .align(Alignment.CenterVertically)
      )

      Button(
        onClick = {
          nailViewModel.sendPrompt(prompt)
        },
        enabled = prompt.isNotEmpty(),
        modifier = Modifier
          .align(Alignment.CenterVertically)
      ) {
        Text(text = stringResource(R.string.action_go))
      }
    }

    val setAppointments by MockDB.availableSlots.observeAsState()

    if (uiState is UiState.Success) {

      Spacer(modifier = Modifier.height(10.dp))

      Text((uiState as UiState.Success).outputText, fontSize = 18.sp)

      Spacer(modifier = Modifier.height(10.dp))
    }

    setAppointments?.let { appointments ->
      Text("Set Appointments", fontSize = 16.sp, modifier = Modifier.padding(vertical = 5.dp))
      LazyColumn {

      items(appointments) { item ->

        Text("\nDate: ${item.date}\nTime: ${item.time}\nProvider: ${item.provider}\n\n--------------")

      }
      }
    }


    if (uiState is UiState.Loading) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
  }
}

@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    NailScreen()
}