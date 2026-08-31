package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.data.model.CommitteeCategory
import com.robiul.mosquetime.data.model.CommitteeMember
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

@Composable
fun CommitteeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    var selectedCategory by remember { mutableStateOf(CommitteeCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var activeMemberForDialog by remember { mutableStateOf<CommitteeMember?>(null) }

    val allMembers by MosqueRepository.committeeFlow.collectAsState()

    val filteredMembers = remember(selectedCategory, searchQuery, allMembers) {
        allMembers.filter { member ->
            val matchCategory = selectedCategory == CommitteeCategory.ALL || member.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    member.name.contains(searchQuery, ignoreCase = true) ||
                    member.designationBn.contains(searchQuery, ignoreCase = true) ||
                    member.profession.contains(searchQuery, ignoreCase = true) ||
                    member.phone.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    // Top Executive Leaders (President, General Secretary)
    val president = remember(allMembers) {
        allMembers.firstOrNull { it.designationBn.contains("সভাপতি") && !it.designationBn.contains("সহ") }
    }
    val generalSecretary = remember(allMembers) {
        allMembers.firstOrNull { it.designationBn.contains("সাধারণ সম্পাদক") && !it.designationBn.contains("যুগ্ম") }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("committee_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "মসজিদ পরিচালনা পরিষদ",
                subtitle = "কার্যনির্বাহী ও উপদেষ্টা পরিষদ (২০২৪-২০২৬)",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Filter Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("committee_search_input"),
                    placeholder = {
                        Text(
                            text = "সদস্যের নাম, পদবি বা মোবাইল নম্বর খুঁজুন...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills with Count Badges
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CommitteeCategory.entries.toTypedArray()) { cat ->
                        val isSelected = selectedCategory == cat
                        val count = if (cat == CommitteeCategory.ALL) {
                            allMembers.size
                        } else {
                            allMembers.count { it.category == cat }
                        }

                        val bg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                        val border = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                        val textColor = if (isSelected) PrimaryGreen else TextMuted

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .border(1.dp, border, RoundedCornerShape(16.dp))
                                .clickable {
                                    HapticUtils.performLongPressHaptic(view)
                                    selectedCategory = cat
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = cat.titleBn,
                                    color = textColor,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) PrimaryGreen else DarkSurfaceBorder)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        color = if (isSelected) DarkBackground else TextWhite,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Members Content List
            if (filteredMembers.isEmpty()) {
                AppEmptyStateView(
                    icon = Icons.Outlined.PeopleOutline,
                    title = "কোনো সদস্যের তথ্য পাওয়া যায়নি",
                    subtitle = if (searchQuery.isNotEmpty()) "'$searchQuery'-এর সাথে মিলে এমন কোনো সদস্য নেই" else "এই ক্যাটাগরিতে বর্তমানে কোনো সদস্যের তথ্য নেই",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    // Leadership Hero Showcase (Visible when Search is empty and viewing ALL or OFFICE_BEARERS)
                    if (searchQuery.isBlank() && (selectedCategory == CommitteeCategory.ALL || selectedCategory == CommitteeCategory.OFFICE_BEARERS)) {
                        if (president != null || generalSecretary != null) {
                            item {
                                LeadershipHeroShowcase(
                                    president = president,
                                    generalSecretary = generalSecretary,
                                    onMemberClick = { member ->
                                        HapticUtils.performLongPressHaptic(view)
                                        activeMemberForDialog = member
                                    },
                                    onCall = { phone -> makePhoneCall(context, phone) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Groups, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "পরিচালনা কমিটির সম্মানিত সদস্যবৃন্দ",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }

                    // Members List
                    items(filteredMembers, key = { it.id }) { member ->
                        EnhancedCommitteeMemberCard(
                            member = member,
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                activeMemberForDialog = member
                            },
                            onCallClick = {
                                makePhoneCall(context, member.phone)
                            },
                            onSmsClick = {
                                sendSms(context, member.phone)
                            },
                            onCopyPhone = {
                                copyToClipboard(context, member.phone, "${member.name}-এর নম্বর কপি করা হয়েছে")
                            },
                            onShareContact = {
                                shareMemberContact(context, member)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Member Detail Dialog
    activeMemberForDialog?.let { member ->
        MemberProfileDialog(
            member = member,
            onDismiss = { activeMemberForDialog = null },
            onCall = { makePhoneCall(context, member.phone) },
            onSms = { sendSms(context, member.phone) },
            onCopyPhone = { copyToClipboard(context, member.phone, "${member.name}-এর নম্বর কপি করা হয়েছে") },
            onShare = { shareMemberContact(context, member) }
        )
    }
}

/**
 * Top Leadership Showcase (President & General Secretary)
 */
@Composable
private fun LeadershipHeroShowcase(
    president: CommitteeMember?,
    generalSecretary: CommitteeMember?,
    onMemberClick: (CommitteeMember) -> Unit,
    onCall: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1B3828), DarkSurface)
                )
            )
            .border(1.2.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GoldAccent.copy(alpha = 0.15f))
                    .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "শীর্ষ নেতৃত্ব ও কর্মকর্তা",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "মেয়াদ: ২০২৪-২০২৬",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // President Card
            president?.let { leader ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground.copy(alpha = 0.7f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onMemberClick(leader) }
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .border(1.dp, GoldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = leader.designationBn,
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = leader.name,
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        IconButton(
                            onClick = { onCall(leader.phone) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = CyanBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // General Secretary Card
            generalSecretary?.let { leader ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground.copy(alpha = 0.7f))
                        .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onMemberClick(leader) }
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.15f))
                                .border(1.dp, PrimaryGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = leader.designationBn,
                            color = PrimaryGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = leader.name,
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        IconButton(
                            onClick = { onCall(leader.phone) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = CyanBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Enhanced Committee Member Card with action buttons
 */
@Composable
private fun EnhancedCommitteeMemberCard(
    member: CommitteeMember,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onSmsClick: () -> Unit,
    onCopyPhone: () -> Unit,
    onShareContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    val designationColor = when (member.category) {
        CommitteeCategory.OFFICE_BEARERS -> GoldAccent
        CommitteeCategory.ADVISORY -> CyanBlue
        CommitteeCategory.EXECUTIVE -> PrimaryGreen
        else -> TextWhite
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("committee_member_${member.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Islamic Badge Style
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (member.category == CommitteeCategory.OFFICE_BEARERS) GoldAccent.copy(alpha = 0.15f) else DarkGreen.copy(alpha = 0.5f))
                        .border(1.dp, if (member.category == CommitteeCategory.OFFICE_BEARERS) GoldAccent else PrimaryGreen.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (member.category == CommitteeCategory.OFFICE_BEARERS) GoldAccent else PrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        color = TextWhite,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.designationBn,
                            color = designationColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${member.termYears})",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }

                    if (member.profession.isNotBlank()) {
                        Text(
                            text = member.profession,
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phone Number with Copy Icon
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onCopyPhone() }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.PhoneAndroid, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = member.phone,
                        color = CyanBlue,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(12.dp))
                }

                // Action Icons (Call, SMS, Share)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCallClick, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = NeonGreenGlow, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onSmsClick, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Outlined.Email, contentDescription = "SMS", tint = CyanBlue, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onShareContact, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = TextMuted, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

/**
 * Member Profile Modal Dialog
 */
@Composable
private fun MemberProfileDialog(
    member: CommitteeMember,
    onDismiss: () -> Unit,
    onCall: () -> Unit,
    onSms: () -> Unit,
    onCopyPhone: () -> Unit,
    onShare: () -> Unit
) {
    val designationColor = when (member.category) {
        CommitteeCategory.OFFICE_BEARERS -> GoldAccent
        CommitteeCategory.ADVISORY -> CyanBlue
        CommitteeCategory.EXECUTIVE -> PrimaryGreen
        else -> TextWhite
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.2.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(20.dp))
                    }
                }

                // Avatar Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(designationColor.copy(alpha = 0.15f))
                        .border(1.5.dp, designationColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = designationColor, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = member.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = member.designationBn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = designationColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Detail Info Rows
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailInfoRow(
                        label = "পরিষদ ও ক্যাটাগরি",
                        value = member.category.titleBn,
                        icon = Icons.Outlined.Category
                    )

                    DetailInfoRow(
                        label = "পেশাগত পরিচিতি",
                        value = if (member.profession.isNotBlank()) member.profession else "সমাজসেবক",
                        icon = Icons.Outlined.WorkOutline
                    )

                    DetailInfoRow(
                        label = "কার্যকাল / মেয়াদ",
                        value = member.termYears,
                        icon = Icons.Outlined.CalendarToday
                    )

                    DetailInfoRow(
                        label = "মোবাইল নম্বর",
                        value = member.phone,
                        icon = Icons.Outlined.Phone
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons (Call, SMS, Share)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCall,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কল করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onSms,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.Email, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("বার্তা পাঠান", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("সদস্যের তথ্য শেয়ার করুন", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, color = TextMuted, fontSize = 10.5.sp)
            Text(text = value, color = TextWhite, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
        }
    }
}

private fun makePhoneCall(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "কল করা সম্ভব হচ্ছে না", Toast.LENGTH_SHORT).show()
    }
}

private fun sendSms(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
            putExtra("sms_body", "আসসালামু আলাইকুম, চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ প্রসঙ্গে...")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "মেসেজ পাঠানো সম্ভব হচ্ছে না", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(context: Context, text: String, toastMessage: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Member Contact", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}

private fun shareMemberContact(context: Context, member: CommitteeMember) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "🕌 *মসজিদ পরিচালনা পরিষদ সদস্য*\n\n👤 *নাম:* ${member.name}\n📌 *পদবি:* ${member.designationBn}\n🏛️ *পরিষদ:* ${member.category.titleBn}\n💼 *পেশা:* ${member.profession}\n📱 *মোবাইল:* ${member.phone}\n⏳ *মেয়াদ:* ${member.termYears}\n\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ ও ইসলামিক সেন্টার"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "সদস্যের তথ্য শেয়ার করুন")
    context.startActivity(shareIntent)
}
