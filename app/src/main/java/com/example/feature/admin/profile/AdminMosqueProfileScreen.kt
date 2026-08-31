package com.example.feature.admin.profile

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.auth.AdminUser
import com.example.core.auth.PermissionManager
import com.example.data.model.CommitteeCategory
import com.example.data.model.CommitteeMember
import com.example.data.model.FacilityItem
import com.example.ui.theme.SolaimanLipiFontFamily
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMosqueProfileScreen(
    currentAdmin: AdminUser?,
    onNavigateBack: () -> Unit,
    viewModel: AdminMosqueProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    // Permission Guard
    val hasPermission = currentAdmin?.let { PermissionManager.canManageMosqueProfile(it) } ?: false

    val nameBn by viewModel.nameBn.collectAsState()
    val nameEn by viewModel.nameEn.collectAsState()
    val establishedYear by viewModel.establishedYear.collectAsState()
    val address by viewModel.address.collectAsState()
    val district by viewModel.district.collectAsState()
    val capacity by viewModel.capacity.collectAsState()
    val floors by viewModel.floors.collectAsState()
    val description by viewModel.description.collectAsState()
    val history by viewModel.history.collectAsState()

    val imamName by viewModel.imamName.collectAsState()
    val imamTitle by viewModel.imamTitle.collectAsState()
    val imamEducation by viewModel.imamEducation.collectAsState()
    val imamPhone by viewModel.imamPhone.collectAsState()

    val muazzinName by viewModel.muazzinName.collectAsState()
    val muazzinPhone by viewModel.muazzinPhone.collectAsState()
    val khademName by viewModel.khademName.collectAsState()
    val officePhone by viewModel.officePhone.collectAsState()
    val officeEmail by viewModel.officeEmail.collectAsState()
    val website by viewModel.website.collectAsState()

    val facilities by viewModel.facilities.collectAsState()
    val committee by viewModel.committee.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("সাধারণ তথ্য", "ইমাম ও স্টাফ", "সুবিধাসমূহ", "কমিটি সদস্য")

    var showAddFacilityDialog by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, if (event.isError) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "মসজিদ পরিচিতি সম্পাদনা",
                            fontFamily = SolaimanLipiFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "মসজিদের নাম, স্টাফ, ইতিহাস ও কমিটি তথ্য",
                            fontFamily = SolaimanLipiFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (hasPermission) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.populateForm(viewModel.currentDetails.value, viewModel.committeeMembers.value)
                                Toast.makeText(context, "রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("reset_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("রিসেট", fontFamily = SolaimanLipiFontFamily, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { viewModel.saveAll(currentAdmin) },
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp)
                                .testTag("save_profile_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("সংরক্ষণ করুন", fontFamily = SolaimanLipiFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (!hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "অনুমতি নেই",
                        fontFamily = SolaimanLipiFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "আপনার এই রোল দিয়ে মসজিদের প্রোফাইল পরিবর্তন করার অনুমতি নেই।",
                        fontFamily = SolaimanLipiFontFamily,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontFamily = SolaimanLipiFontFamily,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // সাধারণ তথ্য
                        item {
                            SectionHeader(title = "মসজিদের নাম ও প্রাথমিক পরিচিতি", icon = Icons.Default.AccountBalance)
                        }
                        item {
                            OutlinedTextField(
                                value = nameBn,
                                onValueChange = { viewModel.nameBn.value = it },
                                label = { Text("মসজিদের নাম (বাংলা)*", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_name_bn"),
                                singleLine = true
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = nameEn,
                                onValueChange = { viewModel.nameEn.value = it },
                                label = { Text("মসজিদের নাম (English)", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_name_en"),
                                singleLine = true
                            )
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = establishedYear,
                                    onValueChange = { viewModel.establishedYear.value = it },
                                    label = { Text("স্থাপিত সাল", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_established"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = district,
                                    onValueChange = { viewModel.district.value = it },
                                    label = { Text("জেলা", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_district"),
                                    singleLine = true
                                )
                            }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = capacity,
                                    onValueChange = { viewModel.capacity.value = it },
                                    label = { Text("ধারণক্ষমতা (যেমন: ৩,৫০০ জন)", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_capacity"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = floors,
                                    onValueChange = { viewModel.floors.value = it },
                                    label = { Text("ভবন কাঠামো (যেমন: ৪ তলা)", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_floors"),
                                    singleLine = true
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = address,
                                onValueChange = { viewModel.address.value = it },
                                label = { Text("সম্পূর্ণ ঠিকানা", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_address"),
                                minLines = 2
                            )
                        }
                        item {
                            SectionHeader(title = "বিস্তারিত বিবরণ ও ইতিহাস", icon = Icons.Default.MenuBook)
                        }
                        item {
                            OutlinedTextField(
                                value = description,
                                onValueChange = { viewModel.description.value = it },
                                label = { Text("মসজিদের সাধারণ বিবরণ ও কার্যক্রম", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_description"),
                                minLines = 3
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = history,
                                onValueChange = { viewModel.history.value = it },
                                label = { Text("মসজিদ প্রতিষ্ঠার ইতিহাস ও পটভূমি", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_history"),
                                minLines = 4
                            )
                        }
                    }

                    1 -> {
                        // ইমাম ও স্টাফ
                        item {
                            SectionHeader(title = "খতিব ও প্রধান ইমাম", icon = Icons.Default.Person)
                        }
                        item {
                            OutlinedTextField(
                                value = imamName,
                                onValueChange = { viewModel.imamName.value = it },
                                label = { Text("ইমামের নাম", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_imam_name"),
                                singleLine = true
                            )
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = imamTitle,
                                    onValueChange = { viewModel.imamTitle.value = it },
                                    label = { Text("পদবি", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_imam_title"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = imamPhone,
                                    onValueChange = { viewModel.imamPhone.value = it },
                                    label = { Text("মোবাইল নম্বর", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_imam_phone"),
                                    singleLine = true
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = imamEducation,
                                onValueChange = { viewModel.imamEducation.value = it },
                                label = { Text("শিক্ষাগত যোগ্যতা ও সনদ", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_imam_edu"),
                                singleLine = true
                            )
                        }

                        item {
                            SectionHeader(title = "মুয়াজ্জিন ও খাদেম পরিষদ", icon = Icons.Default.Group)
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = muazzinName,
                                    onValueChange = { viewModel.muazzinName.value = it },
                                    label = { Text("মুয়াজ্জিনের নাম", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1.2f).testTag("input_muazzin_name"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = muazzinPhone,
                                    onValueChange = { viewModel.muazzinPhone.value = it },
                                    label = { Text("মুয়াজ্জিনের ফোন", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_muazzin_phone"),
                                    singleLine = true
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = khademName,
                                onValueChange = { viewModel.khademName.value = it },
                                label = { Text("খাদেমদের নাম ও বিবরণ", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_khadem_name"),
                                singleLine = true
                            )
                        }

                        item {
                            SectionHeader(title = "অফিস ও প্রাতিষ্ঠানিক যোগাযোগ", icon = Icons.Default.Call)
                        }
                        item {
                            OutlinedTextField(
                                value = officePhone,
                                onValueChange = { viewModel.officePhone.value = it },
                                label = { Text("অফিস ফোন নম্বর", fontFamily = SolaimanLipiFontFamily) },
                                modifier = Modifier.fillMaxWidth().testTag("input_office_phone"),
                                singleLine = true
                            )
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = officeEmail,
                                    onValueChange = { viewModel.officeEmail.value = it },
                                    label = { Text("ইমেইল ঠিকানা", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_office_email"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = website,
                                    onValueChange = { viewModel.website.value = it },
                                    label = { Text("ওয়েবসাইট", fontFamily = SolaimanLipiFontFamily) },
                                    modifier = Modifier.weight(1f).testTag("input_website"),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    2 -> {
                        // সুবিধাসমূহ
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SectionHeader(title = "মসজিদের বিশেষ সুবিধাসমূহ", icon = Icons.Default.Star)
                                Button(
                                    onClick = { showAddFacilityDialog = true },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("add_facility_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("সুবিধা যোগ", fontFamily = SolaimanLipiFontFamily, fontSize = 13.sp)
                                }
                            }
                        }

                        if (facilities.isEmpty()) {
                            item {
                                EmptyPlaceholder(text = "কোনো সুবিধা যুক্ত করা হয়নি")
                            }
                        } else {
                            itemsIndexed(facilities) { index, facility ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (facility.iconType) {
                                                    "ac" -> Icons.Default.AcUnit
                                                    "wudu" -> Icons.Default.WaterDrop
                                                    "women" -> Icons.Default.Face
                                                    "library" -> Icons.Default.MenuBook
                                                    "maktab" -> Icons.Default.School
                                                    "ambulance" -> Icons.Default.LocalHospital
                                                    else -> Icons.Default.FlashOn
                                                },
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = facility.title,
                                                fontFamily = SolaimanLipiFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = facility.description,
                                                fontFamily = SolaimanLipiFontFamily,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.removeFacility(index) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "ডিলিট",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // কমিটি সদস্য
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SectionHeader(title = "পরিচালনা কমিটির তালিকা", icon = Icons.Default.Groups)
                                Button(
                                    onClick = { showAddMemberDialog = true },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("add_member_button")
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("সদস্য যোগ", fontFamily = SolaimanLipiFontFamily, fontSize = 13.sp)
                                }
                            }
                        }

                        if (committee.isEmpty()) {
                            item {
                                EmptyPlaceholder(text = "কোনো সদস্যের তালিকা নেই")
                            }
                        } else {
                            itemsIndexed(committee) { _, member ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (member.category) {
                                                        CommitteeCategory.OFFICE_BEARERS -> MaterialTheme.colorScheme.primaryContainer
                                                        CommitteeCategory.ADVISORY -> MaterialTheme.colorScheme.secondaryContainer
                                                        CommitteeCategory.EXECUTIVE -> MaterialTheme.colorScheme.tertiaryContainer
                                                        else -> MaterialTheme.colorScheme.surfaceContainerHighest
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = member.name.take(1),
                                                fontFamily = SolaimanLipiFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = member.name,
                                                    fontFamily = SolaimanLipiFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = member.designationBn,
                                                    fontFamily = SolaimanLipiFontFamily,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                if (member.phone.isNotBlank()) {
                                                    Text(
                                                        text = "• ${member.phone}",
                                                        fontFamily = SolaimanLipiFontFamily,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            if (member.profession.isNotBlank()) {
                                                Text(
                                                    text = member.profession,
                                                    fontFamily = SolaimanLipiFontFamily,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { viewModel.removeCommitteeMember(member.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "মুছুন",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add Facility
    if (showAddFacilityDialog) {
        AddFacilityDialog(
            onDismiss = { showAddFacilityDialog = false },
            onAdd = { title, desc, iconType ->
                viewModel.addFacility(title, desc, iconType)
                showAddFacilityDialog = false
            }
        )
    }

    // Dialog: Add Member
    if (showAddMemberDialog) {
        AddCommitteeMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onAdd = { name, desig, cat, phone, prof, term ->
                viewModel.addCommitteeMember(name, desig, cat, phone, prof, term)
                showAddMemberDialog = false
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            fontFamily = SolaimanLipiFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = SolaimanLipiFontFamily,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AddFacilityDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, description: String, iconType: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("ac") }

    val iconOptions = listOf(
        "ac" to "এসি",
        "wudu" to "ওজু",
        "women" to "মহিলা",
        "library" to "লাইব্রেরি",
        "maktab" to "মক্তব",
        "ambulance" to "এম্বুলেন্স",
        "power" to "বিদ্যুৎ"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন সুবিধা যুক্ত করুন", fontFamily = SolaimanLipiFontFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("সুবিধার শিরোনাম (যেমন: সার্বক্ষণিক লিফট)", fontFamily = SolaimanLipiFontFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("সংক্ষিপ্ত বিবরণ", fontFamily = SolaimanLipiFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Text("আইকন নির্বাচন করুন:", fontFamily = SolaimanLipiFontFamily, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    iconOptions.take(4).forEach { (iconKey, label) ->
                        FilterChip(
                            selected = selectedIcon == iconKey,
                            onClick = { selectedIcon = iconKey },
                            label = { Text(label, fontFamily = SolaimanLipiFontFamily, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onAdd(title.trim(), description.trim(), selectedIcon)
                },
                enabled = title.isNotBlank()
            ) {
                Text("যুক্ত করুন", fontFamily = SolaimanLipiFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", fontFamily = SolaimanLipiFontFamily)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommitteeMemberDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, designation: String, category: CommitteeCategory, phone: String, profession: String, term: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CommitteeCategory.OFFICE_BEARERS) }
    var phone by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var term by remember { mutableStateOf("২০২৪-২০২৬") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন কমিটি সদস্য যুক্ত করুন", fontFamily = SolaimanLipiFontFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("সদস্যের নাম*", fontFamily = SolaimanLipiFontFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { designation = it },
                        label = { Text("পদবি*", fontFamily = SolaimanLipiFontFamily) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("মোবাইল", fontFamily = SolaimanLipiFontFamily) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = profession,
                    onValueChange = { profession = it },
                    label = { Text("পেশা / পরিচিতি", fontFamily = SolaimanLipiFontFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("ক্যাটাগরি:", fontFamily = SolaimanLipiFontFamily, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == CommitteeCategory.OFFICE_BEARERS,
                        onClick = { selectedCategory = CommitteeCategory.OFFICE_BEARERS },
                        label = { Text("কর্মকর্তা", fontFamily = SolaimanLipiFontFamily, fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedCategory == CommitteeCategory.EXECUTIVE,
                        onClick = { selectedCategory = CommitteeCategory.EXECUTIVE },
                        label = { Text("কার্যনির্বাহী", fontFamily = SolaimanLipiFontFamily, fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedCategory == CommitteeCategory.ADVISORY,
                        onClick = { selectedCategory = CommitteeCategory.ADVISORY },
                        label = { Text("উপদেষ্টা", fontFamily = SolaimanLipiFontFamily, fontSize = 11.sp) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && designation.isNotBlank()) {
                        onAdd(name.trim(), designation.trim(), selectedCategory, phone.trim(), profession.trim(), term.trim())
                    }
                },
                enabled = name.isNotBlank() && designation.isNotBlank()
            ) {
                Text("সংরক্ষণ", fontFamily = SolaimanLipiFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", fontFamily = SolaimanLipiFontFamily)
            }
        }
    )
}
