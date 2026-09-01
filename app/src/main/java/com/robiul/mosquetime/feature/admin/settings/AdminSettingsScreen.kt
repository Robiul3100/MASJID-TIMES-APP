package com.robiul.mosquetime.feature.admin.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robiul.mosquetime.core.auth.AdminRole
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.core.auth.PermissionManager
import com.robiul.mosquetime.data.firebase.MosqueConfig
import com.robiul.mosquetime.feature.admin.auth.AdminAuthViewModel
import com.robiul.mosquetime.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminAuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allAdmins by viewModel.allAdmins.collectAsStateWithLifecycle()
    val configuredMosques by viewModel.configuredMosques.collectAsStateWithLifecycle()
    val activeMosque by viewModel.activeMosque.collectAsStateWithLifecycle()

    var showAddAdminDialog by remember { mutableStateOf(false) }
    var showEditAdminDialog by remember { mutableStateOf<AdminUser?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<AdminUser?>(null) }
    var showAddMosqueDialog by remember { mutableStateOf(false) }
    var showPasswordResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "অ্যাডমিন ও রোল সেটিংস",
                            style = AppTypography.screenTitle,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "মুল অ্যাডমিন, মডারেটর ও মাল্টি-মসজিদ কনফিগ",
                            fontSize = 11.sp,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // 1. Super Admin Profile Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF2E2005), Color(0xFF141004))
                                        )
                                    )
                                    .border(1.5.dp, GoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.WorkspacePremium,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser?.nameBn ?: "এইচ এম রবিউল ইসলাম",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GoldAccent.copy(alpha = 0.15f))
                                            .border(0.8.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "সুপার অ্যাডমিন",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = currentUser?.email ?: "rsf.robiul@gmail.com",
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DarkGreenBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "নিরাপত্তা ও পাসওয়ার্ড:",
                                fontSize = 12.sp,
                                color = TextMuted,
                                fontFamily = SolaimanLipiFontFamily
                            )

                            Button(
                                onClick = { showPasswordResetDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Outlined.LockReset, contentDescription = null, modifier = Modifier.size(15.dp), tint = TextDark)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("পাসওয়ার্ড রিসেট", color = TextDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            }
                        }
                    }
                }
            }

            // 2. Multi-Mosque & Database Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🕌 মসজিদ ও ডাটাবেস আইসোলেশন",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        fontFamily = SolaimanLipiFontFamily
                    )

                    TextButton(onClick = { showAddMosqueDialog = true }) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন মসজিদ", color = PrimaryGreen, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "প্রতিটি মসজিদের জন্য আলাদা এডমিন ও ক্লাউড ডাটাবেস সংযুক্ত থাকবে। যেকোনো মসজিদে সুইচ করতে নিচে ক্লিক করুন:",
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            fontFamily = SolaimanLipiFontFamily
                        )

                        configuredMosques.forEach { mosque ->
                            val isCurrent = mosque.id == activeMosque.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) Color(0xFF13271C) else DarkSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isCurrent) PrimaryGreen else DarkGreenBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        if (!isCurrent) {
                                            viewModel.switchMosque(mosque.id) { success, msg ->
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = mosque.nameBn,
                                                color = if (isCurrent) PrimaryGreen else TextWhite,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = SolaimanLipiFontFamily
                                            )
                                            if (isCurrent) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(PrimaryGreen.copy(alpha = 0.2f))
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text("অ্যাক্টিভ", color = PrimaryGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "পাথ: mosques/${mosque.id} • জেলা: ${mosque.district}",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }

                                    if (isCurrent) {
                                        Icon(
                                            imageVector = Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "সুইচ করুন",
                                            color = CyanBlue,
                                            fontSize = 11.sp,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Admin & Moderator Roster Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "👥 অ্যাডমিন ও মডারেটর তালিকা (${allAdmins.size} জন)",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Text(
                            text = "ভূমিকা পরিবর্তন, নতুন নিয়োগ ও অধিকার নিয়ন্ত্রণ",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }

                    Button(
                        onClick = { showAddAdminDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.PersonAdd, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন যোগ করুন", color = TextDark, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                    }
                }
            }

            // 4. Admin List Items
            items(allAdmins) { admin ->
                val isSuper = admin.role == AdminRole.SUPER_ADMIN || admin.email.equals("rsf.robiul@gmail.com", ignoreCase = true)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSuper) GoldAccent.copy(alpha = 0.4f) else DarkGreenBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = admin.nameBn,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Role Badge
                                    val badgeColor = when (admin.role) {
                                        AdminRole.SUPER_ADMIN -> GoldAccent
                                        AdminRole.ADMIN -> PrimaryGreen
                                        AdminRole.IMAM -> CyanBlue
                                        AdminRole.EDITOR -> PurpleAccent
                                        AdminRole.VIEWER -> TextMuted
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeColor.copy(alpha = 0.15f))
                                            .border(0.7.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = admin.role.displayNameBn,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${admin.email} • ${admin.designation}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )

                                if (admin.phone.isNotBlank()) {
                                    Text(
                                        text = "ফোন: ${admin.phone} • মসজিদ: ${admin.mosqueId}",
                                        fontSize = 10.5.sp,
                                        color = TextMuted.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            // Active / Inactive Status Switch
                            Column(horizontalAlignment = Alignment.End) {
                                Switch(
                                    checked = admin.isActive,
                                    onCheckedChange = { active ->
                                        if (!isSuper) {
                                            viewModel.toggleAdminStatus(admin.uid, active) { _, msg ->
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "সুপার অ্যাডমিন নিষ্ক্রিয় করা যাবে না", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = !isSuper,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = PrimaryGreen,
                                        checkedTrackColor = DarkGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = DarkSurfaceElevated
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DarkGreenBorder.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Actions Row (Edit Role, Delete)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { showEditAdminDialog = admin },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanBlue)
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("রোল পরিবর্তন", fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                            }

                            if (!isSuper) {
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { showDeleteConfirmDialog = admin },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RedDigital.copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedDigital)
                                ) {
                                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("মুছে ফেলুন", fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Add New Admin / Moderator Dialog
    // -------------------------------------------------------------
    if (showAddAdminDialog) {
        var nameInput by remember { mutableStateOf("") }
        var emailInput by remember { mutableStateOf("") }
        var designationInput by remember { mutableStateOf("মসজিদ পরিচালনা পরিষদ") }
        var phoneInput by remember { mutableStateOf("") }
        var selectedRole by remember { mutableStateOf(AdminRole.ADMIN) }
        var selectedMosqueId by remember { mutableStateOf(activeMosque.id) }

        AlertDialog(
            onDismissRequest = { showAddAdminDialog = false },
            title = {
                Text(
                    text = "নতুন অ্যাডমিন / মডারেটর যোগ করুন",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("পূর্ণ নাম (বাংলায়)", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("মাওলানা মো. রফিক") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("অ্যাডমিন ইমেইল", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("admin@example.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("মোবাইল নম্বর", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("+880 1700-000000") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = designationInput,
                        onValueChange = { designationInput = it },
                        label = { Text("পদবী", fontFamily = SolaimanLipiFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "ভূমিকা / দায়িত্ব (Role):",
                        fontSize = 12.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(AdminRole.ADMIN, AdminRole.IMAM, AdminRole.EDITOR).forEach { role ->
                            val isSelected = selectedRole == role
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRole = role },
                                label = { Text(role.displayNameBn, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryGreen,
                                    selectedLabelColor = TextDark
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isBlank() || emailInput.isBlank()) {
                            Toast.makeText(context, "নাম ও ইমেইল আবশ্যক", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newAdmin = AdminUser(
                            uid = "admin_${System.currentTimeMillis()}",
                            email = emailInput.trim(),
                            nameBn = nameInput.trim(),
                            designation = designationInput.trim(),
                            role = selectedRole,
                            phone = phoneInput.trim(),
                            mosqueId = selectedMosqueId,
                            isActive = true
                        )
                        viewModel.createOrUpdateAdmin(newAdmin) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) showAddAdminDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("সংরক্ষণ করুন", color = TextDark, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAdminDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // -------------------------------------------------------------
    // Edit Role Dialog
    // -------------------------------------------------------------
    showEditAdminDialog?.let { targetAdmin ->
        var selectedRole by remember { mutableStateOf(targetAdmin.role) }
        var designationInput by remember { mutableStateOf(targetAdmin.designation) }

        AlertDialog(
            onDismissRequest = { showEditAdminDialog = null },
            title = {
                Text(
                    text = "${targetAdmin.nameBn} - রোল পরিবর্তন",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "নতুন ভূমিকা ও দায়িত্ব নির্ধারণ করুন:",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontFamily = SolaimanLipiFontFamily
                    )

                    OutlinedTextField(
                        value = designationInput,
                        onValueChange = { designationInput = it },
                        label = { Text("পদবী", fontFamily = SolaimanLipiFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AdminRole.values().forEach { role ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRole = role }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedRole == role,
                                    onClick = { selectedRole = role },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = role.displayNameBn,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = targetAdmin.copy(role = selectedRole, designation = designationInput)
                        viewModel.createOrUpdateAdmin(updated) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) showEditAdminDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("আপডেট করুন", color = TextDark, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAdminDialog = null }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // -------------------------------------------------------------
    // Delete Confirmation Dialog
    // -------------------------------------------------------------
    showDeleteConfirmDialog?.let { targetAdmin ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = {
                Text(
                    text = "অ্যাডমিন মুছে ফেলা",
                    fontWeight = FontWeight.Bold,
                    color = RedDigital,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিত যে '${targetAdmin.nameBn}' (${targetAdmin.email}) কে অ্যাডমিন প্যানেল থেকে মুছে ফেলতে চান?",
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAdmin(targetAdmin.uid) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) showDeleteConfirmDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("মুছে ফেলুন", color = Color.White, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // -------------------------------------------------------------
    // Add New Mosque Dialog
    // -------------------------------------------------------------
    if (showAddMosqueDialog) {
        var idInput by remember { mutableStateOf("") }
        var nameBnInput by remember { mutableStateOf("") }
        var districtInput by remember { mutableStateOf("ঢাকা") }
        var addressInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMosqueDialog = false },
            title = {
                Text(
                    text = "নতুন মসজিদ ডাটাবেস যুক্ত করুন",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = idInput,
                        onValueChange = { idInput = it },
                        label = { Text("ইউনিক মসজিদ আইডি (English)", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("uttara_sector_11_mosque") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nameBnInput,
                        onValueChange = { nameBnInput = it },
                        label = { Text("মসজিদের নাম (বাংলা)", fontFamily = SolaimanLipiFontFamily) },
                        placeholder = { Text("উত্তরা সেক্টর ১১ জামে মসজিদ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = districtInput,
                        onValueChange = { districtInput = it },
                        label = { Text("জেলা", fontFamily = SolaimanLipiFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("ঠিকানা", fontFamily = SolaimanLipiFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (idInput.isBlank() || nameBnInput.isBlank()) {
                            Toast.makeText(context, "আইডি ও নাম আবশ্যক", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val config = MosqueConfig(
                            id = idInput.trim().lowercase().replace(" ", "_"),
                            nameBn = nameBnInput.trim(),
                            district = districtInput.trim(),
                            address = addressInput.trim()
                        )
                        viewModel.registerNewMosque(config) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) showAddMosqueDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("সংরক্ষণ করুন", color = TextDark, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMosqueDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // -------------------------------------------------------------
    // Password Reset Dialog
    // -------------------------------------------------------------
    if (showPasswordResetDialog) {
        var emailForReset by remember { mutableStateOf(currentUser?.email ?: "rsf.robiul@gmail.com") }

        AlertDialog(
            onDismissRequest = { showPasswordResetDialog = false },
            title = {
                Text(
                    text = "পাসওয়ার্ড রিসেট ইমেইল",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "আমরা আপনার রেজিস্টার্ড ইমেইলে একটি নিরাপদ পাসওয়ার্ড রিসেট লিংক পাঠাব:",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    OutlinedTextField(
                        value = emailForReset,
                        onValueChange = { emailForReset = it },
                        label = { Text("ইমেইল", fontFamily = SolaimanLipiFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendPasswordReset(emailForReset) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) showPasswordResetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("রিসেট লিংক পাঠান", color = TextDark, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordResetDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
