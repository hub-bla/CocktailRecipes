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


@Parcelize
class Item(val name: String) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RouteNavigation() {
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            CocktailList(
                onClick = { item ->
                    navigator.navigateTo(
                        pane = ListDetailPaneScaffoldRole.Detail,
                        content = item
                    )
                },
                modifier = Modifier
            )
        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.content?.let { item ->
                    DetailScreen(
                        item = item as Item,
                        navigateBack = {
                            if (navigator.canNavigateBack()) {
                                scope.launch { navigator.navigateBack() }
                            } else {
                                scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.List) }
                            }
                        },
                        modifier = Modifier

                    )
                }
            }
        }
    )
}
