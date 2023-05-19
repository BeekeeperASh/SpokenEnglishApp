package com.example.spokenenglishapp.navigation

import androidx.compose.foundation.border
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.spokenenglishapp.ui.theme.Purple200
import com.example.spokenenglishapp.ui.theme.Purple400
import com.example.spokenenglishapp.ui.theme.Purple500

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
        backgroundColor = Color.White,
        modifier = Modifier.border(4.dp, Purple400)//.nestedScroll(nestedScrollConnection).offset{IntOffset(x = 0, y = -bottomBarOffsetHeightPx.value.roundToInt()) }
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
                selectedContentColor = Purple200,
                unselectedContentColor = Purple500
            )
        }
    }
}