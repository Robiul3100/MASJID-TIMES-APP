package com.robiul.mosquetime.feature.admin.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit,
    onBackToPublic: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminAuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val activeMosque by viewModel.activeMosque.collectAsState()
    val configuredMosques by viewModel.configuredMosques.collectAsState()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var showMosquePickerSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AdminLoginUiState.Success) {
            onLoginSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "অ্যাডমিন প্রবেশদ্বার",
                            style = AppTypography.screenTitle,
                            fontSize = 17.sp
                        )
                        Text(
                            text = activeMosque.nameBn,
                            fontSize = 11.sp,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToPublic) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ফিরে যান",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Admin Lock Visual Crest with Glow
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0F3822), DarkSurfaceElevated)
                        )
                    )
                    .border(1.5.dp, PrimaryGreen, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AdminPanelSettings,
                    contentDescription = "অ্যাডমিন প্যানেল",
                    tint = NeonGreenGlow,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "মসজিদ পরিচালনা পরিষদ প্যানেল",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                fontFamily = SolaimanLipiFontFamily
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "নামাজের সময়সূচি, হুজুরের খানা, নোটিশ ও অন্যান্য তথ্য সম্পাদনা ও নিয়ন্ত্রণের জন্য প্রবেশ করুন",
                fontSize = 12.5.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                fontFamily = SolaimanLipiFontFamily,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Active Mosque Selector Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkGreenBorder, RoundedCornerShape(12.dp))
                    .clickable { showMosquePickerSheet = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mosque,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = activeMosque.nameBn,
                                color = TextWhite,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                            Text(
                                text = "ডাটাবেস আইডি: ${activeMosque.id}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryGreen.copy(alpha = 0.15f))
                            .border(0.8.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "পরিবর্তন",
                            color = PrimaryGreen,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Alert Banner
            AnimatedVisibility(visible = uiState is AdminLoginUiState.Error) {
                val errorMsg = (uiState as? AdminLoginUiState.Error)?.message ?: ""
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = RedDigitalDim),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RedDigital.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = RedDigital,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMsg,
                            color = Color(0xFFFF8A80),
                            fontSize = 13.sp,
                            fontFamily = SolaimanLipiFontFamily,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Real Login Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = { Text("অ্যাডমিন ইমেইল", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("rsf.robiul@gmail.com", color = TextMuted.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = PrimaryGreen
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkGreenBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedLabelColor = PrimaryGreen,
                            unfocusedLabelColor = TextMuted
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        label = { Text("গোপন পাসওয়ার্ড", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("••••••••", color = TextMuted.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = PrimaryGreen
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (isPasswordVisible) "পাসওয়ার্ড লুকান" else "পাসওয়ার্ড দেখুন",
                                    tint = TextMuted
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.signIn { onLoginSuccess() }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = DarkGreenBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedLabelColor = PrimaryGreen,
                            unfocusedLabelColor = TextMuted
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Forgot Password Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "পাসওয়ার্ড ভুলে গেছেন?",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontFamily = SolaimanLipiFontFamily,
                            modifier = Modifier
                                .clickable {
                                    resetEmailInput = email
                                    showForgotPasswordDialog = true
                                }
                                .padding(vertical = 2.dp)
                        )
                    }

                    // Login Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.signIn { onLoginSuccess() }
                        },
                        enabled = uiState !is AdminLoginUiState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = TextDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState is AdminLoginUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = TextDark,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Security,
                                    contentDescription = null,
                                    tint = TextDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "নিরাপদ সাইন-ইন করুন",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "শুধুমাত্র অনুমোদিত পরিচালনা পরিষদ ও দায়িত্বপ্রাপ্তদের জন্য সংরক্ষিত",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    fontFamily = SolaimanLipiFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back to public button
            TextButton(
                onClick = onBackToPublic,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "← সাধারণ ব্যবহারকারী হিসেবে অ্যাপে ফিরে যান",
                    color = PrimaryGreen,
                    fontSize = 13.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(
                    text = "পাসওয়ার্ড রিসেট লিংক",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "আপনার অ্যাডমিন ইমেইল দিন। আমরা একটি পাসওয়ার্ড রিসেট লিংক পাঠিয়ে দেব:",
                        color = TextMuted,
                        fontSize = 12.5.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        label = { Text("ইমেইল অ্যাড্রেস", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("rsf.robiul@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendPasswordReset(resetEmailInput) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) showForgotPasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("রিসেট লিংক পাঠান", color = TextDark, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Multi-Mosque Switcher Modal Bottom Sheet
    if (showMosquePickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMosquePickerSheet = false },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "মসজিদ ডাটাবেস নির্বাচন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "যেকোনো মসজিদের আলাদা ডাটাবেস ও অ্যাডমিন প্যানেলে লগইন করুন:",
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(14.dp))

                configuredMosques.forEach { mosque ->
                    val isSelected = mosque.id == activeMosque.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.switchMosque(mosque.id) { _, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    showMosquePickerSheet = false
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF14291D) else DarkSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryGreen else DarkGreenBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mosque.nameBn,
                                    color = if (isSelected) PrimaryGreen else TextWhite,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${mosque.district} • আইডি: ${mosque.id}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
