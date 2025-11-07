package com.example.gkltdd

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

// Bạn có thể giữ hoặc xóa data class này nếu nó đã tồn tại ở file khác

@Composable
fun UserScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var products by remember { mutableStateOf(listOf<Product>()) }

    LaunchedEffect(Unit) {
        firestore.collection("products").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
            }
        }
    }

    val mauNen = Color(0xFFFFFBF0)
    val mauTieuDe = Color(0xFFD4A373)
    val mauNhanChinh = Color(0xFF6D4C41)
    val mauHuy = Color(0xFFC62828)
    val mauChuPhu = Color(0xFF4E4E4E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mauNen)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Signin.rout) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = mauHuy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Đăng xuất", color = Color.White)
            }
        }

        Text(
            text = "Tiệm bánh mỳ",
            style = TextStyle(
                fontSize = 45.sp,
                color = mauTieuDe,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Thực đơn hôm nay",
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = mauChuPhu
        )
        Spacer(Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(products) { product ->
                UserProductCard(product = product, mauNhanChinh = mauNhanChinh, mauChuPhu = mauChuPhu)
            }
        }
    }
}

@Composable
fun UserProductCard(product: Product, mauNhanChinh: Color, mauChuPhu: Color) {
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(1) }

    val totalPrice by remember {
        derivedStateOf {
            val pricePerItem = product.price.toDoubleOrNull() ?: 0.0
            pricePerItem * quantity
        }
    }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrl),
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(90.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, color = mauChuPhu)
                Text("Loại: ${product.category}", fontSize = 13.sp)
                Text("Giá: ${currencyFormat.format(product.price.toDoubleOrNull() ?: 0.0)}", fontSize = 13.sp)

                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "Trừ", tint = mauNhanChinh)
                        }

                        Text(
                            "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(onClick = { quantity++ }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Cộng", tint = mauNhanChinh)
                        }
                    }

                    Text(
                        currencyFormat.format(totalPrice),
                        fontWeight = FontWeight.Bold,
                        color = mauNhanChinh,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Đặt thành công $quantity ${product.name}!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = mauNhanChinh),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Đặt món", color = Color.White)
                }
            }
        }
    }
}