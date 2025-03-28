package pl.put.cocktailrecipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.reflect.full.memberProperties

@Composable
fun DetailScreen(cocktailName: String, modifier: Modifier, navController: NavController) {
    val cocktail = CocktailRecipes.getCocktailDetails(cocktailName)

    @OptIn(ExperimentalMaterial3Api::class)
    (Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(cocktail.ingredients.toList()) { index, ing ->
                    val measure =
                        if (ing.second.measure.isNotEmpty()) " - ${ing.second.measure}" else ""
                    val indent = 20.dp
                    Row {
                        Text(
                            text = "\u2022",
                            modifier = Modifier.width(indent),
                        )
                        Text("${ing.second.name}$measure")
                    }
                }
            }
            SectionTitle("Instructions")
            Text(cocktail.instructions)
        }

    })

}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
    )
}