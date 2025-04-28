package pl.put.cocktailrecipes

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.utils.CollapsingToolbar
import pl.put.cocktailrecipes.utils.Loading
import pl.put.cocktailrecipes.utils.TopBar

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
    val topBarTitle = remember { mutableStateOf("") }

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
                            navigateBack = { navController.navigateUp() },
                            title = topBarTitle.value)
                    }
                }
            ) { innerPadding ->
                ComposeNavigation(
                    navController = navController,
                    padding = innerPadding,
                    setImgSrc = { newSrc -> imgSrc.value = newSrc},
                    onTitleChange = {newTitle -> topBarTitle.value = newTitle}
                )
            }
        }
    }
}
