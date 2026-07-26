package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Campaign
import com.example.model.Investment
import com.example.model.Proposal
import com.example.model.ShariaWallet
import com.example.model.UmkmTransaction
import com.example.ui.theme.JagoGold
import com.example.ui.theme.JagoPurple
import com.example.ui.theme.JagoTeal
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.GoldenYellow
import com.example.viewmodel.JagoViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// SECTION 1: COMMON REUSABLE COMPONENTS
@Composable
fun SectionHeader(title: String, actionText: String? = null, onActionClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                fontSize = 14.sp,
                color = JagoTeal,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onActionClick)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun AppHeader(
    title: String = "Jago Modal",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    trailingActions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = JagoPurple, // Branding Header Purple
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp), // Curvy High Density bottom shape
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("app_header_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
            } else {
                // High density styled logo block
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(JagoTeal, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JM",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        letterSpacing = (-0.5).sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f),
                letterSpacing = (-0.2).sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                trailingActions()
            }
        }
    }
}

// SECTION 2: PAGE 1 - BERANDA JAGO MODAL
@Composable
fun BerandaScreen(viewModel: JagoViewModel) {
    val campaigns by viewModel.campaigns.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val investments by viewModel.investments.collectAsState()

    // Calculate dynamic values for the portfolio summary card
    val totalActiveInvestment = investments.sumOf { it.capital }
    val totalPayoutReceived = investments.sumOf { it.totalPayoutsReceived }

    val filteredCampaigns = remember(campaigns, filterCategory) {
        when (filterCategory) {
            "Semua" -> campaigns
            "Musyarakah" -> campaigns.filter { it.type == "Musyarakah" }
            "Mudharabah" -> campaigns.filter { it.type == "Mudharabah" }
            "Sektor Kuliner" -> campaigns.filter { it.sector == "Sektor Kuliner" }
            "Sektor Fashion" -> campaigns.filter { it.sector == "Sektor Fashion" }
            else -> campaigns
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        // Portfolio Summary Card (Teal colored) at the top first, with slightly rounded/padded setup, directly flowing from under custom round AppHeader
        item {
            Card(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillMaxWidth()
                    .testTag("portfolio_summary_card"),
                shape = RoundedCornerShape(20.dp), // high-density sharp visual
                colors = CardDefaults.cardColors(containerColor = JagoTeal),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL INVESTASI AKTIF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.2.sp
                        )
                        // Jago inline mini badge
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "JAGO SYARIAH",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = viewModel.formatCurrency(totalActiveInvestment),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+ ${viewModel.formatCurrency(totalPayoutReceived)} (Bagi Hasil)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldenYellow
                            )
                        }
                        
                        // Custom caret icon container matching active:bg-white/20 from HTML
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.TrendingUp,
                                contentDescription = "Growth",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Compact Hero Banner inside the scroll, as styled in the High Density HTML layout
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // slate-200 fine border
                shape = RoundedCornerShape(16.dp), // rounded-2xl
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bagi hasil nyata, dampak nyata.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B), // slate-800
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Investasi Berkah bersama UMKM Pilihan",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B) // slate-500
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(JagoTeal.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalance,
                            contentDescription = "Growth Sharia",
                            tint = JagoTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Horizontal filter bar
        item {
            val categories = listOf("Semua", "Musyarakah", "Mudharabah", "Sektor Kuliner", "Sektor Fashion")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == filterCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilterCategory(category) },
                        label = { Text(text = category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JagoTeal,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF555555)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFFDDDDDD),
                            selectedBorderColor = JagoTeal,
                            borderWidth = 1.dp
                        ),
                        modifier = Modifier.testTag("filter_chip_$category")
                    )
                }
            }
        }

        // Section header
        item {
            SectionHeader(
                title = "Kampanye Crowdfunding Aktif",
                actionText = "Urutkan: Tenggat",
                onActionClick = {}
            )
        }

        // Campaign vertical List
        if (filteredCampaigns.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inbox,
                        contentDescription = "Empty",
                        tint = Color.LightGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tidak ada kampanye aktif di kategori ini", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            items(filteredCampaigns) { campaign ->
                CampaignCard(campaign = campaign, onClick = { viewModel.selectCampaign(campaign) })
            }
        }

        // Little spacer for footer
        item {
            Spacer(modifier = Modifier.height(86.dp))
        }
    }
}

@Composable
fun CampaignCard(campaign: Campaign, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("campaign_card_${campaign.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // slate-200 fine border
        shape = RoundedCornerShape(24.dp), // high density high-rounded shape (rounded-3xl)
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val colorBlend = Color(0xFFF1F5F9) // neutral slate backdrop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp) // h-28 is 112dp
                    .background(colorBlend)
                    .padding(12.dp)
            ) {
                // Top badges: Akad (Left) and Timer (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = JagoGold,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = campaign.type.uppercase(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${campaign.countdownDays} Hari Lagi",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Sector Tag in image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(JagoTeal.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (campaign.sector == "Sektor Kuliner") Icons.Filled.Restaurant else Icons.Filled.Checkroom,
                            contentDescription = null,
                            tint = JagoTeal,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = campaign.sector.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = JagoTeal
                        )
                    }
                }
            }

            // High Density Info Section
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = campaign.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A) // slate-900
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (campaign.sector == "Sektor Kuliner") "F&B • Jakarta" else "Fashion • Bandung",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B) // slate-500
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "NISBAH",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8) // slate-400
                        )
                        Text(
                            text = "${(campaign.nisbahInvestor * 100).roundToInt()}% p.a",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JagoTeal // Jago Teal (#006B6B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar block matching HTML
                val percentageStr = "${(campaign.progress * 100).roundToInt()}% Terkumpul"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = percentageStr,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569) // slate-600
                    )
                    Text(
                        text = "${formatIdrSimple(campaign.collectedAmount)} / ${formatIdrSimple(campaign.targetAmount)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8), // slate-400 font-mono
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { campaign.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .testTag("progress_bar_${campaign.id}"),
                    color = JagoTeal,
                    trackColor = Color(0xFFF1F5F9) // slate-100
                )
            }
        }
    }
}

// Simple compact currency helper
fun formatIdrSimple(amount: Double): String {
    return if (amount >= 1000000000.0) {
        "Rp${(amount / 1000000000.0).toString().take(4)}M"
    } else if (amount >= 1000000.0) {
        "Rp${(amount / 1000000.0).roundToInt()}Jt"
    } else {
        "Rp${amount.roundToInt()}"
    }
}


// SECTION 3: PAGE 2 - DETAIL KAMPANYE & SIMULASI BAGI HASIL
@Composable
fun DetailScreen(viewModel: JagoViewModel, onBack: () -> Unit) {
    val campaignState by viewModel.selectedCampaign.collectAsState()
    val simulatedAmount by viewModel.simulatedAmount.collectAsState()

    val campaign = campaignState ?: return

    val estMonthlyReturn = viewModel.calculateMonthlyEstimate(simulatedAmount, campaign.nisbahInvestor, campaign.tenorMonths)
    val estTotalReturn = viewModel.calculateTotalExpectedReturn(simulatedAmount, campaign.nisbahInvestor, campaign.tenorMonths)

    Scaffold(
        topBar = {
            AppHeader(
                title = "Detail Kampanye",
                showBackButton = true,
                onBackClick = onBack,
                trailingActions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = "Share", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            // Sticky CTA button at bottom
            Surface(
                tonalElevation = 8.dp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = { viewModel.startCheckout() },
                    colors = ButtonDefaults.buttonColors(containerColor = JagoTeal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp)
                        .testTag("invest_now_button")
                ) {
                    Text(text = "Investasi Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FB))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Main visual/image display card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(JagoPurple.copy(alpha = 0.2f), JagoTeal.copy(alpha = 0.3f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Large styled central graphic simulating business store photo
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (campaign.sector == "Sektor Kuliner") Icons.Filled.Restaurant else Icons.Filled.Checkroom,
                        contentDescription = "Main Pic",
                        tint = JagoTeal,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "GALERI FOTO PROYEK UMKM",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Lokasi dan Operasional Lapangan",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Simulated indicators overlay for carousels
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(width = if (index == 0) 18.dp else 6.dp, height = 6.dp)
                                .background(
                                    color = if (index == 0) JagoTeal else Color.LightGray,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Status Badge overlay
                Surface(
                    color = JagoTeal,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Aktif • ${campaign.countdownDays} Hari Lagi",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Body Context
            Column(modifier = Modifier.padding(16.dp)) {
                // Title and badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = campaign.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E1E)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sektor: ${campaign.sector}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Akad Card Gold
                    Surface(
                        color = JagoGold,
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            text = campaign.type,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dana Terkumpul", fontSize = 12.sp, color = Color.Gray)
                            Text("Target Pendanaan", fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = viewModel.formatCurrency(campaign.collectedAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = JagoTeal
                            )
                            Text(
                                text = viewModel.formatCurrency(campaign.targetAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom animated bar
                        LinearProgressIndicator(
                            progress = { campaign.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = JagoTeal,
                            trackColor = Color(0xFFE5EDE9)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progres: ${(campaign.progress * 100).roundToInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = JagoTeal
                            )
                            Text(
                                text = "${campaign.backersCount} Investor",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "Tenor: ${campaign.tenorMonths} Bulan",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sharia Nisbah Visualizer
                Text(
                    text = "Visualisasi Nisbah Syariah (Kemitraan)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(12.dp).background(JagoTeal, CircleShape))
                                Text("Nisbah Investor", fontSize = 11.sp, color = Color.Gray)
                                Text("${(campaign.nisbahInvestor * 100).roundToInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = JagoTeal)
                            }
                            // Custom Canvas representation of ratio pie donut
                            Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.size(70.dp)) {
                                    val investorSweep = campaign.nisbahInvestor.toFloat() * 360f
                                    val umkmSweep = 360f - investorSweep
                                    drawArc(
                                        color = JagoTeal,
                                        startAngle = -90f,
                                        sweepAngle = investorSweep,
                                        useCenter = false,
                                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = JagoGold,
                                        startAngle = -90f + investorSweep,
                                        sweepAngle = umkmSweep,
                                        useCenter = false,
                                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                                    )
                                }
                                Text("AKAD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(12.dp).background(JagoGold, CircleShape))
                                Text("Nisbah UMKM", fontSize = 11.sp, color = Color.Gray)
                                Text("${(campaign.nisbahUmkm * 100).roundToInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = JagoGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFFFECEC))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Sistem Bagi Hasil ${campaign.type}: Setiap keuntungan usaha yang dilaporkan bulanan akan dibagikan sesuai nisbah rasio di atas, amanah & diawasi Dewan Pengawas Syariah.",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            lineHeight = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Simulation Section
                Text(
                    text = "Simulasi Investasi & Profit Sharing",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F1FC)), // Soft light violet background
                    border = BorderStroke(1.dp, JagoPurple.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Jumlah Investasi", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = JagoPurple)
                            Text(
                                text = viewModel.formatCurrency(simulatedAmount),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = JagoPurple,
                                modifier = Modifier.testTag("simulation_amount_label")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom slider: step size 50.000, value in range [100k - 5M]
                        Slider(
                            value = simulatedAmount.toFloat(),
                            onValueChange = { newValue ->
                                val snapped = (newValue / 50000).roundToInt() * 50000.0
                                viewModel.updateSimulatedAmount(snapped)
                            },
                            valueRange = 100000f..5000000f,
                            colors = SliderDefaults.colors(
                                thumbColor = JagoPurple,
                                activeTrackColor = JagoPurple,
                                inactiveTrackColor = Color.LightGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("investment_multiplier_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min: Rp100.000", fontSize = 10.sp, color = Color.Gray)
                            Text("Kelipatan: Rp50.000", fontSize = 10.sp, color = Color.Gray)
                            Text("Max: Rp5.000.000", fontSize = 10.sp, color = Color.Gray)
                        }

                        // Hot Buttons for easy evaluation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(500000.0, 1000000.0, 2500000.0).forEach { value ->
                                Button(
                                    onClick = { viewModel.updateSimulatedAmount(value) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (simulatedAmount == value) JagoPurple else Color.White
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, JagoPurple.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = viewModel.formatCurrency(value).replace("Rp", ""),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (simulatedAmount == value) Color.White else JagoPurple
                                    )
                                }
                            }
                        }

                        Divider(color = JagoPurple.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 10.dp))

                        // Output state estimation values
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Est. Bagi Hasil /Bulan", fontSize = 11.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Filled.Info, "Info", tint = Color.Gray, modifier = Modifier.size(10.dp))
                                }
                                Text(
                                    text = viewModel.formatCurrency(estMonthlyReturn),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JagoTeal,
                                    modifier = Modifier.testTag("est_monthly_result")
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Estimasi Pengembalian", fontSize = 11.sp, color = Color.DarkGray)
                                Text(
                                    text = viewModel.formatCurrency(estTotalReturn),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JagoPurple,
                                    modifier = Modifier.testTag("est_total_result")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "*Estimasi bagi hasil adalah representatif dari profit-sharing berdasarkan tingkat expected productivity rata-rata 15% per tahun dari nominal dana.",
                            fontSize = 9.sp,
                            color = Color.Gray,
                            lineHeight = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Documents section
                Text(
                    text = "Legalitas & Prospektus Kampanye",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(8.dp))

                DocumentListSection()

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DocumentListSection() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Laporan Keuangan.pdf", "Prospektus.pdf").forEach { doc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Mendownload $doc...")
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = "PDF Doc",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = doc, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
                            Text(text = "Telah Terverifikasi OJK & DPS", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Unduh",
                        tint = JagoTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}


// SECTION 4: PAGE 3 - INVESTASI MULTI-STEP & AKAD DIGITAL
@Composable
fun CheckoutScreen(viewModel: JagoViewModel) {
    val campaign by viewModel.selectedCampaign.collectAsState()
    val stepNum by viewModel.checkoutStepNum.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val selectedWallet by viewModel.selectedWallet.collectAsState()
    val simulatedAmount by viewModel.simulatedAmount.collectAsState()

    val errorMsg by viewModel.checkoutError.collectAsState()

    val campaignVal = campaign ?: return

    Scaffold(
        topBar = {
            AppHeader(
                title = "Checkout Investasi",
                showBackButton = true,
                onBackClick = { viewModel.cancelCheckout() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FB))
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Step Indicator Top Row
                StepTrackerProgressBar(currentStep = stepNum)

                // Error banner dynamic if exists
                if (errorMsg != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDE8E8)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("error_banner"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFF8B4B4))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, "Warning", tint = AlertRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = errorMsg ?: "", color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Page sub-screen conditional depending on Step configuration state
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (stepNum) {
                        1 -> Step1NominalContent(viewModel, simulatedAmount, wallets, selectedWallet, campaignVal)
                        2 -> Step2AkadContent(viewModel)
                        3 -> Step3ConfirmationContent(viewModel, simulatedAmount, selectedWallet, campaignVal)
                        4 -> SuccessScreenContent(viewModel, simulatedAmount, campaignVal)
                    }
                }

                // Standard Step Navigation buttons for multi-step
                if (stepNum <= 3) {
                    Surface(
                        tonalElevation = 8.dp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .navigationBarsPadding(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (stepNum > 1) {
                                OutlinedButton(
                                    onClick = { viewModel.prevCheckoutStep() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("checkout_back_btn"),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Sebelumnya")
                                }
                            }

                            val isAgreedToAkad by viewModel.isAgreedToAkad.collectAsState()
                            val isContractScrolledBottom by viewModel.isContractScrolledBottom.collectAsState()

                            // Disable "Lanjut" in step 2 if contract scrolled bottom and checkbox agreed are not completed
                            val isLanjutEnabled = when (stepNum) {
                                1 -> true
                                2 -> isAgreedToAkad && isContractScrolledBottom
                                else -> false // Step 3 has keypad confirmation logic, no regular Lanjut button
                            }

                            if (stepNum < 3) {
                                Button(
                                    onClick = { viewModel.nextCheckoutStep() },
                                    enabled = isLanjutEnabled,
                                    colors = ButtonDefaults.buttonColors(containerColor = JagoTeal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(48.dp)
                                        .testTag("checkout_next_btn")
                                ) {
                                    Text("Lanjut", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepTrackerProgressBar(currentStep: Int) {
    val steps = listOf("1. Input", "2. Akad Digital", "3. Konfirmasi")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val stepIndex = index + 1
            val isActive = stepIndex == currentStep
            val isCompleted = stepIndex < currentStep

            val textColor = if (isActive) JagoTeal else if (isCompleted) Color.Gray else Color.LightGray
            val weight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = if (isActive || isCompleted) JagoTeal else Color.LightGray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Filled.Check, "Done", tint = Color.White, modifier = Modifier.size(12.dp))
                    } else {
                        Text(stepIndex.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, fontSize = 11.sp, color = textColor, fontWeight = weight)
            }

            if (index < steps.size - 1) {
                Icon(Icons.Filled.KeyboardArrowRight, "Arrow", tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun Step1NominalContent(
    viewModel: JagoViewModel,
    amount: Double,
    wallets: List<ShariaWallet>,
    selectedWallet: ShariaWallet,
    campaign: Campaign
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Kampanye Tujuan", fontSize = 11.sp, color = Color.Gray)
                Text(campaign.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = JagoPurple)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = JagoGold, shape = RoundedCornerShape(4.dp)) {
                        Text(campaign.type, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tenor • ${campaign.tenorMonths} Bulan", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Wallet Dropdown Selector Jago Syariah
        Text("Pilih Sumber Dana Jago Syariah", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
        Spacer(modifier = Modifier.height(6.dp))

        var isDropdownExpanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDropdownExpanded = true }
                    .testTag("wallet_source_selector"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, JagoTeal.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AccountBalanceWallet, "Wallet", tint = JagoTeal, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(selectedWallet.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
                            Text("No. Rek: ${selectedWallet.accountNumber}", fontSize = 11.sp, color = Color.Gray)
                            Text("Saldo: " + viewModel.formatCurrency(selectedWallet.balance), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JagoTeal)
                        }
                    }
                    Icon(Icons.Filled.ArrowDropDown, "Chevron", tint = JagoTeal)
                }
            }

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                wallets.forEach { wallet ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(wallet.name, fontWeight = FontWeight.Bold)
                                Text("Saldo: ${viewModel.formatCurrency(wallet.balance)}")
                            }
                        },
                        onClick = {
                            viewModel.setCheckoutWallet(wallet)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Payment value confirmation field
        Text("Nominal Investasi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = viewModel.formatCurrency(amount),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = JagoTeal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("investment_value_confirm")
                )

                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF9EE)).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Info, "info", tint = JagoGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Minimal investasi Rp100.000 dengan sistem autodebet Kantong Syariah.",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun Step2AkadContent(viewModel: JagoViewModel) {
    val isAgreed by viewModel.isAgreedToAkad.collectAsState() // Represents "Menyetujui Ijab Qabul Syariah"
    val isScrolledBottom by viewModel.isContractScrolledBottom.collectAsState() // Represents "Menyetujui Kontrak Akad Digital"
    val campaign by viewModel.selectedCampaign.collectAsState()
    val amount by viewModel.simulatedAmount.collectAsState()
    val loggedInName by viewModel.loggedInName.collectAsState()

    val campaignVal = campaign ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lembar Persetujuan Akad & Ijab Qabul",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Harap verifikasi rincian kesepakatan kemitraan syariah dan nyatakan Ijab Qabul di bawah ini.",
            fontSize = 11.sp,
            color = Color.Gray,
            lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Contract details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pihak Pertama (Nasabah Pemodal)", fontSize = 10.sp, color = Color.Gray)
                    Text(loggedInName.ifEmpty { "Rifan Ashir" }, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pihak Kedua (Penerbit UMKM)", fontSize = 10.sp, color = Color.Gray)
                    Text(campaignVal.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Struktur Akad Syariah", fontSize = 10.sp, color = Color.Gray)
                    Text("Akad ${campaignVal.type} Digital", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = JagoTeal)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Nominal Pendanaan", fontSize = 10.sp, color = Color.Gray)
                    Text(viewModel.formatCurrency(amount), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = JagoPurple)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Contract box container representing a scrollable raw text
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(10.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "BISMILLAHIRRAHMANIRRAHIM\n\nKONTRAK KEMITRAAN SYARIAH\nAKAD ${campaignVal.type.uppercase()} DIGITAL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                val rawContractText = """
                    Dengan menyebut nama Allah Yang Maha Pengasih lagi Maha Penyayang.
                    
                    PASAL 1: PERNYATAAN IJAB QABUL (SERAH TERIMA)
                    Pihak Pertama (Nasabah Pemodal) menyerahkan sejumlah dana investasi secara tunai sesuai nominal yang tertera kepada Pihak Kedua (Penerbit UMKM) sebagai modal usaha untuk diusahakan secara halal, produktif, transparan, dan bertanggung jawab.
                    
                    PASAL 2: NISBAH BAGI HASIL SYARIAH
                    Kedua belah pihak bersepakat atas nisbah bagi hasil, yaitu sebesar ${(campaignVal.nisbahInvestor * 100).toInt()}% dari keuntungan operasional menjadi hak Pihak Pertama, dan ${(campaignVal.nisbahUmkm * 100).toInt()}% menjadi hak Pihak Kedua.
                    
                    PASAL 3: PERLINDUNGAN & PENGEMBALIAN DANA (REFUND)
                    Sesuai dengan ketentuan Fiqh Muamalah Jago Syariah, jika target penggalangan dana proyek ini gagal terkumpul 100% sampai batas waktu berakhir, maka sistem secara otomatis akan membatalkan akad ini dan memindahkan kembali (REFUND 100%) dana Pihak Pertama secara utuh tanpa potongan apapun ke Kantong Utama Syariah.
                    
                    PASAL 4: KEPATUHAN & PENGAWASAN
                    Akad digital ini berkekuatan hukum tetap, bebas dari unsur gharar (ketidakjelasan), maysir (judi), dan riba (bunga), serta diawasi oleh Dewan Pengawas Syariah (DPS) Bank Jago Syariah.
                """.trimIndent()

                Text(
                    text = rawContractText,
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Anchor of Contract
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "--- BATAS AKHIR IJAB QABUL ---",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            // Quick button to simulate scroll to bottom to speed up user testing
            if (!isScrolledBottom || !isAgreed) {
                Button(
                    onClick = {
                        viewModel.simulateScrollToBottom()
                        viewModel.toggleAkadAgreement(true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JagoPurple),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .testTag("simulate_scroll_btn"),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Check, "Setuju Cepat", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Klik untuk Setuju Cepat (Ijab Qabul & Akad)", fontSize = 10.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Requirement warning visual if not agreed to both
        if (!isScrolledBottom || !isAgreed) {
            Surface(
                color = Color(0xFFFFF4EC),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔒 Untuk melanjutkan, harap setujui kedua poin kesepakatan hukum & syariah di bawah ini (atau klik tombol persetujuan cepat di atas).",
                    fontSize = 10.sp,
                    color = JagoGold,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // DOUBLE CHECKBOX REQUIREMENTS:
        // Checkbox 1: Kontrak Akad Digital
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleContractScrolledBottom(!isScrolledBottom) }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isScrolledBottom,
                onCheckedChange = { viewModel.toggleContractScrolledBottom(it) },
                colors = CheckboxDefaults.colors(checkedColor = JagoTeal),
                modifier = Modifier.testTag("contract_checkbox")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Saya menyatakan menyetujui seluruh isi Kontrak Akad Digital secara hukum.",
                fontSize = 10.sp,
                color = if (isScrolledBottom) Color.DarkGray else Color.Gray,
                lineHeight = 14.sp
            )
        }

        // Checkbox 2: Ijab Qabul Syariah
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleAkadAgreement(!isAgreed) }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isAgreed,
                onCheckedChange = { viewModel.toggleAkadAgreement(it) },
                colors = CheckboxDefaults.colors(checkedColor = JagoTeal),
                modifier = Modifier.testTag("ijab_qabul_checkbox")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Saya menyatakan ridha melafalkan Ijab Qabul & menyerahkan dana pembiayaan ini.",
                fontSize = 10.sp,
                color = if (isAgreed) Color.DarkGray else Color.Gray,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun Step3ConfirmationContent(
    viewModel: JagoViewModel,
    amount: Double,
    wallet: ShariaWallet,
    campaign: Campaign
) {
    val pinNumber by viewModel.pinNumber.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Konfirmasi Rincian Investasi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
        Spacer(modifier = Modifier.height(8.dp))

        // Read-only transaction summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RowSummaryItem("Nama Proyek", campaign.title)
                RowSummaryItem("Jenis Kontak Akad", "${campaign.type} Digital")
                RowSummaryItem("Asal Dana", wallet.name)
                RowSummaryItem("Transfer No Rek.", wallet.accountNumber)
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Investasi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(
                        viewModel.formatCurrency(amount),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = JagoTeal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secure PIN Keypad overlay section
        Text("Masukkan PIN Jago Syariah", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JagoPurple)
        Text("Harap masukkan 6-digit PIN sandi keamanan transaksi Anda", fontSize = 11.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))

        // Password indicator pins
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            repeat(6) { index ->
                val filled = index < pinNumber.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(1.5.dp, JagoPurple, CircleShape)
                        .background(
                            color = if (filled) JagoPurple else Color.Transparent,
                            shape = CircleShape
                        )
                )
            }
        }

        Text(
            text = "Petunjuk penguji: Gunakan PIN default 123456",
            fontSize = 9.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Simulated keypad
        Column(
            modifier = Modifier
                .width(280.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val keys = listOf(
                listOf('1', '2', '3'),
                listOf('4', '5', '6'),
                listOf('7', '8', '9'),
                listOf('x', '0', 'd') // x = fingerprint, d = delete
            )

            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { k ->
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF7F9FC))
                                .clickable {
                                    when (k) {
                                        'd' -> viewModel.deletePinChar()
                                        'x' -> {
                                            // Biometric fingerprint placeholder trigger
                                            viewModel.appendPin('1')
                                            viewModel.appendPin('2')
                                            viewModel.appendPin('3')
                                            viewModel.appendPin('4')
                                            viewModel.appendPin('5')
                                            viewModel.appendPin('6')
                                        }

                                        else -> viewModel.appendPin(k)
                                    }
                                }
                                .testTag("keypad_$k"),
                            contentAlignment = Alignment.Center
                        ) {
                            when (k) {
                                'd' -> Icon(Icons.Filled.Backspace, "Hapus", tint = JagoPurple, modifier = Modifier.size(20.dp))
                                'x' -> Icon(Icons.Filled.Fingerprint, "Biometrik", tint = JagoTeal, modifier = Modifier.size(22.dp))
                                else -> Text(k.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowSummaryItem(label: String, valText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = valText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
    }
}

@Composable
fun SuccessScreenContent(viewModel: JagoViewModel, amount: Double, campaign: Campaign) {
    val context = LocalContext.current
    val bannerHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(JagoTeal.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Success",
                tint = JagoTeal,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Investasi Berhasil Disetujui!",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = JagoTeal,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Akad Musyarakah telah sah secara moral syariah & tercantum dalam blockchain sistem regulasi OJK.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Transaction confirmation summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Kwitansi Penerimaan", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                RowSummaryItem("Penerima", campaign.title)
                RowSummaryItem("Metode Pembayaran", "Jago Syariah Autodebet")
                RowSummaryItem("Kategori Sektor", campaign.sector)
                RowSummaryItem("Sertifikat ID", "CERT-88${System.currentTimeMillis() % 1000}X")
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Terpotong", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                    Text(viewModel.formatCurrency(amount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JagoPurple)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Button(
            onClick = {
                scope.launch {
                    bannerHostState.showSnackbar("Sertifikat Akad Musyarakah.pdf disimpan ke folder Unduhan!")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = JagoPurple),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("download_akad_button")
        ) {
            Icon(Icons.Filled.Download, "Download", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Unduh Bukti Akad (PDF)", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                viewModel.cancelCheckout()
                viewModel.selectTab(2) // Transition straight to PAGE 4 (Dashboard Portofolio)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("view_portfolio_button")
        ) {
            Text("Lihat Portofolio")
        }

        Spacer(modifier = Modifier.height(16.dp))
        SnackbarHost(hostState = bannerHostState)
    }
}


// SECTION 5: PAGE 4 - DASHBOARD PORTOFOLIO INVESTOR
@Composable
fun PortfolioScreen(viewModel: JagoViewModel) {
    val investments by viewModel.investments.collectAsState()

    val totalDana = investments.sumOf { it.capital }
    val totalHasil = investments.sumOf { it.totalPayoutsReceived }
    val activeCount = investments.filter { it.currentMonth < it.campaign.tenorMonths }.size

    // State to trace selected item for expandable section (IF-04 logs expansion click)
    var expandedInvestmentId by remember { mutableStateOf<String?>(null) }
    var selectedLogTab by remember { mutableStateOf(0) } // 0: Investasi Aktif, 1: Selesai, 2: Semua

    val filteredInvestments = remember(investments, selectedLogTab) {
        when (selectedLogTab) {
            0 -> investments.filter { it.currentMonth < it.campaign.tenorMonths }
            1 -> investments.filter { it.currentMonth >= it.campaign.tenorMonths }
            else -> investments
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        // Simple elegant header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JagoTeal)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Portofolio Jago Modal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Summary Grid layout
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PremiumSummaryCard(
                        title = "Dana Pembiayaan",
                        valueStr = viewModel.formatCurrency(totalDana),
                        icon = Icons.Filled.AccountBalanceWallet,
                        modifier = Modifier.weight(1.2f)
                    )
                    PremiumSummaryCard(
                        title = "Bagi Hasil",
                        valueStr = viewModel.formatCurrency(totalHasil),
                        icon = Icons.Filled.TrendingUp,
                        highlight = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                PremiumSummaryCard(
                    title = "Proyek Berjalan Syariah",
                    valueStr = "$activeCount UMKM Aktif",
                    icon = Icons.Filled.Business,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Analytics bar chart representation of payouts
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Histori Distribusi Bagi Hasil (6 Bulan Terakhir)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated columns bar charts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val monthlyReturns = listOf(140000f, 185000f, 210000f, 260000f, 203000f, 263125f)
                        val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun")

                        val maxVal = monthlyReturns.maxOrNull() ?: 100000f

                        monthlyReturns.forEachIndexed { i, yieldVal ->
                            val heightRatio = yieldVal / maxVal
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Small text flag representing dynamic price
                                Text(
                                    text = (yieldVal / 1000).toInt().toString() + "k",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JagoTeal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Custom drawn vertical bar
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxHeight(heightRatio * 0.75f) // scale down to fit safely
                                        .background(
                                            color = if (i == 5) JagoTeal else JagoTeal.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = months[i], fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Tab View [Investasi Aktif, Selesai, Semua]
        item {
            TabRow(
                selectedTabIndex = selectedLogTab,
                containerColor = Color.White,
                contentColor = JagoTeal,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                listOf("Investasi Aktif", "Selesai", "Semua").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedLogTab == index,
                        onClick = { selectedLogTab = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        // Active List Items
        if (filteredInvestments.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Belum ada portofolio investasi tercatat", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            items(filteredInvestments) { investment ->
                val isExpanded = expandedInvestmentId == investment.id
                AssetPortfolioCard(
                    investment = investment,
                    isExpanded = isExpanded,
                    onExpandClick = {
                        expandedInvestmentId = if (isExpanded) null else investment.id
                    },
                    viewModel = viewModel
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(86.dp))
        }
    }
}

@Composable
fun PremiumSummaryCard(
    title: String,
    valueStr: String,
    icon: ImageVector,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) Color(0xFFF9F1FC) else Color.White
        ),
        border = BorderStroke(1.dp, if (highlight) JagoPurple.copy(alpha = 0.3f) else Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(if (highlight) JagoPurple.copy(alpha = 0.15f) else JagoTeal.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlight) JagoPurple else JagoTeal,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Text(text = valueStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E1E))
            }
        }
    }
}

@Composable
fun AssetPortfolioCard(
    investment: Investment,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    viewModel: JagoViewModel
) {
    val isFailed = investment.status == "GAGAL_BELUM_REFUND"
    val isRefunded = investment.status == "GAGAL_REFUNDED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("investment_asset_card_${investment.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isFailed) Color(0xFFFCA5A5) else Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpandClick)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = investment.campaign.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                    Text(
                        text = "Kontrak: ${investment.campaign.type}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = when {
                        isFailed -> Color(0xFFFEE2E2)
                        isRefunded -> Color(0xFFE6F4EA)
                        else -> JagoTeal.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = when {
                            isFailed -> "Target Gagal"
                            isRefunded -> "Telah Direfund"
                            else -> "Aktif"
                        },
                        color = when {
                            isFailed -> Color(0xFFDC2626)
                            isRefunded -> Color(0xFF137333)
                            else -> JagoTeal
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isFailed || isRefunded) {
                // Failed or Refunded Layout
                Surface(
                    color = if (isFailed) Color(0xFFFFF5F5) else Color(0xFFF6FBF7),
                    border = BorderStroke(1.dp, if (isFailed) Color(0xFFFCA5A5).copy(alpha = 0.5f) else Color(0xFFCEEAD6)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isFailed) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (isFailed) Color(0xFFDC2626) else Color(0xFF137333),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFailed) "Target Penggalangan Gagal Terpenuhi" else "Dana Pembiayaan Telah Refunded",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFailed) Color(0xFF991B1B) else Color(0xFF137333)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isFailed) {
                                "Proyek berakhir tanpa memenuhi batas nominal pembiayaan minimum."
                            } else {
                                "Saldo dana investasi sebesar ${viewModel.formatCurrency(investment.capital)} telah ditransfer kembali ke Kantong Utama Syariah Anda secara utuh."
                            },
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            color = Color.DarkGray
                        )

                        if (!isExpanded) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "👉 Ketuk kartu ini untuk melihat analisis penyebab, grafik target, laporan keuangan, & opsi refund",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isFailed) Color(0xFFDC2626) else Color(0xFF137333)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Divider(color = if (isFailed) Color(0xFFFCA5A5).copy(alpha = 0.3f) else Color(0xFFCEEAD6), modifier = Modifier.padding(vertical = 8.dp))
                                
                                // 1. Penyebab Gagal
                                Text(
                                    text = "Analisis & Penyebab Kegagalan:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Berdasarkan evaluasi tim kurasi Jago Modal, proyek '${investment.campaign.title}' tidak dapat dilanjutkan karena jumlah komitmen dana terkumpul hanya mencapai 56% hingga batas waktu penawaran berakhir. Sesuai prinsip kehati-hatian Syariah & aturan OJK, akad otomatis batal demi hukum (Inidhadh) jika target pembiayaan 100% tidak tercapai guna mencegah risiko ketimpangan modal kerja operasional.",
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // 2. Grafik Seberapa Jauh Dari Target (Visual Progress Chart)
                                Text(
                                    text = "Visualisasi Penggalangan Dana (Pencapaian vs Target):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                val targetAmt = investment.campaign.targetAmount
                                val collectedAmt = investment.campaign.collectedAmount
                                val collectedPercent = if (targetAmt > 0) ((collectedAmt * 100) / targetAmt).toInt() else 0
                                val deficitAmt = targetAmt - collectedAmt

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Dana Terkumpul ($collectedPercent%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                        Text("Target (100%)", fontSize = 9.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // Custom visual progress bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(16.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF1F5F9))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(if (targetAmt > 0) collectedAmt.toFloat() / targetAmt.toFloat() else 0f)
                                                .fillMaxHeight()
                                                .background(Color(0xFFEF4444))
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = viewModel.formatCurrency(collectedAmt), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                        Text(text = viewModel.formatCurrency(targetAmt), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = "⚠️ Kekurangan Target: ${viewModel.formatCurrency(deficitAmt)} (Kurang ${100 - collectedPercent}%)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // 3. Laporan Keuangan Bisnis (Audit Pra-Rilis)
                                Text(
                                    text = "Laporan Keuangan Mitra UMKM (Audit Pra-Rilis):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                TableLaporanKeuangan(viewModel = viewModel)

                                Spacer(modifier = Modifier.height(12.dp))

                                // 4. Informasi Refund & Action
                                Text(
                                    text = "Informasi Pengembalian Dana:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Modal Investasi Anda:", fontSize = 10.sp, color = Color.DarkGray)
                                    Text(viewModel.formatCurrency(investment.capital), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Ditransfer Kembali Ke:", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("Kantong Utama Syariah", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = JagoTeal)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Potongan Biaya Admin:", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("Rp0 (Gratis Sesuai Akad)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                }

                                if (isFailed) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { viewModel.refundInvestment(investment.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = JagoPurple),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("refund_action_button_${investment.id}")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Refund 100% ke Kantong Utama Syariah", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFFE6F4EA),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.Check, null, tint = Color(0xFF137333), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Dana telah sukses dikembalikan ke Kantong Utama Syariah secara utuh.", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color(0xFF137333))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Active/Normal Layout
                val currentM = investment.currentMonth
                val totalM = investment.campaign.tenorMonths
                val progressFactor = currentM.toFloat() / totalM.toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tenor: Bulan ke-$currentM dari $totalM",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Investasi: ${viewModel.formatCurrency(investment.capital)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progressFactor },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = JagoTeal,
                    trackColor = Color(0xFFEEEEEE)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // green-colored label text for "Bagi Hasil Terakhir"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.TrendingUp, "Bagi Hasil", tint = JagoTeal, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bagi Hasil Terakhir:",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                    Text(
                        text = if (investment.lastPayoutAmount > 0) "+" + viewModel.formatCurrency(investment.lastPayoutAmount) else "Menunggu Pembagian Pertama",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (investment.lastPayoutAmount > 0) SuccessGreen else JagoGold,
                        modifier = Modifier.testTag("bagi_hasil_terakhir_label")
                    )
                }
            }

            // Expandable sections revealing monthly payment logs
            AnimatedVisibility(visible = isExpanded && !isFailed && !isRefunded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(Color(0xFFF9FBFB))
                        .padding(8.dp)
                ) {
                    Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(bottom = 8.dp))
                    Text("Histori Buku Hasil Bulanan (Digital Ledger)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JagoPurple)

                    if (investment.payoutLogs.isEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Amanah: Dana diinvestasikan sedang diproduktifkan oleh mitra UMKM. Estimasi bagi hasil pertama akan disalurkan awal bulan depan.",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            lineHeight = 14.sp
                        )
                    } else {
                        investment.payoutLogs.forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bulan ${log.monthIndex} (${log.date})", fontSize = 10.sp, color = Color.DarkGray)
                                Text(viewModel.formatCurrency(log.amount), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


@Composable
fun UmkmDashboardScreen(viewModel: JagoViewModel) {
    val submittedProposals by viewModel.submittedProposals.collectAsState()
    val umkmWallets by viewModel.umkmWallets.collectAsState()
    val umkmTransactions by viewModel.umkmTransactions.collectAsState()
    val umkmCampaignRefunded by viewModel.umkmCampaignRefunded.collectAsState()
    
    var selectedWalletForReturn by remember { mutableStateOf("") }
    var selectedWalletForRefund by remember { mutableStateOf("") }
    
    var returnAmountStr by remember { mutableStateOf("5000000") }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successTitle by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    
    var showConfirmRefundDialog by remember { mutableStateOf(false) }
    var showConfirmReturnDialog by remember { mutableStateOf(false) }
    
    if (selectedWalletForReturn.isEmpty() && umkmWallets.isNotEmpty()) {
        selectedWalletForReturn = umkmWallets.first().name
    }
    if (selectedWalletForRefund.isEmpty() && umkmWallets.isNotEmpty()) {
        selectedWalletForRefund = umkmWallets.last().name
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcoming card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = JagoPurple),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Storefront,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Dasbor Penerbit UMKM",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ayam Bakar Sambal Korek Barokah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "Kemitraan Amanah Syariah • PT Kuliner Barokah Nusantara",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        
        // Pockets/Wallets balance list
        Text(
            text = "Kantong Jago Syariah Usaha Anda",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                umkmWallets.forEachIndexed { index, wallet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = null,
                                tint = JagoPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(wallet.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                Text(wallet.accountNumber, fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                        Text(
                            text = viewModel.formatCurrency(wallet.balance),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JagoPurple
                        )
                    }
                    if (index < umkmWallets.size - 1) {
                        Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        // Feature 1 & 2: Campaign Performance & Details
        Text(
            text = "Performa Kampanye Pendanaan Aktif",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ayam Bakar Sambal Korek",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    
                    Surface(
                        color = if (umkmCampaignRefunded) Color(0xFFFEE2E2) else Color(0xFFECFDF5),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (umkmCampaignRefunded) "DIREFUND (Gagal)" else "AKTIF (Menggalang Dana)",
                            color = if (umkmCampaignRefunded) Color(0xFFDC2626) else Color(0xFF059669),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Progress
                val targetAmount = 120000000.0
                val collectedAmount = if (umkmCampaignRefunded) 0.0 else 110000000.0
                val progress = (collectedAmount / targetAmount).toFloat()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progres Pendanaan", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        "${(progress * 100).toInt()}% Terkumpul",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = JagoPurple
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = JagoPurple,
                    trackColor = Color(0xFFE2E8F0)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Telah Terkumpul", fontSize = 10.sp, color = Color.Gray)
                        Text(viewModel.formatCurrency(collectedAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Target Modal", fontSize = 10.sp, color = Color.Gray)
                        Text(viewModel.formatCurrency(targetAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Group, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("94 Investor Nasabah", fontSize = 11.sp, color = Color.DarkGray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timer, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sisa 2 Hari Lagi", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }
        }
        
        // Feature 5: Transfer Return (Bagi Hasil) to Investors
        Text(
            text = "Transfer Bagi Hasil Bulanan",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Sesuai akad Musyarakah, transfer bagi hasil riil usaha Anda secara berkala kepada 94 investor nasabah.",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Select source pocket
                Text("Pilih Sumber Kantong Jago", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                umkmWallets.forEach { wallet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedWalletForReturn = wallet.name }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedWalletForReturn == wallet.name),
                            onClick = { selectedWalletForReturn = wallet.name },
                            colors = RadioButtonDefaults.colors(selectedColor = JagoPurple)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${wallet.name} (${viewModel.formatCurrency(wallet.balance)})",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Pre-selected amounts
                Text("Pilih Nominal Transfer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val amounts = listOf("2500000", "5000000", "10000000")
                    amounts.forEach { amt ->
                        val isSelected = returnAmountStr == amt
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .clickable { returnAmountStr = amt },
                            color = if (isSelected) JagoPurple.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isSelected) JagoPurple else Color.Transparent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = viewModel.formatCurrency(amt.toDouble()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) JagoPurple else Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { showConfirmReturnDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = JagoPurple),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !umkmCampaignRefunded
                ) {
                    Icon(Icons.Filled.Send, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Transfer Bagi Hasil Ke Investor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        // Feature 3: Refund Dana Penerbit UMKM
        Text(
            text = "Kompensasi & Refund Modal Investor",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Sesuai komitmen amanah syariah: jika kinerja usaha Anda dinilai buruk atau tidak berjalan lancar, Anda dapat mengembalikan (refund) 100% modal terkumpul Rp110.000.000 kembali ke Jago pockets investor nasabah.",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                if (umkmCampaignRefunded) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Dana kampanye sebesar Rp110.000.000 berhasil direfund penuh kepada seluruh nasabah investor.",
                                fontSize = 11.sp,
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Text("Pilih Sumber Kantong Refund", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    umkmWallets.forEach { wallet ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedWalletForRefund = wallet.name }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedWalletForRefund == wallet.name),
                                onClick = { selectedWalletForRefund = wallet.name },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFDC2626))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${wallet.name} (${viewModel.formatCurrency(wallet.balance)})",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showConfirmRefundDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.Cancel, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lakukan Refund 100% Modal Investor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        // Feature 4: Re-apply Rejected Proposals
        Text(
            text = "Status Pengajuan & Riwayat Proposal",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        submittedProposals.forEach { proposal ->
            val isRejected = proposal.status.startsWith("Ditolak")
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(
                    width = if (isRejected) 2.dp else 1.dp,
                    color = if (isRejected) Color(0xFFF87171) else Color(0xFFE2E8F0)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(proposal.businessName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text(proposal.id, fontSize = 10.sp, color = Color.Gray)
                        }
                        Surface(
                            color = if (isRejected) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = proposal.status,
                                color = if (isRejected) Color(0xFFDC2626) else Color.DarkGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(proposal.description, fontSize = 11.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Target: ${viewModel.formatCurrency(proposal.capitalTarget)} • Tenor: ${proposal.tenureMonths} Bulan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = JagoPurple
                    )
                    
                    if (isRejected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.reApplyRejectedProposal(proposal) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = JagoPurple.copy(alpha = 0.12f), contentColor = JagoPurple),
                            border = BorderStroke(1.dp, JagoPurple),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajukan Ulang / Perbaiki Proposal Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Transaction History list
        Text(
            text = "Daftar Riwayat Transaksi Penerbit",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (umkmTransactions.isEmpty()) {
                    Text("Belum ada transaksi bagi hasil atau refund.", fontSize = 11.sp, color = Color.Gray)
                } else {
                    umkmTransactions.forEachIndexed { idx, tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (tx.type == "REFUND") Icons.Filled.Cancel else Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = if (tx.type == "REFUND") Color(0xFFDC2626) else Color(0xFF059669),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(tx.description, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    Text("Sumber: ${tx.walletSource} • ${tx.date}", fontSize = 9.sp, color = Color.Gray)
                                }
                            }
                            Text(
                                text = "- ${viewModel.formatCurrency(tx.amount)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                        if (idx < umkmTransactions.size - 1) {
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        }
    }
    
    // Dialogs
    if (showConfirmReturnDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmReturnDialog = false },
            title = { Text("Konfirmasi Transfer Bagi Hasil", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin mentransfer bagi hasil sebesar ${viewModel.formatCurrency(returnAmountStr.toDoubleOrNull() ?: 5000000.0)} kepada investor nasabah melalui ${selectedWalletForReturn}?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmReturnDialog = false
                        val amt = returnAmountStr.toDoubleOrNull() ?: 5000000.0
                        val success = viewModel.transferReturnToInvestors(amt, selectedWalletForReturn)
                        if (success) {
                            successTitle = "Bagi Hasil Berhasil!"
                            successMessage = "Bagi hasil syariah sebesar ${viewModel.formatCurrency(amt)} berhasil didistribusikan ke Jago pockets milik investor secara realtime."
                            showSuccessDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JagoPurple)
                ) {
                    Text("Ya, Transfer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmReturnDialog = false }) { Text("Batal") }
            }
        )
    }
    
    if (showConfirmRefundDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmRefundDialog = false },
            title = { Text("Konfirmasi Refund Modal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) },
            text = { Text("Peringatan: Tindakan ini akan mengembalikan 100% modal terkumpul Rp110.000.000 kepada para investor secara utuh karena kinerja yang buruk. Tindakan ini tidak dapat dibatalkan!") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmRefundDialog = false
                        val success = viewModel.refundCampaignToInvestors(selectedWalletForRefund)
                        if (success) {
                            successTitle = "Refund Berhasil!"
                            successMessage = "Seluruh dana investasi sebesar Rp110.000.000 telah berhasil dikembalikan ke kantong para investor nasabah secara adil dan amanah."
                            showSuccessDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Ya, Refund Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmRefundDialog = false }) { Text("Batal") }
            }
        )
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text(successTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = JagoPurple) },
            text = { Text(successMessage) },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = JagoPurple)
                ) {
                    Text("OK")
                }
            }
        )
    }
}


// SECTION 6: PAGE 5 - FORMULIR PENGAJUAN KAMPANYE (UMKM VIEW)
@Composable
fun UmkmOnboardingScreen(viewModel: JagoViewModel) {
    val step by viewModel.proposalStep.collectAsState()
    val submittedProposals by viewModel.submittedProposals.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(
                title = "Pengajuan Modal UMKM Syariah",
                showBackButton = step > 1 && step < 5,
                onBackClick = { viewModel.setProposalStep(step - 1) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FB))
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Steps tracker if not completed
                if (step <= 4) {
                    UmkmStepIndicator(currentStep = step)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (step) {
                        1 -> UmkmStep1InfoUsaha(viewModel)
                        2 -> UmkmStep2Pendanaan(viewModel)
                        3 -> UmkmStep3Dokumen(viewModel)
                        4 -> UmkmStep4Review(viewModel)
                        5 -> UmkmStatusTrackerDashboard(viewModel, submittedProposals)
                    }
                }

                // Normal bottom navigations buttons
                if (step <= 4) {
                    Surface(
                        tonalElevation = 8.dp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .navigationBarsPadding(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (step > 1) {
                                OutlinedButton(
                                    onClick = { viewModel.setProposalStep(step - 1) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Text("Kembali")
                                }
                            }

                            Button(
                                onClick = {
                                    if (step < 4) {
                                        viewModel.setProposalStep(step + 1)
                                    } else {
                                        viewModel.submitProposal()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JagoTeal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(44.dp)
                                    .testTag("proposal_next_btn")
                            ) {
                                Text(
                                    text = if (step == 4) "Kirim Pengajuan" else "Lanjut",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UmkmStepIndicator(currentStep: Int) {
    val steps = listOf("1. Info", "2. Dana", "3. Dokumen", "4. Review")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, name ->
            val num = index + 1
            val isActive = num == currentStep
            val isCompleted = num < currentStep

            val col = if (isActive) JagoTeal else if (isCompleted) Color.Gray else Color.LightGray

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(if (isActive || isCompleted) JagoTeal else Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(10.dp))
                    } else {
                        Text(num.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(name, fontSize = 10.sp, color = col, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
            }

            if (index < steps.size - 1) {
                Icon(Icons.Filled.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun UmkmStep1InfoUsaha(viewModel: JagoViewModel) {
    val name by viewModel.proposalBusinessName.collectAsState()
    val sector by viewModel.proposalSector.collectAsState()
    val address by viewModel.proposalAddress.collectAsState()
    val desc by viewModel.proposalDescription.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Langkah 1: Identitas & Informasi Usaha", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JagoPurple)

        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.proposalBusinessName.value = it },
            label = { Text("Nama Usaha UMKM") },
            placeholder = { Text("Contoh: Kedai Bakso Mas Jhon") },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("proposal_business_name"),
            shape = RoundedCornerShape(8.dp)
        )

        // Dropdown sector selector
        var expandedSectors by remember { mutableStateOf(false) }
        val sectorsList = listOf("Sektor Kuliner", "Sektor Fashion", "Sektor Jasa", "Sektor Agrobisnis")

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = sector,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Sektor Industri") },
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                trailingIcon = {
                    IconButton(onClick = { expandedSectors = true }) {
                        Icon(Icons.Filled.ArrowDropDown, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedSectors = true }
                    .testTag("proposal_sector_selector"),
                shape = RoundedCornerShape(8.dp)
            )

            DropdownMenu(expanded = expandedSectors, onDismissRequest = { expandedSectors = false }) {
                sectorsList.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s) },
                        onClick = {
                            viewModel.proposalSector.value = s
                            expandedSectors = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = address,
            onValueChange = { viewModel.proposalAddress.value = it },
            label = { Text("Alamat Kantor / Outlet") },
            placeholder = { Text("Contoh: Jl. Diponegoro No. 12, Surabaya") },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { viewModel.proposalDescription.value = it },
            label = { Text("Deskripsi Singkat Operasional Usaha") },
            placeholder = { Text("Terangkan secara ringkas produk, pasar, dan rencana penggunaan modal syariah...") },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Image attachment mock button
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1EEFA)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.PhotoCamera, "Camera", tint = JagoPurple)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Format Foto Usaha / Lokasi Outlet", color = JagoPurple, fontSize = 12.sp)
        }
    }
}

@Composable
fun UmkmStep2Pendanaan(viewModel: JagoViewModel) {
    val targetVal by viewModel.proposalCapitalTarget.collectAsState()
    val contractType by viewModel.proposalContractType.collectAsState()
    val tenor by viewModel.proposalTenureMonths.collectAsState()

    var showNisbahTooltip by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Langkah 2: Detail Kebutuhan Finansial Syariah", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JagoPurple)

        OutlinedTextField(
            value = targetVal,
            onValueChange = { viewModel.proposalCapitalTarget.value = it },
            label = { Text("Target Nominal Pembiayaan (Rupiah)") },
            placeholder = { Text("Minimal Rp50.000.000") },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("proposal_target_capital"),
            shape = RoundedCornerShape(8.dp)
        )

        Text("Pilihan Struktur Kontrak Akad Syariah", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Musyarakah", "Mudharabah").forEach { t ->
                val isSel = contractType == t
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.proposalContractType.value = t }
                        .testTag("akad_picker_$t"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSel) Color(0xFFEAF5F5) else Color.White
                    ),
                    border = BorderStroke(2.dp, if (isSel) JagoTeal else Color(0xFFDDDDDD))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (t == "Musyarakah") Icons.Filled.Group else Icons.Filled.SupervisorAccount,
                            contentDescription = t,
                            tint = if (isSel) JagoTeal else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(t, fontWeight = FontWeight.Bold, color = if (isSel) JagoTeal else Color.DarkGray, fontSize = 13.sp)
                    }
                }
            }
        }

        // Info helpers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF9EE))
                .clickable { showNisbahTooltip = !showNisbahTooltip }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Info, "Help", tint = JagoGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Klik di sini untuk melihat perbedaan Akad Musyarakah vs Mudharabah.",
                fontSize = 11.sp,
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
        }

        if (showNisbahTooltip) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, JagoGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("💡 Panduan Kepatuhan Akad Syariah:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JagoGold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Musyarakah: Usaha patungan modal syirkah di mana kedua pihak berkontribusi modal fisik & kerja. Bagi hasil disepakati di awal, ruginya bareng-bareng.\n• Mudharabah: Mitra UMKM murni bekerja mengelola usaha, sedangkan investor menyetor 100% modal pasif finansial.",
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Tenure Duration Choice Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Durasi Tenor Pengembalian", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("${tenor.roundToInt()} Bulan", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = JagoTeal)
        }

        Slider(
            value = tenor,
            onValueChange = { viewModel.proposalTenureMonths.value = it },
            valueRange = 3f..24f,
            steps = 21,
            colors = SliderDefaults.colors(thumbColor = JagoTeal, activeTrackColor = JagoTeal)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Min: 3 Bulan", fontSize = 10.sp, color = Color.Gray)
            Text("Premium: 12 Bulan", fontSize = 10.sp, color = Color.Gray)
            Text("Max: 24 Bulan", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun UmkmStep3Dokumen(viewModel: JagoViewModel) {
    val nibUploaded by viewModel.proposalNibUploaded.collectAsState()
    val npwpUploaded by viewModel.proposalNpwpUploaded.collectAsState()
    val financialUploaded by viewModel.proposalFinancialUploaded.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Langkah 3: Unggah Legalitas Usaha (Kepatuhan OJK)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JagoPurple)
        Text(
            text = "Klik pada setiap box dokumen di bawah ini untuk mensimulasikan kepatuhan drag & drop berkas pendanaan.",
            fontSize = 11.sp,
            color = Color.Gray,
            lineHeight = 15.sp
        )

        // Box 1: NIB
        SimulatedDocumentUploadBox(
            title = "Nomor Induk Berusaha (NIB)",
            sub = "Format berkas: PDF maximal 5MB",
            isUploaded = nibUploaded,
            onClick = { viewModel.toggleNib() },
            tag = "nib_box"
        )

        // Box 2: NPWP
        SimulatedDocumentUploadBox(
            title = "NPWP Badan Usaha / Pemilik",
            sub = "Fotokopi NPWP pemrakarsa",
            isUploaded = npwpUploaded,
            onClick = { viewModel.toggleNpwp() },
            tag = "npwp_box"
        )

        // Box 3: Financial statement
        SimulatedDocumentUploadBox(
            title = "Laporan Laba Rugi Sederhana (6 Bulan Terakhir)",
            sub = "Dokumen pembukuan sirkulasi kas bulanan",
            isUploaded = financialUploaded,
            onClick = { viewModel.toggleFinancial() },
            tag = "financial_box"
        )
    }
}

@Composable
fun SimulatedDocumentUploadBox(
    title: String,
    sub: String,
    isUploaded: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        colors = CardDefaults.cardColors(
            containerColor = if (isUploaded) Color(0xFFEBF6F1) else Color.White
        ),
        border = BorderStroke(
            1.5.dp,
            if (isUploaded) SuccessGreen else Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isUploaded) Icons.Filled.CheckCircle else Icons.Filled.CloudUpload,
                contentDescription = null,
                tint = if (isUploaded) SuccessGreen else JagoTeal,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1F1F1F), textAlign = TextAlign.Center)
            Text(sub, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)

            // Simulated progress loader if uploaded
            if (isUploaded) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { 1f },
                        color = SuccessGreen,
                        trackColor = Color.LightGray,
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("100% Selesai", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UmkmStep4Review(viewModel: JagoViewModel) {
    val bName by viewModel.proposalBusinessName.collectAsState()
    val bSector by viewModel.proposalSector.collectAsState()
    val bAddress by viewModel.proposalAddress.collectAsState()
    val bDesc by viewModel.proposalDescription.collectAsState()
    val targetVal by viewModel.proposalCapitalTarget.collectAsState()
    val contractType by viewModel.proposalContractType.collectAsState()
    val tenor by viewModel.proposalTenureMonths.collectAsState()

    val targetDouble = targetVal.toDoubleOrNull() ?: 50000000.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Langkah 4: Review Validasi Dokumen", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JagoPurple)
        Text(
            text = "Harap tinjau ringkasan pengajuan proposal modal crowdfunding syariah Anda sebelum diterbitkan ke bursa penawaran publik.",
            fontSize = 11.sp,
            color = Color.Gray,
            lineHeight = 15.sp
        )

        // Config Table Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RowSummaryItem("Nama Usaha", bName.ifBlank { "Bakso Berkah Mandiri" })
                RowSummaryItem("Sektor Industri", bSector)
                RowSummaryItem("Akad Diusulkan", contractType)
                RowSummaryItem("Target Pembiayaan", viewModel.formatCurrency(targetDouble))
                RowSummaryItem("Jadwal Tenor", "${tenor.roundToInt()} Bulan")
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                Text("Alamat Usaha:", fontSize = 10.sp, color = Color.Gray)
                Text(bAddress.ifBlank { "Surabaya, Jawa Timur" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Sinopsis Bisnis:", fontSize = 10.sp, color = Color.Gray)
                Text(
                    bDesc.ifBlank { "Mengembangkan sistem digitalisasi logistik pasokan rempah lokal." },
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun UmkmStatusTrackerDashboard(viewModel: JagoViewModel, proposals: List<Proposal>) {
    var selectedSimulatedStatus by remember { mutableStateOf("Menunggu Review Admin") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simulated administrative buttons so the reviewer can easily test states
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEC)),
            border = BorderStroke(1.dp, JagoPurple.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("⚙️ PANEL SIMULASI PROSES REVIEW (DEVELOPER CHECK):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JagoPurple)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Simulasikan perubahan status administratif dari admin Bank Jago Syariah:", fontSize = 10.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Menunggu Review Admin", "Revisi Diperlukan", "Disetujui/Aktif").forEach { s ->
                        Button(
                            onClick = { selectedSimulatedStatus = s },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedSimulatedStatus == s) JagoPurple else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, JagoPurple.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = s.replace(" Admin", "").replace(" Diperlukan", ""),
                                fontSize = 9.sp,
                                color = if (selectedSimulatedStatus == s) Color.White else JagoPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Text("Status Pengajuan Pembiayaan Anda", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))

        proposals.forEach { proposal ->
            // Use simulated status state or fallback to model state
            val activeStatus = if (proposal == proposals.first()) selectedSimulatedStatus else proposal.status

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("proposal_status_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(proposal.businessName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E1E))

                        // Custom dynamic status pill badge
                        val statusCol = when (activeStatus) {
                            "Disetujui/Aktif" -> SuccessGreen
                            "Revisi Diperlukan" -> AlertRed
                            else -> JagoGold // "Menunggu Review Admin"
                        }
                        val statusBg = when (activeStatus) {
                            "Disetujui/Aktif" -> Color(0xFFEBF6F1)
                            "Revisi Diperlukan" -> Color(0xFFFDE8E8)
                            else -> Color(0xFFFFFAEB)
                        }

                        Surface(color = statusBg, shape = RoundedCornerShape(12.dp)) {
                            Text(
                                text = activeStatus,
                                color = statusCol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("proposal_status_badge")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    RowSummaryItem("Sektor Industri", proposal.sector)
                    RowSummaryItem("Target Modal", viewModel.formatCurrency(proposal.capitalTarget))
                    RowSummaryItem("Mekanisme Akad", proposal.contractType)
                    RowSummaryItem("Rencana Tenor", "${proposal.tenureMonths} Bulan")
                    RowSummaryItem("Waktu Daftar", proposal.submittedAt)

                    // Conditional details banner based on status
                    Spacer(modifier = Modifier.height(12.dp))

                    when (activeStatus) {
                        "Menunggu Review Admin" -> {
                            Surface(color = Color(0xFFFFFBEB), shape = RoundedCornerShape(6.dp)) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.HourglassEmpty, null, tint = JagoGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Dokumen komplit sedang diverifikasi oleh Tim Pengawas Hubungan Kerja Syariah Bank Jago. Estimasi penyelesaian review: 1-2 hari kerja.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF7A6000),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        "Revisi Diperlukan" -> {
                            Surface(color = Color(0xFFFDF2F2), shape = RoundedCornerShape(6.dp)) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Warning, null, tint = AlertRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Dokumen NPWP Anda buram atau terpotong pada lembar kedua. Silakan klik hubungi customer relation untuk mengajukan revisi formulir.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF9B1C1C),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        "Disetujui/Aktif" -> {
                            Surface(color = Color(0xFFEFFBF5), shape = RoundedCornerShape(6.dp)) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Selamat! Pengajuan Anda disetujui & kampanye Anda resmi diterbitkan di bursa crowdfunding Beranda Jago Modal.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF14532D),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.clearProposalFormAndReset() },
            colors = ButtonDefaults.buttonColors(containerColor = JagoTeal),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buat Pengajuan Baru", color = Color.White)
        }
    }
}

@Composable
fun DpsDashboardScreen(viewModel: JagoViewModel) {
    val proposals by viewModel.submittedProposals.collectAsState()
    
    // Split proposals into pending vs audited histories
    val pendingProposals = proposals.filter { it.status == "Menunggu Review" }
    val auditedProposals = proposals.filter { it.status != "Menunggu Review" }
    
    var selectedProposalIdForAudit by remember { mutableStateOf<String?>(null) }
    
    // Let's auto-select the first pending proposal if none is selected
    LaunchedEffect(pendingProposals) {
        if (selectedProposalIdForAudit == null && pendingProposals.isNotEmpty()) {
            selectedProposalIdForAudit = pendingProposals.first().id
        }
    }
    
    val selectedProposal = proposals.find { it.id == selectedProposalIdForAudit }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Dasbor Penelaahan & Opini Syariah",
                showBackButton = true,
                onBackClick = { viewModel.setDpsDashboardActive(false) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
        ) {
            // Header Hero Banner with Sharia Credentials
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(JagoPurple, JagoPurple.copy(alpha = 0.85f))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tim Pengawas Syariah",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Melakukan verifikasi kepatuhan, kehalalan, dan menerbitkan opini syariah digital secara transparan.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Filled.Gavel,
                        contentDescription = "Gavel",
                        tint = JagoGold,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- SECTION: DPS PROFILE & LOGOUT CARD ---
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dps_profile_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AccountBox, null, tint = JagoPurple, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PROFIL ANGGOTA DEWAN PENGAWAS SYARIAH", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = JagoPurple, letterSpacing = 0.5.sp)
                            }
                            
                            // Logout button
                            TextButton(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("dps_logout_button")
                            ) {
                                Icon(Icons.Filled.Logout, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Keluar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Nama Lengkap", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                Text("KH. Ahmad Syarifuddin, M.A.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Jabatan Utama", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                Text("Ketua Komite Pengawas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("No. Anggota DSN-MUI", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                Text("DSN-MUI/A-2019-8971", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sertifikat Kompetensi", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                Text("Auditor Syariah Utama", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Instansi & Alamat", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text("Dewan Syariah Nasional - MUI, Gedung MUI Menteng", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        }
                    }
                }

                // PART 1: Pending Queue List
                Text(
                    text = "Antrean Penelaahan Aktif (${pendingProposals.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                if (pendingProposals.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.FactCheck, null, tint = SuccessGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Semua Bersih! Tidak Ada Antrean",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                            Text(
                                "Seluruh proposal UMKM syariah yang diajukan telah selesai diperiksa dan diaudit oleh DPS.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    // Show small horizontal chip list or list cards of pending reviews
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pendingProposals.forEach { prop ->
                            val isSelected = prop.id == selectedProposalIdForAudit
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedProposalIdForAudit = prop.id },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFF0FDF4) else Color.White
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) JagoTeal else Color(0xFFE2E8F0)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                if (isSelected) JagoTeal.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Storefront,
                                            contentDescription = null,
                                            tint = if (isSelected) JagoTeal else Color(0xFF64748B),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prop.businessName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) JagoTeal else Color(0xFF1E293B)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = prop.sector,
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = "•",
                                                fontSize = 11.sp,
                                                color = Color(0xFFCBD5E1)
                                            )
                                            Text(
                                                text = "Target: ${viewModel.formatCurrency(prop.capitalTarget)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.ChevronRight,
                                        contentDescription = null,
                                        tint = if (isSelected) JagoTeal else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // PART 2: Selected Proposal Audit Checklist Card
                if (selectedProposal != null) {
                    Text(
                        text = "Lembar Kerja Kepatuhan Syariah: ${selectedProposal.businessName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    // Interactive checklist state
                    var checkHalal by remember(selectedProposal.id) { mutableStateOf(false) }
                    var checkGharar by remember(selectedProposal.id) { mutableStateOf(false) }
                    var checkMaysir by remember(selectedProposal.id) { mutableStateOf(false) }
                    var checkRiba by remember(selectedProposal.id) { mutableStateOf(false) }

                    // Custom Dialog States
                    var showRejectDialog by remember(selectedProposal.id) { mutableStateOf(false) }
                    var rejectionReasonInput by remember(selectedProposal.id) { mutableStateOf("Rasio leverage usaha dinilai masih terlalu tinggi dan memerlukan transparansi agunan.") }

                    var showSignDialog by remember(selectedProposal.id) { mutableStateOf(false) }
                    var signatureNameInput by remember(selectedProposal.id) { mutableStateOf("KH. Ahmad Syarifuddin, M.A.") }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Info Ringkas Bisnis
                            Text(
                                text = "INFORMASI PROYEK UMKM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = JagoPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedProposal.description,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Rencana Akad", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(selectedProposal.contractType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JagoGold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Tenor Usaha", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text("${selectedProposal.tenureMonths} Bulan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                }
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text("Lokasi Proyek", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(selectedProposal.address, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(16.dp))

                            // Audit Checklists
                            Text(
                                text = "DAFTAR VERIFIKASI PEMERIKSA (AUDIT CHECKLIST)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Checklist 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { checkHalal = !checkHalal }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checkHalal,
                                    onCheckedChange = { checkHalal = it },
                                    colors = CheckboxDefaults.colors(checkedColor = JagoTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Sektor Industri Bebas dari Produk Non-Halal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                    Text("Usaha tidak memproduksi alkohol, babi, judi, hiburan maksiat.", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }

                            // Checklist 2
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { checkGharar = !checkGharar }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checkGharar,
                                    onCheckedChange = { checkGharar = it },
                                    colors = CheckboxDefaults.colors(checkedColor = JagoTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Bebas dari Unsur Gharar (Ketidakpastian)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                    Text("Skema bisnis dan kepemilikan aset jelas & bebas manipulasi.", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }

                            // Checklist 3
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { checkMaysir = !checkMaysir }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checkMaysir,
                                    onCheckedChange = { checkMaysir = it },
                                    colors = CheckboxDefaults.colors(checkedColor = JagoTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Bebas dari Unsur Maysir (Spekulasi / Judi)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                    Text("Bisnis murni operasional riil, bukan instrumen spekulasi mata uang.", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }

                            // Checklist 4
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { checkRiba = !checkRiba }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checkRiba,
                                    onCheckedChange = { checkRiba = it },
                                    colors = CheckboxDefaults.colors(checkedColor = JagoTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Bebas dari Unsur Riba, Zhalim & Eksploitasi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                    Text("Sistem kemitraan adil, tidak menetapkan bunga tetap sepihak.", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Dual Action Buttons
                            val allCheckedActual = checkHalal && checkGharar && checkMaysir && checkRiba

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        showRejectDialog = true
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                                    border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("dps_reject_trigger")
                                ) {
                                    Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tolak", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        showSignDialog = true
                                    },
                                    enabled = allCheckedActual,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = JagoTeal,
                                        disabledContainerColor = Color(0xFFE2E8F0)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(44.dp)
                                        .testTag("dps_approve_trigger")
                                ) {
                                    Icon(Icons.Filled.Verified, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Setujui & Tandatangani", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            if (!allCheckedActual) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "*Harap centang semua aspek verifikasi audit di atas untuk menandatangani opini kelayakan syariah emiten.",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Dialog Rejection (dengan alasan penolakan/revisi)
                    if (showRejectDialog) {
                        AlertDialog(
                            onDismissRequest = { showRejectDialog = false },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Close, null, tint = AlertRed)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tolak & Berikan Alasan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "Tuliskan masukan evaluasi atau alasan penolakan syariah sebagai panduan perbaikan bagi Penerbit UMKM:",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    OutlinedTextField(
                                        value = rejectionReasonInput,
                                        onValueChange = { rejectionReasonInput = it },
                                        label = { Text("Alasan Penolakan", fontSize = 11.sp) },
                                        minLines = 3,
                                        modifier = Modifier.fillMaxWidth().testTag("rejection_reason_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AlertRed,
                                            focusedLabelColor = AlertRed
                                        )
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (rejectionReasonInput.isNotEmpty()) {
                                            viewModel.rejectProposal(selectedProposal.id, rejectionReasonInput)
                                            showRejectDialog = false
                                            selectedProposalIdForAudit = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                                    modifier = Modifier.testTag("rejection_confirm_button")
                                ) {
                                    Text("Kirim Penolakan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showRejectDialog = false }) {
                                    Text("Batal", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        )
                    }

                    // Dialog Digital Signature Opini Syariah
                    if (showSignDialog) {
                        AlertDialog(
                            onDismissRequest = { showSignDialog = false },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.VerifiedUser, null, tint = JagoTeal)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tanda Tangan Opini Syariah", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            text = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Dokumen kesepakatan kelayakan syariah emiten Jago Modal:",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    // Surat Opini Syariah Box
                                    Surface(
                                        color = Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "SURAT REKOMENDASI OPINI KEPATUHAN SYARIAH",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = JagoPurple,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Menimbang hasil audit kepatuhan, Dewan Pengawas Syariah Bank Jago menerangkan bahwa kegiatan usaha emiten \"${selectedProposal.businessName}\" dengan rencana akad \"${selectedProposal.contractType}\" dinyatakan memenuhi pilar & syarat syariat Islam serta terbebas dari unsur Riba, Gharar, dan Maysir.",
                                                fontSize = 9.sp,
                                                color = Color(0xFF334155),
                                                lineHeight = 13.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "KESEPAKATAN NISBAH BAGI HASIL:\n- Porsi Nasabah Investor: 65% Keuntungan\n- Porsi Penerbit UMKM: 35% Keuntungan",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = JagoGold,
                                                lineHeight = 13.sp
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = signatureNameInput,
                                        onValueChange = { signatureNameInput = it },
                                        label = { Text("Nama Lengkap Penandatangan", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("signature_name_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = JagoTeal,
                                            focusedLabelColor = JagoTeal
                                        )
                                    )

                                    // Stylized Digital Signature Graphic Card
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            // Mock Arabic Calligraphy / Signature Symbol
                                            Text(
                                                text = signatureNameInput,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = JagoPurple.copy(alpha = 0.8f),
                                                fontStyle = FontStyle.Italic,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "E-SIGNATURE SECURE SHA-256",
                                                fontSize = 8.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (signatureNameInput.isNotEmpty()) {
                                            viewModel.approveProposal(selectedProposal.id, signatureNameInput)
                                            showSignDialog = false
                                            selectedProposalIdForAudit = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JagoTeal),
                                    modifier = Modifier.testTag("signature_confirm_button")
                                ) {
                                    Text("Tandatangani & Terbitkan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showSignDialog = false }) {
                                    Text("Batal", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        )
                    }
                }

                // PART 3: Audited History Logs
                Text(
                    text = "Riwayat Opini Syariah & Keputusan (${auditedProposals.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    auditedProposals.forEach { prop ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val isApproved = prop.status == "Disetujui/Aktif"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = prop.businessName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )

                                    // Status Pill
                                    val badgeColor = if (isApproved) SuccessGreen else AlertRed
                                    val badgeBg = if (isApproved) Color(0xFFEBF6F1) else Color(0xFFFDE8E8)
                                    
                                    Surface(color = badgeBg, shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = if (isApproved) "AKTIF & SYARIAH" else "DITOLAK/REVISI",
                                            color = badgeColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Nisbah: ${prop.contractType} • Dana: ${viewModel.formatCurrency(prop.capitalTarget)}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = Color(0xFFF8FAFC))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isApproved) Icons.Filled.VerifiedUser else Icons.Filled.Error,
                                        contentDescription = null,
                                        tint = if (isApproved) JagoGold else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isApproved) {
                                            "Opini Syariah Digital: SA-${prop.id.substringAfter("-")}-MUI (Kepatuhan Penuh)"
                                        } else {
                                            "Keputusan Audit: Gagal verifikasi orisinalitas berkas NPWP."
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: JagoViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsState()
    
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Decorative Top Arc background representing the Jago Islamic Purple
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            drawRect(
                color = JagoPurple,
                size = Size(size.width, size.height - 40f)
            )
            drawCircle(
                color = JagoPurple,
                radius = size.width,
                center = Offset(size.width / 2, size.height - 40f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = JagoGold,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Jago Syariah",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = "MODAL SYARIAH PROTOTYPE",
                color = JagoGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Login Container Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk ke Akun Anda",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Gunakan kredensial atau klik simulasi cepat di bawah",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Error Box
                    if (loginError != null) {
                        Surface(
                            color = Color(0xFFFDE8E8),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Error, null, tint = AlertRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = loginError ?: "",
                                    color = AlertRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        placeholder = { Text("admin / investor / nasabah") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_username_input"),
                        leadingIcon = {
                            Icon(Icons.Filled.Person, null, tint = Color.Gray)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JagoTeal,
                            focusedLabelColor = JagoTeal
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("Masukkan password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = Color.Gray)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                                    tint = Color.Gray
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JagoTeal,
                            focusedLabelColor = JagoTeal
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Trigger Button
                    Button(
                        onClick = { viewModel.login(username, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = JagoTeal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "MASUK SEKARANG",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bypass Quick Login Section
            Text(
                text = "PILIH AKTOR UNTUK SIMULASI CEPAT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Actor Lists
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 1. NASABAH (Pemodal / Investor)
                QuickActorCard(
                    title = "Nasabah Pemodal (Investor)",
                    username = "nasabah",
                    password = "nasabah123",
                    desc = "Cari UMKM aktif (Page 1), tinjau prospektus (Page 2), setujui Akad Ijab Qabul & investasi nominal.",
                    icon = Icons.Filled.TrendingUp,
                    accentColor = JagoTeal,
                    onClick = { viewModel.login("nasabah", "nasabah123") }
                )

                // 2. PENERBIT (UMKM)
                QuickActorCard(
                    title = "Penerbit UMKM (Owner)",
                    username = "penerbit",
                    password = "penerbit123",
                    desc = "Ajukan proposal pendanaan modal baru (Page 3), lengkapi legalitas (NIB/NPWP), & pantau status audit.",
                    icon = Icons.Filled.Storefront,
                    accentColor = JagoPurple,
                    onClick = { viewModel.login("penerbit", "penerbit123") }
                )

                // 3. ADMIN (DPS / Syariah Auditor)
                QuickActorCard(
                    title = "Dewan Pengawas Syariah (DPS)",
                    username = "admin",
                    password = "admin123",
                    desc = "Audit kesesuaian syariah proposal (Page 4), verifikasi ketiadaan gharar/riba, & rilis opini syariah digital.",
                    icon = Icons.Filled.Gavel,
                    accentColor = JagoGold,
                    onClick = { viewModel.login("admin", "admin123") }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun QuickActorCard(
    title: String,
    username: String,
    password: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("quick_actor_$username"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = desc,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "User: $username",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Pass: $password",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun QuickProfileDialog(
    viewModel: JagoViewModel,
    onDismiss: () -> Unit
) {
    val loggedInUser by viewModel.loggedInUser.collectAsState()
    val loggedInName by viewModel.loggedInName.collectAsState()
    val loggedInRoleName by viewModel.loggedInRoleName.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = JagoPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Informasi Akun Aktif",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // User Bio Header
                Surface(
                    color = JagoPurple.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, JagoPurple.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = loggedInName.ifEmpty { "Pengguna Jago" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JagoPurple
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = loggedInRoleName.ifEmpty { "Member Aktif" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }
                }

                Divider(color = Color(0xFFF1F5F9))

                // Detail Profil based on Role
                when (loggedInUser) {
                    "admin" -> {
                        // KH. Ahmad Syarifuddin (DPS)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailRowHelper(label = "Jabatan", value = "Ketua Dewan Pengawas Syariah (DPS)")
                            DetailRowHelper(label = "Instansi Afiliasi", value = "Dewan Syariah Nasional - MUI (DSN-MUI)")
                            DetailRowHelper(label = "No. Registrasi Anggota", value = "DSN-MUI/A-2019-8971")
                            DetailRowHelper(label = "Sertifikat", value = "Auditor Syariah Utama Bersertifikasi")
                            DetailRowHelper(label = "Alamat Kantor", value = "Gedung MUI, Jl. Proklamasi No. 51, Jakarta Pusat")
                        }
                    }
                    "penerbit" -> {
                        // Siti Khadijah (UMKM)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailRowHelper(label = "Nama Pemilik", value = "Siti Khadijah")
                            DetailRowHelper(label = "KTP Pemilik", value = "3273100412850002")
                            DetailRowHelper(label = "Nama Perusahaan", value = "PT Kuliner Barokah Nusantara")
                            DetailRowHelper(label = "Merek Usaha", value = "Ayam Bakar Sambal Korek Barokah")
                            DetailRowHelper(label = "NIB (Nomor Induk Berusaha)", value = "912030491024")
                            DetailRowHelper(label = "NPWP Badan", value = "42.102.391.2-013.000")
                            DetailRowHelper(label = "Lokasi Usaha", value = "Ruko Permata Hijau Blok C No. 5, Jakarta Barat")
                        }
                    }
                    else -> {
                        // Nasabah (Rifan Ashir)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailRowHelper(label = "Nama Lengkap", value = "Rifan Ashir")
                            DetailRowHelper(label = "No KTP (NIK)", value = "3273100204980003")
                            DetailRowHelper(label = "Alamat Rumah", value = "Jl. Diponegoro No. 14, Citarum, Bandung")
                            DetailRowHelper(label = "Alamat Email", value = "rifanashir34@gmail.com")
                            DetailRowHelper(label = "Pekerjaan", value = "Senior IT Specialist")
                            DetailRowHelper(label = "Sertifikasi", value = "Pemodal Retail Syariah (Active)")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Logout Button inside dialog
                Button(
                    onClick = {
                        viewModel.logout()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDE8E8)),
                    border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dialog_logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Keluar",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LOGOUT (KELUAR AKUN)",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = JagoTeal, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun DetailRowHelper(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 11.sp, color = Color(0xFF334155), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TableLaporanKeuangan(viewModel: JagoViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9))
                .padding(6.dp)
        ) {
            Text("Pos Laporan Keuangan", modifier = Modifier.weight(1.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Text("Nilai Pra-Audit", modifier = Modifier.weight(1f), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, textAlign = TextAlign.End)
        }
        
        FinancialRow(label = "Aset Tetap (Tanah/Peralatan)", value = "Rp185.000.000", isBold = false)
        FinancialRow(label = "Kas & Setara Kas", value = "Rp45.000.000", isBold = false)
        FinancialRow(label = "Total Aset Lancar", value = "Rp62.000.000", isBold = false)
        FinancialRow(label = "Proyeksi Omset Bulanan", value = "Rp35.000.000", isBold = true)
        FinancialRow(label = "Beban Pokok Penjualan", value = "Rp15.000.000", isBold = false)
        FinancialRow(label = "Rencana Laba Bersih/Bulan", value = "Rp20.000.000", isBold = true)
        FinancialRow(label = "Rasio Utang Usaha (Leverage)", value = "0.0 (Bebas Riba)", isBold = false, isValueColorGreen = true)
    }
}

@Composable
fun FinancialRow(label: String, value: String, isBold: Boolean, isValueColorGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.DarkGray,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = value,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isValueColorGreen) SuccessGreen else Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DpsMentoringScreen(viewModel: JagoViewModel) {
    val mentoringSessions by viewModel.mentoringSessions.collectAsState()
    
    var umkmName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var successScheduledMessage by remember { mutableStateOf<String?>(null) }
    
    // Auto-prefill today's / tomorrow's date
    LaunchedEffect(showAddDialog) {
        if (showAddDialog) {
            date = "20 Jul 2026"
            time = "10:00"
            umkmName = "Ayam Bakar Sambal Korek Barokah"
            topic = "Tata Kelola Manajemen Risiko Syariah"
            material = "Materi bimbingan mengenai penyusunan mitigasi risiko operasional, pengelolaan akad penjaminan modal, dan regulasi transparansi."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = JagoPurple),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Pendampingan & Bimbingan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sebagai Regulator Syariah, DPS aktif memberikan konsultasi kepatuhan tata kelola syariah demi kemaslahatan dan pertumbuhan berkelanjutan mitra UMKM.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 15.sp
                )
            }
        }

        // Action Trigger Button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("schedule_consultation_button"),
            colors = ButtonDefaults.buttonColors(containerColor = JagoTeal),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Jadwalkan Konsultasi Baru", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        // Notification Banner
        if (successScheduledMessage != null) {
            Surface(
                color = Color(0xFFECFDF5),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Konsultasi Berhasil Dijadwalkan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        Text(successScheduledMessage ?: "", fontSize = 10.sp, color = Color(0xFF047857))
                    }
                    IconButton(onClick = { successScheduledMessage = null }) {
                        Icon(Icons.Filled.Close, null, tint = Color(0xFF065F46), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Section Title
        Text(
            text = "Jadwal Bimbingan & Konsultasi Aktif (${mentoringSessions.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (mentoringSessions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.EventBusy, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Belum Ada Jadwal Konsultasi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                mentoringSessions.forEach { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Storefront, null, tint = JagoPurple, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = session.umkmName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Surface(
                                    color = JagoTeal.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = session.status,
                                        color = JagoTeal,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text("Tanggal Pelaksanaan", fontSize = 9.sp, color = Color.Gray)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.CalendarMonth, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(session.date, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                    }
                                }
                                Column {
                                    Text("Waktu", fontSize = 9.sp, color = Color.Gray)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Schedule, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${session.time} WIB", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Topik Pendampingan", fontSize = 9.sp, color = Color.Gray)
                            Text(session.topic, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JagoGold)

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Materi / Pembahasan", fontSize = 9.sp, color = Color.Gray)
                            Text(
                                session.material,
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }

    // Scheduling Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, null, tint = JagoTeal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Jadwalkan Bimbingan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lengkapi informasi bimbingan dan pendampingan kepatuhan syariah bagi Penerbit UMKM berikut:", fontSize = 11.sp, color = Color.Gray)
                    
                    OutlinedTextField(
                        value = umkmName,
                        onValueChange = { umkmName = it },
                        label = { Text("Nama UMKM / Penerbit", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Tanggal", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Jam", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topik Bimbingan", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = material,
                        onValueChange = { material = it },
                        label = { Text("Materi / Penjelasan", fontSize = 11.sp) },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (umkmName.isNotEmpty() && topic.isNotEmpty() && material.isNotEmpty()) {
                            viewModel.scheduleMentoring(umkmName, date, time, topic, material)
                            successScheduledMessage = "Bimbingan untuk \"$umkmName\" tentang \"$topic\" berhasil dirilis."
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JagoTeal)
                ) {
                    Text("Jadwalkan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal", color = Color.Gray, fontSize = 12.sp)
                }
            }
        )
    }
}
