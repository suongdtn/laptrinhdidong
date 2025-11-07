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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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

// Data class Product (Không thay đổi)
data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: String = "",
    val imageUrl: String = ""
)

@Composable
fun HomeScreen(navController: NavHostController) {
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

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var editingId by remember { mutableStateOf<String?>(null) }

    // ++ BẢNG MÀU MỚI "TIỆM BÁNH MỲ"
    val mauNen = Color(0xFFFFFBF0) // Màu kem nền
    val mauTieuDe = Color(0xFFD4A373) // Màu vàng đồng (vỏ bánh)
    val mauNhanChinh = Color(0xFF6D4C41) // Màu nâu cà phê (cho nút, viền)
    val mauHuy = Color(0xFFC62828) // Màu đỏ sậm
    val mauSua = Color(0xFF558B2F) // Màu xanh olive
    val mauChuPhu = Color(0xFF4E4E4E) // Màu xám đen cho chữ
    // --- Kết thúc bảng màu

    val inputShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mauNen) // ++ THAY ĐỔI: Nền màu kem
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
                    navController.navigate(Screen.Signin.rout)
                },
                colors = ButtonDefaults.buttonColors(containerColor = mauHuy), // ++ THAY ĐỔI
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Đăng xuất", color = Color.White)
            }
        }

        // --- HEADER ---
        Text(
            text = "Huynh Hoa",
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
            text = "Quản lý sản phẩm",
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = mauChuPhu
        )
        Spacer(Modifier.height(20.dp))


        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên sản phẩm") },
            modifier = Modifier.fillMaxWidth(),
            shape = inputShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = mauNhanChinh,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = mauNhanChinh,
                cursorColor = mauNhanChinh
            )
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Loại sản phẩm") },
            modifier = Modifier.fillMaxWidth(),
            shape = inputShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = mauNhanChinh,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = mauNhanChinh,
                cursorColor = mauNhanChinh
            )
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Giá sản phẩm") },
            modifier = Modifier.fillMaxWidth(),
            shape = inputShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = mauNhanChinh,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = mauNhanChinh,
                cursorColor = mauNhanChinh
            )
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Link hình ảnh (vd: https://...)") },
            modifier = Modifier.fillMaxWidth(),
            shape = inputShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = mauNhanChinh,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = mauNhanChinh,
                cursorColor = mauNhanChinh
            )
        )


        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && category.isNotEmpty() && price.isNotEmpty() && imageUrl.isNotEmpty()) {
                    val product = hashMapOf(
                        "name" to name,
                        "category" to category,
                        "price" to price,
                        "imageUrl" to imageUrl
                    )

                    if (editingId == null) {
                        firestore.collection("products").add(product)
                        Toast.makeText(context, "Đã thêm sản phẩm!", Toast.LENGTH_SHORT).show()
                    } else {
                        firestore.collection("products").document(editingId!!)
                            .update(product as Map<String, Any>)
                        Toast.makeText(context, "Đã cập nhật sản phẩm!", Toast.LENGTH_SHORT).show()
                        editingId = null
                    }
                    name = ""
                    category = ""
                    price = ""
                    imageUrl = ""
                } else {
                    Toast.makeText(context, "Vui lòng điền đủ thông tin!", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = mauNhanChinh), // ++ THAY ĐỔI
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(
                text = if (editingId == null) "Thêm sản phẩm mới" else "Cập nhập sản phẩm",
                color = Color.White, // Chữ trắng nổi bật trên nền nâu
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White), // Card màu trắng
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)) // Viền xám nhạt
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
                                    .size(70.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, color = mauChuPhu)
                            Text("Loại: ${product.category}", fontSize = 13.sp)
                            Text("Giá: ${product.price} VNĐ", fontSize = 13.sp)
                        }
                        IconButton(onClick = {
                            name = product.name
                            category = product.category
                            price = product.price
                            imageUrl = product.imageUrl
                            editingId = product.id
                        }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Sửa",
                                tint = mauSua // ++ THAY ĐỔI
                            )
                        }
                        IconButton(onClick = {
                            firestore.collection("products").document(product.id).delete()
                            Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Default.Delete, contentDescription = "Xóa",
                                tint = mauHuy // ++ THAY ĐỔI
                            )
                        }
                    }
                }
            }
        }
    }
}