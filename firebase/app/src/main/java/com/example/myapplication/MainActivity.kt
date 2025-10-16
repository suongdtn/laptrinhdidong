package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚡ Khởi tạo Firebase (chỉ cần nếu chưa gọi elsewhere)
        // FirebaseApp.initializeApp(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirebaseExampleScreen()
                }
            }
        }
    }
}

@Composable
fun FirebaseExampleScreen() {
    var message by remember { mutableStateOf("Hello, Firebase!") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("message")
            myRef.setValue("Hello, World!") // ghi dữ liệu lên Firebase
            message = "Đã gửi dữ liệu lên Firebase!"
        }) {
            Text("Gửi dữ liệu lên Firebase")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        FirebaseExampleScreen()
    }
}
