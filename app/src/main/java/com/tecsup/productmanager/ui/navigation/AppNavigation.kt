package com.tecsup.productmanager.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.productmanager.ui.screens.*
import com.tecsup.productmanager.ui.viewmodel.AuthViewModel
import com.tecsup.productmanager.ui.viewmodel.ProductViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ProductList : Screen("product_list")
    object ProductAdd : Screen("product_add")
    object ProductEdit : Screen("product_edit/{productId}") {
        fun createRoute(productId: String): String {
            return "product_edit/$productId"
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()
    val productState by productViewModel.productState.collectAsState()

    LaunchedEffect(authState.userId) {
        authState.userId?.let { userId ->
            productViewModel.loadProducts(userId)
        }
    }

    LaunchedEffect(productState.successMessage) {
        productState.successMessage?.let {
            productViewModel.clearMessages()
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) Screen.ProductList.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                authState = authState,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onClearError = {
                    authViewModel.clearError()
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                authState = authState,
                onRegister = { email, password ->
                    authViewModel.register(email, password)
                }
            )
        }

        composable(Screen.ProductList.route) {
            ProductListScreen(
                products = productState.products,
                onAddProduct = {
                    navController.navigate(Screen.ProductAdd.route)
                },
                onEditProduct = { product ->
                    val route = Screen.ProductEdit.createRoute(product.id)
                    navController.navigate(route)
                },
                onDeleteProduct = { productId ->
                    productViewModel.deleteProduct(productId)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProductAdd.route) {
            ProductFormScreen(
                product = null,
                userId = authState.userId ?: "",
                onSave = { product ->
                    productViewModel.createProduct(product)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                isLoading = productState.isLoading
            )
        }

        composable(
            route = Screen.ProductEdit.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val product = productState.products.find { it.id == productId }

            if (product != null) {
                ProductFormScreen(
                    product = product,
                    userId = authState.userId ?: "",
                    onSave = { updatedProduct ->
                        productViewModel.updateProduct(product.id, updatedProduct)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    isLoading = productState.isLoading
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}