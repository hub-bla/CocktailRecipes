package pl.put.cocktailrecipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.reflect.full.memberProperties

@Composable
fun DetailScreen(cocktailName: String, modifier: Modifier, navController: NavController) {
    val cocktail = CocktailRecipes.getCocktailDetails(cocktailName)
    val scrollState = rememberScrollState() // Create a scroll state
    val indent = 20.dp

    @OptIn(ExperimentalMaterial3Api::class)

    (Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, bottom = 150.dp)
        ) {
            Box(
                modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
            ) {
                IconButton(onClick = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            Column(
                modifier = modifier.padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)

            ) {
                Text(
                    text = cocktailName,
                    modifier = Modifier
                        .padding(bottom = 26.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                SectionTitle("Ingredients")
                Column(modifier = Modifier.fillMaxWidth()) {
                    for ((_, ingredient) in cocktail.ingredients) {
                        val measure =
                            if (ingredient.measure.isNotEmpty()) " - ${ingredient.measure}" else ""
                        Row {
                            Text(
                                text = "\u2022",
                                modifier = Modifier.width(indent),
                            )
                            Text("${ingredient.name}$measure")
                        }
                    }
                }

            }
            SectionTitle("Instructions")
            Text(cocktail.instructions)

        }

        TimerComponent(
            Modifier
                .padding(20.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-40).dp)
        )
    })

}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    )
}