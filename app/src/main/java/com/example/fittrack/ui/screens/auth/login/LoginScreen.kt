package com.example.fittrack.ui.screens.auth.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalView
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import com.example.fittrack.ui.components.feedback.AppSnackKind
import com.example.fittrack.ui.components.feedback.AppSnackMessage
import com.example.fittrack.ui.components.feedback.AppSnackbarHost
import com.example.fittrack.ui.components.feedback.rememberAppSnackbarState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.ui.theme.FitCtaGlow
import com.example.fittrack.ui.theme.FitCtaGradientEnd
import com.example.fittrack.ui.theme.FitCtaGradientMid
import androidx.compose.ui.res.stringResource
import com.example.fittrack.R
import com.example.fittrack.ui.components.inputs.EmailDomainField
import com.example.fittrack.ui.theme.FitCtaGradientStart
import com.example.fittrack.ui.theme.FitTrackTheme
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.data.security.SecureAuthStorage
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private fun firebaseAuthErrorToMessage(context: Context, e: Exception): String {
    val code = (e as? FirebaseAuthException)?.errorCode

    val messageResId = when (code) {
        "ERROR_INVALID_EMAIL" -> R.string.error_login_invalid_email
        "ERROR_USER_NOT_FOUND" -> R.string.error_login_user_not_found
        "ERROR_WRONG_PASSWORD" -> R.string.error_login_wrong_password
        "ERROR_INVALID_CREDENTIAL" -> R.string.error_login_invalid_credential
        "ERROR_USER_DISABLED" -> R.string.error_login_user_disabled
        "ERROR_TOO_MANY_REQUESTS" -> R.string.error_login_too_many_requests
        "ERROR_NETWORK_REQUEST_FAILED" -> R.string.error_login_network_error
        else -> null
    }

    val fallback = e.message?.takeIf { it.isNotBlank() } ?: R.string.error_login_failed
    val normalized = messageResId?.let(context::getString) ?: fallback

    return (if (!code.isNullOrBlank()) {
        context.getString(R.string.error_login_with_code, normalized, code)
    } else {
        normalized
    }) as String
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun languageTagToFlagEmoji(languageTag: String): String {
    val base = languageTag.lowercase().substringBefore('-').substringBefore('_')
    return when (base) {
        "vi" -> "🇻🇳"
        "en" -> "🇺🇸"
        else -> "🌐"
    }
}

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
@Composable
fun LoginScreen(
    currentLanguageTag: String = "en",
    onToggleLanguage: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    prefillEmail: String = "",
) {
    val scope = rememberCoroutineScope()
    var showTransitionOverlay by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val view = LocalView.current
    val isPreview = LocalInspectionMode.current
    val snackBar = rememberAppSnackbarState()
    val secureAuthStorage = remember(context) { SecureAuthStorage(context) }
    val auth = remember { if (isPreview) null else FirebaseAuth.getInstance() }
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val googleWebClientId = remember(context) {
        val resId = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        if (resId != 0) context.getString(resId) else ""
    }

    Scaffold(
        snackbarHost = { AppSnackbarHost(state = snackBar) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
        var email by remember { mutableStateOf(prefillEmail) }
        var password by remember { mutableStateOf("") }
        var showPassword by remember { mutableStateOf(false) }
        val passwordFocusRequester = remember { FocusRequester() }
        var isLoading by remember { mutableStateOf(false) }
        var snackMessage by remember { mutableStateOf<AppSnackMessage?>(null) }
        var showResendVerify by remember { mutableStateOf(false) }
        var showVerifyResendDialog by remember { mutableStateOf(false) }
        var verifyDialogEmail by remember { mutableStateOf("") }
        var autoLoginAttempted by remember { mutableStateOf(false) }
        var showLongMessageDialog by remember { mutableStateOf(false) }
        var longMessageDialogText by remember { mutableStateOf("") }

        LaunchedEffect(snackMessage) {
            val msg = snackMessage ?: return@LaunchedEffect
            if (msg.text.length > 90) {
                longMessageDialogText = msg.text
                showLongMessageDialog = true
            } else {
                snackBar.show(msg)
            }
            snackMessage = null
        }

        LaunchedEffect(prefillEmail) {
            if (prefillEmail.isNotBlank()) {
                email = prefillEmail
            }
        }

        LaunchedEffect(Unit) {
            if (isPreview || autoLoginAttempted) return@LaunchedEffect
            autoLoginAttempted = true

            val currentUser = auth?.currentUser
            if (currentUser != null) {
                try {
                    currentUser.reload().await()
                    val refreshed = auth.currentUser
                    if (refreshed != null && refreshed.isEmailVerified) {
                        onLoginSuccess()
                        return@LaunchedEffect
                    }
                } catch (_: Exception) {
                    auth.signOut()
                }
            }

            val cred = secureAuthStorage.getCredentials() ?: return@LaunchedEffect
            val (savedEmail, savedPassword) = cred
            email = savedEmail
            password = savedPassword

            isLoading = true
            snackMessage = null
            showResendVerify = false

            try {
                val result = auth?.signInWithEmailAndPassword(savedEmail, savedPassword)?.await()
                val user = result?.user
                if (user == null) {
                    secureAuthStorage.clearCredentials()
                    snackMessage = AppSnackMessage(context.getString(R.string.error_login_failed), AppSnackKind.Error)
                } else if (!user.isEmailVerified) {
                    snackMessage = AppSnackMessage(context.getString(R.string.error_login_email_not_verified), AppSnackKind.Error)
                    showResendVerify = true
                    verifyDialogEmail = savedEmail
                    showVerifyResendDialog = true
                } else {
                    onLoginSuccess()
                }
            } catch (e: Exception) {
                secureAuthStorage.clearCredentials()
                snackMessage = AppSnackMessage(firebaseAuthErrorToMessage(context, e), AppSnackKind.Error)
            } finally {
                isLoading = false
            }
        }

        if (showVerifyResendDialog) {
            AlertDialog(
                onDismissRequest = { showVerifyResendDialog = false },
                title = { Text(text = stringResource(id = R.string.dialog_login_title)) },
                text = {
                    val target = verifyDialogEmail.ifBlank { email.trim() }
                    Text(text = context.getString(R.string.dialog_login_message, target))
                },
                confirmButton = {
                    TextButton(
                        enabled = !isLoading,
                        onClick = {
                            scope.launch {
                                isLoading = true
                                try {
                                    val currentUser = auth?.currentUser
                                    if (currentUser == null) {
                                        snackMessage = AppSnackMessage(context.getString(R.string.error_dialog_login_message), AppSnackKind.Error)
                                    } else {
                                        currentUser.sendEmailVerification().await()
                                        snackMessage = AppSnackMessage(context.getString(R.string.dialog_login_resend_message), AppSnackKind.Success)
                                        showResendVerify = false
                                        showVerifyResendDialog = false
                                    }
                                } catch (e: Exception) {
                                    snackMessage = AppSnackMessage(firebaseAuthErrorToMessage(context, e), AppSnackKind.Error)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                    ) {
                        Text(text = stringResource(id = R.string.dialog_login_confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showVerifyResendDialog = false },
                    ) {
                        Text(text = stringResource(id = R.string.dialog_login_cancel))
                    }
                },
            )
        }

        if (showLongMessageDialog) {
            val scrollState = rememberScrollState()
            AlertDialog(
                onDismissRequest = { showLongMessageDialog = false },
                title = { Text(text = stringResource(id = R.string.dialog_notice_title)) },
                text = {
                    SelectionContainer {
                        Text(
                            text = longMessageDialogText,
                            modifier = Modifier.verticalScroll(scrollState),
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showLongMessageDialog = false },
                    ) {
                        Text(text = stringResource(id = R.string.dialog_notice_close))
                    }
                },
            )
        }

       Column(
           modifier = Modifier
               .fillMaxSize()
               .verticalScroll(rememberScrollState())
               .padding(horizontal = 12.dp),
       ) {
           val focusManager = LocalFocusManager.current
           val colorScheme = MaterialTheme.colorScheme
           val extras = FitTrackExtras.colors

           Row(
               modifier = Modifier
                   .fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               Text(
                   text = stringResource(id = R.string.login_title),
                   fontSize = 28.sp,
                   fontWeight = FontWeight.Bold,
                   color = colorScheme.primary,
               )

               Row(verticalAlignment = Alignment.CenterVertically) {
                   val languageToggleLabel = stringResource(id = R.string.login_toggle_language)
                   IconButton(
                       onClick = {
                           showTransitionOverlay = true
                           scope.launch {
                               onToggleLanguage()
                               delay(350)
                               showTransitionOverlay = false
                           }
                       },
                       modifier = Modifier.semantics { contentDescription = languageToggleLabel },
                   ) {
                       Text(
                           text = languageTagToFlagEmoji(currentLanguageTag),
                           fontSize = 22.sp,
                       )
                   }

//                   IconButton(
//                       onClick = {
//                           showTransitionOverlay = true
//                           scope.launch {
//                               onToggleTheme()
//                               delay(220)
//                               showTransitionOverlay = false
//                           }
//                       }
//                   ) {
//                       Icon(
//                           imageVector = if (currentThemeMode == ThemeMode.DARK) {
//                               Icons.Filled.Brightness7
//                           } else {
//                               Icons.Filled.Brightness4
//                           },
//                           contentDescription = stringResource(id = R.string.login_toggle_theme),
//                           tint = colorScheme.primary,
//                       )
//                   }
               }
           }

           Text(
               text = stringResource(id = R.string.login_subtitle),
               fontSize = 14.sp,
               color = extras.textMuted,
               lineHeight = 24.sp
           )

           Spacer(modifier = Modifier.height(32.dp))

           if (showResendVerify) {
               Text(
                   text = stringResource(id = R.string.error_login_email_not_verified),
                   color = extras.textMuted,
                   fontSize = 13.sp,
                   lineHeight = 18.sp,
               )
               Spacer(Modifier.height(8.dp))
               Text(
                   text = stringResource(id = R.string.login_resend_verification_link),
                   color = colorScheme.primary,
                   fontSize = 13.sp,
                   fontWeight = FontWeight.SemiBold,
                   modifier = Modifier.clickable(enabled = !isLoading) {
                       verifyDialogEmail = email.trim()
                       showVerifyResendDialog = true
                   },
               )
               Spacer(Modifier.height(16.dp))
           }

           EmailDomainField(
               label = stringResource(id = R.string.login_email_label),
               value = email,
               onValueChange = { email = it },
               domainOptions = listOf("@gmail.com", "@yahoo.com", "@outlook.com"),
               placeholderLocalPart = "",
               keyboardActions = KeyboardActions(
                   onNext = { passwordFocusRequester.requestFocus() },
               ),
           )

           Spacer(modifier = Modifier.height(24.dp))

           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               Text(
                   text = stringResource(id = R.string.login_password_label),
                   style = MaterialTheme.typography.labelMedium,
                   fontWeight = FontWeight.SemiBold,
                   color = extras.textMuted,
               )
               Text(
                   text = stringResource(id = R.string.login_forgot_password),
                   style = MaterialTheme.typography.labelMedium,
                   color = colorScheme.primary,
               )
           }

           OutlinedTextField(
               value = password,
               onValueChange = { password = it },
               placeholder = {
                   Text(
                       text = stringResource(id = R.string.login_password_placeholder),
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
           )

           Spacer(modifier = Modifier.height(36.dp))

           val loginButtonShape = RoundedCornerShape(percent = 50)
           val loginGradient = Brush.horizontalGradient(
               colors = listOf(
                   FitCtaGradientStart,
                   FitCtaGradientMid,
                   FitCtaGradientEnd,
               ),
           )
           val glowBlue = FitCtaGlow

           Button(
               onClick = {
                   if (isLoading) return@Button
                   focusManager.clearFocus()
                   snackMessage = null
                   showResendVerify = false
                   val trimmedEmail = email.trim()
                   if (trimmedEmail.isBlank() || password.isBlank()) {
                       snackMessage = AppSnackMessage(context.getString(R.string.error_login_enter_email_password), AppSnackKind.Error)
                       return@Button
                   }
                   scope.launch {
                       isLoading = true
                       try {
                           val result = auth?.signInWithEmailAndPassword(trimmedEmail, password)?.await()
                           val user = result?.user
                           if (user == null) {
                               snackMessage = AppSnackMessage(context.getString(R.string.error_login_failed), AppSnackKind.Error)
                           } else if (!user.isEmailVerified) {
                               snackMessage = AppSnackMessage(context.getString(R.string.error_login_email_not_verified), AppSnackKind.Error)
                               showResendVerify = true
                           } else {
                               secureAuthStorage.saveCredentials(trimmedEmail, password)
                               onLoginSuccess()
                           }
                       } catch (e: Exception) {
                           snackMessage = AppSnackMessage(firebaseAuthErrorToMessage(context, e), AppSnackKind.Error)
                       } finally {
                           isLoading = false
                       }
                   }
               },
               modifier = Modifier
                   .fillMaxWidth()
                   .height(56.dp)
                   .shadow(
                       elevation = 14.dp,
                       shape = loginButtonShape,
                       spotColor = glowBlue.copy(alpha = 0.42f),
                       ambientColor = glowBlue.copy(alpha = 0.26f),
                   )
                   .background(brush = loginGradient, shape = loginButtonShape),
               shape = loginButtonShape,
               colors = ButtonDefaults.buttonColors(
                   containerColor = Color.Transparent,
                   contentColor = Color.White,
               ),
               elevation = ButtonDefaults.buttonElevation(
                   defaultElevation = 0.dp,
                   pressedElevation = 0.dp,
                   focusedElevation = 0.dp,
                   hoveredElevation = 0.dp,
                   disabledElevation = 0.dp,
               ),
           ) {
               Row(
                   verticalAlignment = Alignment.CenterVertically,
                   horizontalArrangement = Arrangement.Center,
               ) {
                   Text(
                       text = if (isLoading) stringResource(id = R.string.login_loading) else stringResource(id = R.string.login_button),
                       fontSize = 17.sp,
                       fontWeight = FontWeight.SemiBold,
                       color = Color.White,
                   )
                   Spacer(Modifier.width(8.dp))
                   Icon(
                       imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                       contentDescription = null,
                       tint = Color.White,
                   )
               }
           }

           Row(
               modifier = Modifier.padding(vertical = 24.dp),
               verticalAlignment = Alignment.CenterVertically
           ) {
               HorizontalDivider(
                   modifier = Modifier.weight(1f),
                   thickness = DividerDefaults.Thickness,
                   color = Color.LightGray
               )
               Text(
                   "OR CONTINUE WITH",
                   modifier = Modifier.padding(horizontal = 16.dp),
                   fontSize = 12.sp,
                   color = Color.Gray
               )
               HorizontalDivider(
                   modifier = Modifier.weight(1f),
                   thickness = DividerDefaults.Thickness,
                   color = Color.LightGray
               )
           }

           Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
               SocialButton(
                   text = "Google",
                   iconText = "G",
                   modifier = Modifier.weight(1f),
                   onClick = {
                       if (isLoading) return@SocialButton
                       if (googleWebClientId.isBlank()) {
                           snackMessage = AppSnackMessage(context.getString(R.string.error_login_missing_web_client_id), AppSnackKind.Error)
                           return@SocialButton
                       }
                       scope.launch {
                           isLoading = true
                           snackMessage = null
                           showResendVerify = false
                           try {
                               val googleIdOption = GetGoogleIdOption.Builder()
                                   .setServerClientId(googleWebClientId)
                                   .setFilterByAuthorizedAccounts(false)
                                   .build()

                               val request = GetCredentialRequest.Builder()
                                   .addCredentialOption(googleIdOption)
                                   .build()

                               val activity = context.findActivity() ?: view.context.findActivity()
                               if (activity == null) {
                                   snackMessage = AppSnackMessage(context.getString(R.string.error_login_google_sign_in_failed), AppSnackKind.Error)
                                   return@launch
                               }

                               val result = credentialManager.getCredential(
                                   context = activity,
                                   request = request,
                               )

                               val credential = result.credential
                               if (credential is CustomCredential &&
                                   credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                               ) {
                                   val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                   val idToken = googleIdTokenCredential.idToken
                                   val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                   val signInResult = auth?.signInWithCredential(firebaseCredential)?.await()

                                   if (signInResult?.user != null) {
                                       secureAuthStorage.clearCredentials()
                                       onLoginSuccess()
                                   } else {
                                       snackMessage = AppSnackMessage(context.getString(R.string.error_login_failed), AppSnackKind.Error)
                                   }
                               } else {
                                   snackMessage = AppSnackMessage(context.getString(R.string.error_login_google_invalid_credential), AppSnackKind.Error)
                               }
                           } catch (e: GetCredentialException) {
                               val msg = e.message?.takeIf { it.isNotBlank() }
                               snackMessage = AppSnackMessage(msg ?: context.getString(R.string.error_login_google_generic), AppSnackKind.Error)
                           } catch (e: Exception) {
                               snackMessage = AppSnackMessage(firebaseAuthErrorToMessage(context, e), AppSnackKind.Error)
                           } finally {
                               isLoading = false
                           }
                       }
                   }
               )

           }

           Spacer(modifier = Modifier.height(24.dp))

           Row(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(vertical = 24.dp),
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.Center,
           ) {
               Text(
                   text = stringResource(id = R.string.login_footer_no_account) + " ",
                   fontSize = 12.sp,
                   color = extras.textMuted,
               )
               Text(
                   text = stringResource(id = R.string.login_footer_sign_up),
                   fontSize = 12.sp,
                   color = colorScheme.primary,
                   fontWeight = FontWeight.SemiBold,
                   modifier = Modifier.clickable { onNavigateToRegister() },
               )
           }
       }

        AnimatedVisibility(
            visible = showTransitionOverlay,
            enter = fadeIn(animationSpec = tween(120)),
            exit = fadeOut(animationSpec = tween(160)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.28f))
            )
        }
        }
    }
}

@Composable
fun SocialButton(
    text: String,
    iconText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val extras = FitTrackExtras.colors
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Text(iconText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(10.dp))
        Text(text, color = extras.textMuted)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenPreview() {
    FitTrackTheme {
        LoginScreen()
    }
}
