package com.spitzer.recipes.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing

data class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    @DrawableRes val icon: Int
)

@Composable
fun BottomNavBar(
    screens: List<BottomNavItem>,
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    BottomNavigation {
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = screen.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(bottom = Spacing.HALF.dp)
                            .height(25.dp),
                        tint = if (isSelected) RTheme.colors.p00p00 else RTheme.colors.n00n99
                    )
                },
                label = {
                    Text(
                        text = stringResource(screen.titleRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.wrapContentWidth(),
                        color = if (isSelected) RTheme.colors.p00p00 else RTheme.colors.n00n99,
                        style = RTheme.typography.caption1,
                        fontWeight = FontWeight.Light
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(1f / screens.size)
                    .weight(1f)
                    .background(RTheme.colors.n99n00)
            )
        }
    }
}
