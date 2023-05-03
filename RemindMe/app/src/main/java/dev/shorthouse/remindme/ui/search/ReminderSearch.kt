package dev.shorthouse.remindme.ui.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar

@Composable
@Destination
fun ReminderSearchScreen(
    viewModel: ReminderSearchViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReminderSearchScreen(
        uiState = uiState,
        onNavigateUp = { navigator.navigateUp() }
    )
}

@Composable
fun ReminderSearchScreen(
    uiState: ReminderSearchUiState,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ReminderSearchTopBar(
                onNavigateUp = onNavigateUp
            )
        },
        content = { scaffoldPadding ->
            if (!uiState.isLoading) {
                ReminderSearchContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun ReminderSearchTopBar(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    RemindMeTopAppBar(
        title = stringResource(R.string.top_bar_title_reminder_search),
        navigationIcon = {
            IconButton(onClick = { onNavigateUp() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_app_bar_back)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun ReminderSearchContent(modifier: Modifier) {
    Text("Reminder search content!")
}
