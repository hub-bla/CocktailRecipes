package pl.put.cocktailrecipes

import android.util.Log
import io.ktor.client.engine.android.Android
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.full.memberProperties

@Serializable
data class CocktailResponse(
    val idDrink: String,
    val strDrink: String,
    val strDrinkAlternate: String?,
    val strTags: String?,
    val strVideo: String?,
    val strCategory: String,
    val strIBA: String?,
    val strAlcoholic: String,
    val strGlass: String,
    val strInstructions: String,
    val strDrinkThumb: String,
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    val strIngredient10: String?,
    val strIngredient11: String?,
    val strIngredient12: String?,
    val strIngredient13: String?,
    val strIngredient14: String?,
    val strIngredient15: String?,
    val strMeasure1: String?,
    val strMeasure2: String?,
    val strMeasure3: String?,
    val strMeasure4: String?,
    val strMeasure5: String?,
    val strMeasure6: String?,
    val strMeasure7: String?,
    val strMeasure8: String?,
    val strMeasure9: String?,
    val strMeasure10: String?,
    val strMeasure11: String?,
    val strMeasure12: String?,
    val strMeasure13: String?,
    val strMeasure14: String?,
    val strMeasure15: String?,
    val strImageSource: String?,
    val strImageAttribution: String?,
    val strCreativeCommonsConfirmed: String,
    val dateModified: String
)

@Serializable
data class CocktailsResponse(
    val drinks: List<CocktailResponse>
)

data class Ingredient(
    var name: String = "",
    var measure: String = "",
)

data class Cocktail(
    var name: String = "",
    var instructions: String = "",
    val ingredients: HashMap<String, Ingredient> = HashMap()
)


const val COCKTAIL_URL = "http://www.thecocktaildb.com/api/json/v1/1/search.php?f=a"

fun parseToCocktail(cocktailResponse: CocktailResponse): Cocktail {
    val cocktail = Cocktail()
    val regex = """(\d+)$""".toRegex()

    for (property in CocktailResponse::class.memberProperties) {
        val propertyValue = property.get(cocktailResponse)

        if (propertyValue == null) {
            continue
        }

        if (property.name.contains("Ingredient")) {
            val matchResult = regex.find(property.name)
            val ingNumber = matchResult?.groups?.get(1)?.value?.toInt()
            val ingredient = Ingredient()
            ingredient.name = propertyValue.toString()
            cocktail.ingredients["Ingredient$ingNumber"] = ingredient
        } else if (property.name.contains("Measure")) {
            val matchResult = regex.find(property.name)
            val ingNumber = matchResult?.groups?.get(1)?.value?.toInt()
            cocktail.ingredients["Ingredient$ingNumber"]?.measure = propertyValue.toString()
        } else if (property.name.contains("strInstructions")) {
            cocktail.instructions = propertyValue.toString()
        }
    }

    return cocktail
}

object CocktailRecipes {
    private val client = HttpClient(Android)
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val cocktails = HashMap<String, Cocktail>()
    private var isInitialized = false

    suspend fun init() {
        if (isInitialized) {
            return
        }
        isInitialized = true

        val response = client.get(COCKTAIL_URL).bodyAsText()
        Log.d("response", response)
        val cocktailsData = json.decodeFromString<CocktailsResponse>(response)

        for (cocktailResponse in cocktailsData.drinks) {
            cocktails[cocktailResponse.strDrink] = parseToCocktail(cocktailResponse)
        }
    }

    fun getCocktailNames(): Set<String> {
        return cocktails.keys
    }

    fun getCocktailDetails(cocktailName: String): Cocktail {
        return cocktails.getValue(cocktailName)
    }

}