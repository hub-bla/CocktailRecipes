package pl.put.cocktailrecipes.views

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.util.Log
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import pl.put.cocktailrecipes.api.Cocktail
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.models.Item


@Composable
fun CocktailList(onClick: (Item) -> Unit, modifier: Modifier, categoryName: Item) {

    val cocktailNames = remember {mutableStateOf(emptyList<String>())}
    val gridState = rememberLazyGridState()

    LaunchedEffect(categoryName) {
        cocktailNames.value = CocktailRecipes.getCocktailNamesByCategory(categoryName)
    }


    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = gridState,
        modifier = modifier
            .fillMaxSize(0.5f),
        contentPadding = PaddingValues(
            top = 50.dp,
            bottom = 50.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        items(cocktailNames.value) { cocktailName ->
            CocktailCard(CocktailRecipes.getCocktailDetails(cocktailName), onClick)
        }
    }
}

@Composable
fun CocktailCard(cocktail: Cocktail, onClick: (Item) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Card(
            onClick = { onClick(Item(cocktail.name)) },
            modifier = Modifier
                .fillMaxWidth(0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cocktail.thumbImgURL)
                        .crossfade(true)
                        .build(),
                    contentDescription = cocktail.name,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .height(150.dp)
                        .fillMaxWidth(1f),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(cocktail.name, modifier = Modifier)
                }
            }
        }
    }
}



