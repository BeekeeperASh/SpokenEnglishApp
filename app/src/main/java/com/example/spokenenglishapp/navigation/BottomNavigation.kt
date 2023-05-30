package com.example.spokenenglishapp.navigation

import androidx.compose.foundation.border
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(
    navController: NavController
) {

    val listItem = listOf(
        NavigateItem.Screen1,
        NavigateItem.Screen2,
        NavigateItem.Screen3
    )
    androidx.compose.material.BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.border(4.dp, MaterialTheme.colorScheme.secondary)//.nestedScroll(nestedScrollConnection).offset{IntOffset(x = 0, y = -bottomBarOffsetHeightPx.value.roundToInt()) }
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        listItem.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = "im_icon",
                        //modifier = Modifier.size(40.dp)
                    )
                },
                label = {
                    Text(text = item.title, fontSize = 8.sp)
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}