package pl.put.cocktailrecipes.views


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.models.Item
import pl.put.cocktailrecipes.utils.SuccessComponent
import pl.put.cocktailrecipes.utils.TimerComponent



@Composable
fun DetailScreen(
    item: Item, setImgSrc: (String) -> Unit, isTablet: Boolean, modifier: Modifier
) {
    val cocktail = CocktailRecipes.getCocktailDetails(item.name)
    val successMessage = remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    LaunchedEffect(item.name) {
        if (!isTablet) {
            setImgSrc(cocktail.thumbImgURL)
            return@LaunchedEffect
        }
        setImgSrc("")
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            if (isTablet) {
                item {
                    AsyncImage(
                        model = cocktail.thumbImgURL,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                SectionTitle("Ingredients")
                cocktail.ingredients.forEach { (_, ing) ->
                    val measure = ing.measure.takeIf(String::isNotBlank)?.let { " — $it" } ?: ""
                    Text(
                        "• ${ing.name}$measure",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                SectionTitle("Instructions")
                Text(
                    cocktail.instructions,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(64.dp))
            }

        }

        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimerComponent(
                    modifier = Modifier
                )

                FloatingActionButton(
                    onClick = { successMessage.value = "Message was sent!" },
                    containerColor = MaterialTheme.colorScheme.primary // Set the FAB color
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = MaterialTheme.colorScheme.onPrimary // Set the icon color
                    )
                }
            }
        }

        if (successMessage.value != "") {
            SuccessComponent(
                successMessage.value,
                3000,
                {
                    Log.d("", "Clear success message")
                    successMessage.value = ""
                },
                Modifier
                    .offset(y = 30.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    )
}