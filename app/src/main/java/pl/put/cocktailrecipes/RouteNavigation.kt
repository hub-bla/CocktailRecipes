package pl.put.cocktailrecipes

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.adaptive.*
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Parcelize
class Item(val name: String) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RouteNavigation() {
    val navigator = rememberListDetailPaneScaffoldNavigator<Item>()
    val currentPane = remember { mutableStateOf<ThreePaneScaffoldRole?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val isDataLoading = remember { mutableStateOf(true) }

    LaunchedEffect(navigator.currentDestination) {
        currentPane.value = navigator.currentDestination?.pane
    }

    LaunchedEffect(Unit) {
        Log.d("loading", "loadings")
        withContext(Dispatchers.IO) {
            // Perform network call on IO thread
            CocktailRecipes.init()  // This is your suspend function that fetches data
        }
        isDataLoading.value = false
    }

    var showAnimation = remember { mutableStateOf(true) }

    if (showAnimation.value) {
        Loading({ showAnimation.value = false }, isDataLoading.value)
    } else {


        BackHandler(enabled = currentPane.value != null) {
            when (currentPane.value) {
                ThreePaneScaffoldRole.Primary -> {
                    navigator.navigateTo(
                        pane = ThreePaneScaffoldRole.Secondary,
                        content = navigator.currentDestination?.content ?: return@BackHandler
                    )
                }

                ThreePaneScaffoldRole.Tertiary -> {
                    navigator.navigateTo(
                        pane = ThreePaneScaffoldRole.Primary,
                        content = navigator.currentDestination?.content ?: return@BackHandler
                    )
                }

                else -> navigator.navigateBack()
            }
        }
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,

            listPane = {

                CategoryList(
                    modifier = Modifier,
                    onClick = { category: Item ->
                        coroutineScope.launch {
                            navigator.navigateTo(
                                pane = ThreePaneScaffoldRole.Primary,
                                content = category
                            )
                        }
                    }
                )
            },

            detailPane = {
                AnimatedPane {
                    navigator.currentDestination?.content?.let {
                        CocktailList(
                            modifier = Modifier,
                            cocktailName = it,
                            currentPane = currentPane.value ?: ThreePaneScaffoldRole.Secondary,
                            onClick = { cocktail: Item ->
                                coroutineScope.launch {
                                    navigator.navigateTo(
                                        pane = ThreePaneScaffoldRole.Tertiary,
                                        content = cocktail
                                    )
                                }
                            }
                        )
                    }
                }
            },

            extraPane = {
                AnimatedPane {
                    navigator.currentDestination?.content?.let {
                        DetailScreen(
                            item = it,
                            modifier = Modifier,
                            navigateBack = {
                                coroutineScope.launch {
                                    navigator.navigateBack()
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
