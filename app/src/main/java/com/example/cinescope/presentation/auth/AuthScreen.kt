package com.example.cinescope.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cinescope.data.remote.dto.RegisterRequest
import com.example.cinescope.ui.theme.Crimson

@Composable
fun AuthScreen(
    isSignup: Boolean,
    onBack: () -> Unit,
    onSwitch: () -> Unit,
    onSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onSuccess((uiState as AuthUiState.Success).token)
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Show back button always so user can return to the app
        Icon(
            Icons.Outlined.ArrowBack,
            contentDescription = "Back",
            tint = Crimson,
            modifier = Modifier
                .padding(24.dp)
                .size(24.dp)
                .clickable { onBack() }
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Movie, contentDescription = null, tint = Crimson, modifier = Modifier.size(64.dp))
                Text(if (isSignup) "Welcome to CinePass" else "Welcome Back", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Text(if (isSignup) "Create account" else "Please enter your details to continue", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(36.dp))
            
            if (uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = Crimson,
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (isSignup) AuthField("Username", value = username, onValueChange = { username = it })
                AuthField("Email Address", value = email, onValueChange = { email = it })
                AuthField("Password", true, value = password, onValueChange = { password = it })
                if (isSignup) AuthField("Confirm Password", true, value = confirmPassword, onValueChange = { confirmPassword = it })
            }
            if (isSignup) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
                    Text("I agree to the Terms & Conditions", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text("Forgot Password?", modifier = Modifier.padding(top = 16.dp), color = Crimson, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (uiState is AuthUiState.Loading) Crimson.copy(alpha = 0.5f) else Crimson)
                    .clickable(enabled = uiState !is AuthUiState.Loading) {
                        if (isSignup) {
                            viewModel.register(
                                RegisterRequest(
                                    username = username,
                                    email = email,
                                    password = password,
                                    confirm_password = confirmPassword
                                )
                            )
                        } else {
                            viewModel.login(email, password)
                        }
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isSignup) "Sign Up" else "Login", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("OR CONTINUE WITH", modifier = Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp)).padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text("Google", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(18.dp))
            Row {
                Text(if (isSignup) "Already have an account?" else "Don't have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Text(if (isSignup) "Login" else "Sign Up", color = Crimson, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { 
                    viewModel.resetState()
                    onSwitch() 
                })
            }
        }
    }
}

@Composable
private fun AuthField(
    label: String, 
    password: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color(0xFF94A3B8)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(999.dp),
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
