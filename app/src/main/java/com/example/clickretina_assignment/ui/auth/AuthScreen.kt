package com.example.clickretina_assignment.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clickretina_assignment.R

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var showLogin by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthHeader()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(140.dp)) // Adjust this to position the form correctly

            if (showLogin) {
                LoginContent(
                    onSwitchToSignup = { showLogin = false },
                    viewModel = authViewModel,
                    onLoginSuccess = onLoginSuccess
                )
            } else {
                SignUpContent(
                    onSwitchToLogin = { showLogin = true },
                    viewModel = authViewModel,
                    onSignUpSuccess = onSignUpSuccess
                )
            }
        }
    }
}

@Composable
fun LoginContent(
    onSwitchToSignup: () -> Unit,
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState = viewModel.authState.value

    Text(
        text = "Welcome Back",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(32.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthTextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Login", modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onSwitchToSignup) {
            Text("Don't have an account? Sign up", color = MaterialTheme.colorScheme.onBackground)
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                authState.message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LaunchedEffect(authState) {
            if (authState is AuthState.Authenticated) {
                onLoginSuccess()
            }
        }
    }
}

@Composable
fun SignUpContent(
    onSwitchToLogin: () -> Unit,
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState = viewModel.authState.value

    Text(
        text = "Create Account",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(32.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AuthTextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Button(
                onClick = { viewModel.signUp(email, password) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign Up", modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onSwitchToLogin) {
            Text("Already have an account? Login", color = MaterialTheme.colorScheme.onBackground)
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                authState.message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LaunchedEffect(authState) {
            if (authState is AuthState.Authenticated) {
                onSignUpSuccess()
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email
        ),
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) {
                    R.drawable.visibility
                } else {
                    R.drawable.visible
                }
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painterResource(image), contentDescription = description)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    )
}
