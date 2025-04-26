package pl.put.cocktailrecipes

import android.util.Log
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.utils.CollapsingToolbar
import pl.put.cocktailrecipes.utils.Loading
import pl.put.cocktailrecipes.utils.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val isDataLoading = remember { mutableStateOf(true) }
    val expandedHeight = 300.dp
    val collapsedHeight = 64.dp
    val density = LocalDensity.current
    val maxOffsetPx = remember {
        with(density) { (expandedHeight - collapsedHeight).toPx() }
    }
    val collapseOffset = remember { mutableStateOf(0f) }
    val showAnimation = remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val nestedConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0f && collapseOffset.value < maxOffsetPx) {
                    val consumed = (-delta).coerceAtMost(maxOffsetPx - collapseOffset.value)
                    collapseOffset.value += consumed
                    return Offset(0f, -consumed)
                }
                if (delta > 0f
                    && listState.firstVisibleItemIndex == 0
                    && listState.firstVisibleItemScrollOffset == 0
                    && collapseOffset.value > 0f
                ) {
                    val consumed = delta.coerceAtMost(collapseOffset.value)
                    collapseOffset.value -= consumed
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }
        }
    }

    val imgSrc = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Log.d("loading", "loadings")
        withContext(Dispatchers.IO) {
            CocktailRecipes.init()
        }
        isDataLoading.value = false
    }


    if (showAnimation.value) {
        Loading({ showAnimation.value = false }, isDataLoading.value)
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerLayout(
                        scope = scope,
                        drawerState = drawerState,
                        navController = navController
                    )
                }
            }
        ) {
            Scaffold(
                modifier = if (imgSrc.value != "") Modifier.nestedScroll(nestedConnection) else Modifier,
                topBar = {
                    if (imgSrc.value != "") {
                        val headerHeight = with(density) {
                            (expandedHeight.toPx() - collapseOffset.value)
                                .coerceAtLeast(collapsedHeight.toPx())
                                .toDp()
                        }
                        CollapsingToolbar(
                            headerHeight = headerHeight,
                            collapsedHeight = collapsedHeight,
                            imgSrc = imgSrc.value,
                            expandedHeight = expandedHeight,
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onBackClick = { navController.navigateUp() }
                        )
                    } else {
                        TopBar(
                            scope = scope,
                            drawerState = drawerState,
                            modifier = Modifier,
                            navigateBack = { navController.navigateUp() })
                    }
                }
            ) { innerPadding ->
                ComposeNavigation(
                    navController = navController,
                    padding = innerPadding,
                    setImgSrc = { newSrc -> imgSrc.value = newSrc }
                )
            }
        }
    }
}
