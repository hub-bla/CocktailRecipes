package pl.put.cocktailrecipes


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun DetailScreen(item: Item, modifier: Modifier, navigateBack: () -> Unit) {
    val cocktail = CocktailRecipes.getCocktailDetails(item.name)
    val scrollState = rememberScrollState()
    val indent = 20.dp
    val successMessage = remember { mutableStateOf("") }

    Log.d("sucessMesage", successMessage.value)
    Box(
        modifier = Modifier.fillMaxSize()
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

                IconButton(
                    onClick = {
                        navigateBack()

                    },
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Column(
                modifier = modifier.padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = item.name,
                    modifier = Modifier
                        .padding(bottom = 26.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
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
                SectionTitle("Ingredients")
                Column(modifier = Modifier.fillMaxWidth()) {
                    for ((_, ingredient) in cocktail.ingredients) {
                        val measure =
                            if (ingredient.measure.isNotEmpty()) " - ${ingredient.measure}" else ""
                        Row {
                            Text(text = "\u2022", modifier = Modifier.width(indent))
                            Text("${ingredient.name}$measure")
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { successMessage.value = "Message was sent!" },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Send SMS with ingredients")
                }
            }

            SectionTitle("Instructions", modifier = Modifier)
            Text(cocktail.instructions)
        }

        TimerComponent(
            Modifier
                .padding(20.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-40).dp)
        )
        if (successMessage.value != "") {
            SuccessComponent(
                successMessage.value,
                3000,
                {
                    Log.d("", "Clear sucess message")
                    successMessage.value = ""
                },
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 30.dp)
            )
        }

    }
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

