package pl.put.cocktailrecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.util.Log


@Composable
fun CategoryList(onClick: (Item) -> Unit, modifier: Modifier){

    val categories = remember { mutableStateOf(emptyList<String>()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        CocktailRecipes.init()
        isLoading.value = false
        categories.value = CocktailRecipes.getCategories()
    }

    if (isLoading.value) {
        Loading()
    }
    else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = modifier
                .background(Color(0xffedebe4))
                .fillMaxSize(0.5f),
            contentPadding = PaddingValues(
                top = 50.dp,
                bottom = 50.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories.value.toList(), key = { it }) { cocktailCategory ->
                CategoryCard(cocktailCategory, onClick)
            }
        }

        androidx.compose.foundation.lazy.LazyColumn(modifier = modifier) {
            items(categories.value.size) { index ->
                val category = categories.value[index]
                Text(
                    text = category,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, onClick: (Item) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Card(
            onClick = { onClick(Item(CocktailRecipes.mapCategoryToDrinkName(Item(category))))},
            modifier = Modifier
                .fillMaxWidth(0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(CocktailRecipes.getCategoryThumbImgURL(category))
                        .crossfade(true)
                        .build(),
                    contentDescription = category,
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
                    Text(category, modifier = Modifier)
                }
            }
        }
    }
}