package com.alquimia.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Mensagens",
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Filled.Email,
        route = "messages"
    ),
    BottomNavItem(
        title = "Descobrir",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Filled.Search,
        route = "profiles"
    ),
    BottomNavItem(
        title = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Filled.Person,
        route = "settings"
    )
)

@Composable
fun AlquimiaBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
