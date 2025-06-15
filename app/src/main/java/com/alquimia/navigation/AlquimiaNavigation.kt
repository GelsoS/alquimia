package com.alquimia.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alquimia.ui.components.AlquimiaBottomNavigation
import com.alquimia.ui.screens.* // Importa todas as telas, incluindo LoginScreen
import com.alquimia.ui.screens.LoginScreen // Import explícito para garantir

@Composable
fun AlquimiaNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf("profiles", "messages", "settings")
    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                AlquimiaBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = if (showBottomNav) Modifier.padding(paddingValues) else Modifier
        ) {
            composable("login") {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate("forgot_password")
                    },
                    onNavigateToProfiles = {
                        navController.navigate("profiles") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onNavigateToProfiles = {
                        navController.navigate("profiles") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("profiles") {
                ProfilesScreen(
                    onNavigateToProfileDetail = { userId ->
                        navController.navigate("profile_detail/$userId")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }

            composable("messages") {
                MessagesScreen(
                    onNavigateToChat = { userId ->
                        navController.navigate("chat/$userId")
                    }
                )
            }

            composable("profile_detail/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ProfileDetailScreen(
                    userId = userId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToChat = { userIdToChat ->
                        navController.navigate("chat/$userIdToChat")
                    }
                )
            }

            composable("chat/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ChatScreen(
                    userId = userId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToEditProfile = {
                        navController.navigate("edit_profile")
                    }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
