package pl.put.cocktailrecipes.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.distinctUntilChanged
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.models.Item




@Composable
fun CocktailListHorizontalPager(
    onClick: (Item) -> Unit,
    modifier: Modifier = Modifier,
    categoryName: Item,
    onTitleChange: (String) -> Unit
) {
    val items = CocktailRecipes.getCategories()
    val initialPageIdx = items.indexOfFirst { it == categoryName.name }
    val pagerState = rememberPagerState(
        initialPage = initialPageIdx,
        pageCount = { items.size }
    )

    Surface (
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.settledPage }
                    .distinctUntilChanged()
                    .collect { pageIdx ->
                        onTitleChange(items[pageIdx])
                    }
            }

            val currentItem = Item(items[page])

            CocktailList(
                onClick = onClick,
                modifier = Modifier.fillMaxSize(),
                categoryName = currentItem
            )
        }
    }
}
