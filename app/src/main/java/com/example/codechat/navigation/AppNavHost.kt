package com.example.codechat.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.codechat.core.navigation.Routes
import com.example.codechat.features.auth.login.LoginScreen
import com.example.codechat.features.auth.login.LoginViewModel
import com.example.codechat.features.auth.register.RegisterScreen
import com.example.codechat.features.auth.register.RegisterViewModel
import com.example.codechat.features.chat.ChatScreen
import com.example.codechat.features.chat.ChatViewModel
import com.example.codechat.features.main.MainScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.LOGIN) {inclusive = true}
                    launchSingleTop = true
                } },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                viewModel = loginViewModel
            )
//            LoginScreen(navController)
        }

        composable(Routes.REGISTER) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.LOGIN) },
                onLoginClick = { navController.popBackStack() },
                viewModel = registerViewModel
                )
        }

        composable(Routes.MAIN) {
            MainScreen()
        }

        composable(Routes.PROFILE) {

        }

        composable(Routes.HOME) {

        }

    }
}