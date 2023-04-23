package com.example.spokenenglishapp.navigation

import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.spokenenglishapp.ui.theme.Purple300

@Composable
fun TopNavigation(
    navController: NavController
) {
    val listItem = listOf(
        NavigateItem.Screen5,
        NavigateItem.Screen6
    )
    androidx.compose.material.TopAppBar(
        backgroundColor = Purple300
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
                        contentDescription = "im_icon"
                    )
                },
                label = {
                    Text(text = item.title, fontSize = 8.sp)
                },
                selectedContentColor = Color.Blue,
                unselectedContentColor = Color.Gray
            )
        }
    }
}