package com.example.gkltdd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gkltdd.ui.theme.GkltddTheme
import com.example.gkltdd.SignIn


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GkltddTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
                ){

                    Mynavigation()

                }
            }
        }
    }
}

@Composable
fun Mynavigation()
{
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Signin.rout
    ){
        composable(Screen.Signin.rout){
            SignIn(navController = navController)
        }
        composable(route = Screen.Home.rout){
            HomeScreen(navController = navController)
        }
        composable(Screen.Signup.rout){
            SignUp(navController = navController)
        }
        composable(route = "user_screen") {
            UserScreen(navController = navController)
        }


    }
}