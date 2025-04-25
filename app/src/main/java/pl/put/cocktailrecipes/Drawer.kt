package pl.put.cocktailrecipes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerWithHamburgerMenu() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /* Handle the drawer item click */ }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Another Item") },
                    selected = false,
                    onClick = { /* Handle the second item click */ }
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("App with Drawer") },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (drawerState.isClosed) {
                                    coroutineScope.launch { drawerState.open() }
                                } else {
                                    coroutineScope.launch { drawerState.close() }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Open drawer"
                                )
                            }
                        }
                    )
                },
                content = { padding ->
                    Text(
                        text = "Main Screen Content",
                        modifier = Modifier.padding(padding)
                    )
                }
            )
        }
    )
}

@Composable
fun DrawerContent() {
    Column {
        Text("Home", modifier = Modifier.padding(16.dp))
        Text("Settings", modifier = Modifier.padding(16.dp))
        Text("About", modifier = Modifier.padding(16.dp))
    }
}
