package pl.put.cocktailrecipes.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.models.Item



@Composable
fun CocktailListHorizontalPager(
    onClick: (Item) -> Unit,
    modifier: Modifier,
    categoryName: Item
) {

    val items = CocktailRecipes.getCategories()
    val initialPageIdx = items.indexOfFirst { it == categoryName.name }
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = initialPageIdx,pageCount = {items.size},)

    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val currentItem = Item(items[page])
            CocktailList(
                onClick = onClick,
                modifier = Modifier.fillMaxSize(),
                categoryName = currentItem
            )
        }

    }
}