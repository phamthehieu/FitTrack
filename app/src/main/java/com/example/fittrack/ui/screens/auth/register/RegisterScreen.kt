package com.example.fittrack.ui.screens.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.R
import com.example.fittrack.ui.components.feedback.AppSnackbarHost
import com.example.fittrack.ui.components.feedback.rememberAppSnackbarState
import com.example.fittrack.ui.components.inputs.LabeledOutlinedTextField
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.ui.theme.FitTrackTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegistered: (email: String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackBar = rememberAppSnackbarState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }
    var confirmPassword by remember { mutableStateOf("") }
    var showConfirmPassword by remember { mutableStateOf(false) }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { AppSnackbarHost(state = snackBar) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.register_back_to_login),
                        tint = colorScheme.primary,
                    )
                }
                Text(
                    text = stringResource(id = R.string.register_title),
                    modifier = Modifier.weight(1f),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Text(
                text = stringResource(id = R.string.register_subtitle),
                fontSize = 14.sp,
                color = extras.textMuted,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            LabeledOutlinedTextField(
                label = stringResource(id = R.string.register_email_label),
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(id = R.string.register_email_placeholder),
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() },
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.register_password_label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = extras.textSecondary,
            )

           OutlinedTextField(
               value = password,
               onValueChange = { password = it },
               placeholder = {
                   Text(
                       text = stringResource(id = R.string.register_password_placeholder),
                       color = extras.inputPlaceholder,
                   )
               },
               leadingIcon = {
                   Icon(
                       imageVector = Icons.Filled.Lock,
                       contentDescription = null,
                       tint = extras.textSecondary,
                   )
               },
               trailingIcon = {
                   IconButton(onClick = { showPassword = !showPassword }) {
                       Icon(
                           imageVector = if (showPassword) {
                               Icons.Filled.VisibilityOff
                           } else {
                               Icons.Filled.Visibility
                           },
                           contentDescription = if (showPassword) {
                               stringResource(id = R.string.login_toggle_password_hide)
                           } else {
                               stringResource(id = R.string.login_toggle_password_show)
                           },
                           tint = extras.textSecondary,
                       )
                   }
               },
               visualTransformation = if (showPassword) {
                   VisualTransformation.None
               } else {
                   PasswordVisualTransformation()
               },
               shape = RoundedCornerShape(percent = 50),
               colors = OutlinedTextFieldDefaults.colors(
                   focusedTextColor = colorScheme.onSurface,
                   unfocusedTextColor = colorScheme.onSurface,
                   focusedContainerColor = extras.inputBackground,
                   unfocusedContainerColor = extras.inputBackground,
                   cursorColor = colorScheme.primary,
                   focusedBorderColor = Color.Transparent,
                   unfocusedBorderColor = Color.Transparent,
                   disabledBorderColor = Color.Transparent,
                   errorBorderColor = colorScheme.error,
                   focusedLeadingIconColor = extras.textSecondary,
                   unfocusedLeadingIconColor = extras.textSecondary,
                   focusedTrailingIconColor = extras.textSecondary,
                   unfocusedTrailingIconColor = extras.textSecondary,
                   focusedPlaceholderColor = extras.inputPlaceholder,
                   unfocusedPlaceholderColor = extras.inputPlaceholder,
               ),
               modifier = Modifier
                   .focusRequester(passwordFocusRequester)
                   .fillMaxWidth()
                   .padding(top = 8.dp),
               singleLine = true,
               keyboardOptions = KeyboardOptions(
                   keyboardType = KeyboardType.Password,
                   imeAction = ImeAction.Next,
               ),
               keyboardActions = KeyboardActions(
                   onNext = { confirmPasswordFocusRequester.requestFocus() },
               ),
           )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(id= R.string.register_confirm_password_label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = extras.textSecondary,
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text(
                        text = stringResource(id= R.string.register_confirm_password_placeholder),
                        color = extras.inputPlaceholder,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = extras.textSecondary,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            imageVector = if (showConfirmPassword) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = if (showConfirmPassword) {
                                stringResource(id = R.string.login_toggle_password_hide)
                            } else {
                                stringResource(id = R.string.login_toggle_password_show)
                            },
                            tint = extras.textSecondary,
                        )
                    }
                },
                visualTransformation = if (showConfirmPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                shape = RoundedCornerShape(percent = 50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface,
                    focusedContainerColor = extras.inputBackground,
                    unfocusedContainerColor = extras.inputBackground,
                    cursorColor = colorScheme.primary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = colorScheme.error,
                    focusedLeadingIconColor = extras.textSecondary,
                    unfocusedLeadingIconColor = extras.textSecondary,
                    focusedTrailingIconColor = extras.textSecondary,
                    unfocusedTrailingIconColor = extras.textSecondary,
                    focusedPlaceholderColor = extras.inputPlaceholder,
                    unfocusedPlaceholderColor = extras.inputPlaceholder,
                ),
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true,
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isLoading) return@Button
                    focusManager.clearFocus()

                    val trimmedEmail = email.trim()
                    if (trimmedEmail.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        scope.launch {
                            snackBar.showError(context.getString(R.string.error_register_enter_email_password))
                        }
                        return@Button
                    }
                    if (password.length < 6) {
                        scope.launch {
                            snackBar.showError(context.getString(R.string.error_register_password_too_short))
                        }
                        return@Button
                    }
                    if (password != confirmPassword) {
                        scope.launch {
                            snackBar.showError(context.getString(R.string.error_register_password_mismatch))
                        }
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        try {
                            val auth = FirebaseAuth.getInstance()
                            val result = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
                            result.user?.sendEmailVerification()?.await()
                            snackBar.showSuccess(context.getString(R.string.register_verification_email_sent))
                            auth.signOut()
                            onRegistered(trimmedEmail)
                        } catch (e: Exception) {
                            snackBar.showError(e.message ?: context.getString(R.string.register_failed))
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = Color.White,
                ),
                enabled = !isLoading,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                            text = if (isLoading) stringResource(id = R.string.register_loading) else stringResource(id = R.string.register_button),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
    }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RegisterScreenPreview() {
    FitTrackTheme {
        RegisterScreen()
    }
}
