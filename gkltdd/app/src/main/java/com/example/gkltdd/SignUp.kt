package com.example.gkltdd

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignUp(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val (focusUsername, focusPassword) = remember { FocusRequester.createRefs() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }


    val mauNen = Color(0xFFFFFBF0)
    val mauNhanChinh = Color(0xFF6D4C41)
    val mauChuPhu = Color(0xFF4E4E4E)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mauNen),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.40f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.signup),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Spacer(modifier = Modifier.size(15.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(
                    text = "Welcome To ",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = mauChuPhu
                )
                Text(
                    text = "Huynh Hoa",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = mauNhanChinh,
                )
            }

            Spacer(modifier = Modifier.size(28.dp))

            // Email
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .focusRequester(focusUsername),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusPassword.requestFocus() }),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = mauNhanChinh,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = mauNhanChinh,
                    cursorColor = mauNhanChinh
                ),
                label = { Text(text = "Email", color = mauNhanChinh) }
            )

            Spacer(modifier = Modifier.size(9.dp))

            // Password
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .focusRequester(focusPassword),
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Password Toggle",
                            tint = mauNhanChinh
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = mauNhanChinh,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = mauNhanChinh,
                    cursorColor = mauNhanChinh
                ),
                label = { Text(text = "Password", color = mauNhanChinh) }
            )

            Spacer(modifier = Modifier.size(9.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Confirm Password Toggle",
                            tint = mauNhanChinh // ++ THAY ĐỔI
                        )
                    }
                },
                colors = TextFieldDefaults.colors( // ++ THAY ĐỔI
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = mauNhanChinh,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = mauNhanChinh,
                    cursorColor = mauNhanChinh
                ),
                label = { Text(text = "Confirm Password", color = mauNhanChinh) } // ++ THAY ĐỔI
            )

            Spacer(modifier = Modifier.size(20.dp))


            Button(
                onClick = {
                    // Logic đăng ký (Giữ nguyên)
                    val email = username.trim()
                    val pass = password.trim()
                    val confirmPass = confirmPassword.trim()

                    if (email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (pass != confirmPass) {
                        Toast.makeText(context, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (pass.length < 6) {
                        Toast.makeText(context, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Signin.rout) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }

                            } else {
                                Toast.makeText(
                                    context,
                                    "Đăng ký thất bại: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 15.dp,
                    pressedElevation = 6.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mauNhanChinh // ++ THAY ĐỔI
                ),
                border = BorderStroke(0.5.dp, mauNhanChinh.copy(alpha = 0.5f)) // ++ THAY ĐỔI
            ) {
                Text(
                    text = "Sign Up",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White // ++ THÊM: Chữ màu trắng
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    textAlign = TextAlign.Center,
                    color = mauChuPhu // ++ THAY ĐỔI
                )
                TextButton(onClick = {
                    navController.navigate(Screen.Signin.rout)
                }) {
                    Text(
                        text = "Sign In now!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = mauNhanChinh // ++ THAY ĐỔI
                    )
                }
            }

            Spacer(modifier = Modifier.size(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "OR Sign Up with", color = mauChuPhu) // ++ THAY ĐỔI
            }

            Spacer(modifier = Modifier.size(20.dp))

            // Các icon social (Giữ nguyên)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(
                            border = BorderStroke(0.5.dp, Color(0xFFD9D9D9)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.gg),
                        contentDescription = "Logo GG",
                        modifier = Modifier.size(25.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(
                            border = BorderStroke(0.5.dp, Color(0xFFD9D9D9)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.fb),
                        contentDescription = "Logo FB",
                        modifier = Modifier.size(25.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}