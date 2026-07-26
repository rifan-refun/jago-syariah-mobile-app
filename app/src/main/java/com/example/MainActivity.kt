package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.JagoGold
import com.example.ui.theme.JagoPurple
import com.example.ui.theme.JagoTeal
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.JagoViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: JagoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContentHost(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContentHost(viewModel: JagoViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentTab by viewModel.currentSelectedTab.collectAsState()
    val appMode by viewModel.appMode.collectAsState()
    val selectedCampaign by viewModel.selectedCampaign.collectAsState()
    val checkoutState by viewModel.checkoutState.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    if (!isLoggedIn) {
        LoginScreen(viewModel = viewModel)
    } else if (checkoutState == "CHECKOUT") {
        // Render checkout multi-step flow as a full screen override if active
        CheckoutScreen(viewModel = viewModel)
    } else if (selectedCampaign != null) {
        // Render detailed Campaign view (Page 2) as an override if a campaign is selected
        DetailScreen(viewModel = viewModel, onBack = { viewModel.clearCampaignSelection() })
    } else {
        // Standard View Hub with bottom navigation
        Scaffold(
            topBar = {
                AppHeader(
                    title = when {
                        loggedInUser == "penerbit" -> when (currentTab) {
                            0 -> "Dasbor Penerbit UMKM"
                            1 -> "Ajukan Proposal"
                            else -> "Profil Bisnis & Kantong"
                        }
                        loggedInUser == "admin" -> when (currentTab) {
                            0 -> "Dasbor Opini & Audit DPS"
                            1 -> "Konsultasi & Pendampingan"
                            else -> "Profil Dewan Pengawas"
                        }
                        else -> when (currentTab) {
                            0 -> "Beranda Jago Modal"
                            1 -> "Jago Modal Aktif"
                            2 -> "Portofolio Saya"
                            else -> "Profil Investor"
                        }
                    },
                    trailingActions = {
                        var showProfileDialog by remember { mutableStateOf(false) }

                        IconButton(
                            onClick = { showProfileDialog = true },
                            modifier = Modifier.testTag("profile_quick_action")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profil Pengguna",
                                tint = Color.White
                            )
                        }

                        if (showProfileDialog) {
                            QuickProfileDialog(
                                viewModel = viewModel,
                                onDismiss = { showProfileDialog = false }
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Unread notification badge at top right
                        IconButton(
                            onClick = {},
                            modifier = Modifier.testTag("notification_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = JagoGold,
                                        contentColor = Color.White
                                    ) {
                                        Text("3", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    tonalElevation = 0.dp,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)), // slate-100 border on top
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding() // proper inset safe padding
                        .testTag("bottom_nav_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabs = if (loggedInUser == "penerbit") {
                            listOf(
                                Triple("Beranda", Icons.Filled.Storefront, 0),
                                Triple("Ajukan", Icons.Filled.NoteAdd, 1),
                                Triple("Profil", Icons.Filled.Business, 3)
                            )
                        } else if (loggedInUser == "admin") {
                            listOf(
                                Triple("Audit", Icons.Filled.VerifiedUser, 0),
                                Triple("Bimbingan", Icons.Filled.MenuBook, 1),
                                Triple("Profil", Icons.Filled.Person, 2)
                            )
                        } else {
                            listOf(
                                Triple("Beranda", Icons.Filled.Home, 0),
                                Triple("Jago Modal", Icons.Filled.Search, 1),
                                Triple("Portofolio", Icons.Filled.ShowChart, 2),
                                Triple("Profil", Icons.Filled.Person, 3)
                            )
                        }

                        tabs.forEach { (label, icon, index) ->
                            val isSelected = currentTab == index
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectTab(index) }
                                    .testTag("bottom_tab_item_$index")
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Little indicator pill/bar on top matching HTML
                                Box(
                                    modifier = Modifier
                                        .size(width = 24.dp, height = 3.dp)
                                        .background(
                                            color = if (isSelected) JagoTeal else Color.Transparent,
                                            shape = RoundedCornerShape(1.5.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) JagoTeal else Color(0xFF94A3B8), // slate-400
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) JagoTeal else Color(0xFF64748B) // slate-500
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (loggedInUser == "penerbit") {
                    when (currentTab) {
                        0 -> UmkmDashboardScreen(viewModel = viewModel)
                        1 -> UmkmOnboardingScreen(viewModel = viewModel)
                        3 -> ProfileAndUmkmTab(viewModel = viewModel)
                        else -> UmkmDashboardScreen(viewModel = viewModel)
                    }
                } else if (loggedInUser == "admin") {
                    when (currentTab) {
                        0 -> DpsDashboardScreen(viewModel = viewModel)
                        1 -> DpsMentoringScreen(viewModel = viewModel)
                        2 -> ProfileAndUmkmTab(viewModel = viewModel)
                        else -> DpsDashboardScreen(viewModel = viewModel)
                    }
                } else {
                    when (currentTab) {
                        0 -> BerandaScreen(viewModel = viewModel)
                        1 -> ActiveCampaignsListTab(viewModel = viewModel)
                        2 -> PortfolioScreen(viewModel = viewModel)
                        3 -> ProfileAndUmkmTab(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

// Compact active campaign lists shortcut view to provide extra browse ability (represented as Tab 1: "Jago Modal Aktif" or "Active")
@Composable
fun ActiveCampaignsListTab(viewModel: JagoViewModel) {
    val campaigns by viewModel.campaigns.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JagoTeal)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        text = "Jago Modal Aktif",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Semua proyek industri riil syariah terbuka untuk pendanaan publik masa tenang.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                // Short card helper
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFC)),
                    border = BorderStroke(1.dp, JagoTeal.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Security, "syariah", tint = JagoTeal, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dana Terproteksi Kemitraan Syariah", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JagoTeal)
                            Text("Diawasi ketat oleh OJK dan Dewan Syariah MUI.", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Daftar Penawaran Emiten UMKM")
        }

        items(campaigns) { campaign ->
            CampaignCard(campaign = campaign, onClick = { viewModel.selectCampaign(campaign) })
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// Detail row helper for the profile cards
@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
    }
}

// Integrated Profile + Switch block with direct entry to PAGE 5: "Formulir Pengajuan Kampanye (UMKM View - IF-05)"
@Composable
fun ProfileAndUmkmTab(viewModel: JagoViewModel) {
    val proposalStep by viewModel.proposalStep.collectAsState()
    val dpsDashboardActive by viewModel.dpsDashboardActive.collectAsState()
    val loggedInName by viewModel.loggedInName.collectAsState()
    val loggedInRoleName by viewModel.loggedInRoleName.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    // If DPS dashboard is active, render Page 4
    if (dpsDashboardActive) {
        DpsDashboardScreen(viewModel = viewModel)
    } else if (loggedInUser == "penerbit" && proposalStep > 0 && proposalStep <= 5) {
        // If the proposal flow is actively running, render step tracker of Page 5
        UmkmOnboardingScreen(viewModel = viewModel)
    } else {
        // Render Profile Home layout with simulated user data and entry to UMKM space
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FB))
                .verticalScroll(rememberScrollState())
        ) {
            // Bio header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JagoPurple)
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = loggedInName.ifEmpty { "Penguji Jago Modal" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${loggedInRoleName.ifEmpty { "Premium Jago Syariah Member" }} • Jakarta",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            // Wallet details & Specific Profile Content
            Column(modifier = Modifier.padding(16.dp)) {
                
                // --- CARD 1: ROLE SPECIFIC DATA DIRI (Requested Revision) ---
                Text(
                    text = "Informasi Profil Resmi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                when (loggedInUser) {
                    "admin" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Gavel, null, tint = JagoGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("PROFIL AUDITOR SYARIAH", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = JagoGold, letterSpacing = 1.sp)
                                }
                                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))
                                DetailRow(label = "Nama Lengkap", value = "KH. Ahmad Syarifuddin, M.A.")
                                DetailRow(label = "Jabatan Utama", value = "Ketua Komite Pengawas Syariah Jago Modal")
                                DetailRow(label = "Instansi Afiliasi", value = "Dewan Syariah Nasional - Majelis Ulama Indonesia (DSN-MUI)")
                                DetailRow(label = "Nomor Anggota DSN-MUI", value = "DSN-MUI/A-2019-8971")
                                DetailRow(label = "Sertifikat Kompetensi", value = "Auditor Syariah Utama Bersertifikasi (Cert. No. MUI-DSN/2025-992)")
                                DetailRow(label = "Alamat Kantor", value = "Gedung MUI, Jl. Proklamasi No. 51, Menteng, Jakarta Pusat")
                            }
                        }
                    }
                    "penerbit" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Storefront, null, tint = JagoPurple, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("PROFIL PENERBIT & BADAN USAHA (UMKM)", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = JagoPurple, letterSpacing = 1.sp)
                                }
                                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))
                                Text("DATA PRIBADI", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                                DetailRow(label = "Nama Pemilik", value = "Siti Khadijah")
                                DetailRow(label = "NIK KTP Pemilik", value = "3273100412850002")
                                DetailRow(label = "Alamat Rumah", value = "Jl. Barokah Raya No. 42, Kebayoran Baru, Jakarta Selatan")
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("DATA BADAN USAHA", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                                DetailRow(label = "Nama Perusahaan", value = "PT Kuliner Barokah Nusantara")
                                DetailRow(label = "Merek Usaha", value = "Ayam Bakar Sambal Korek Barokah")
                                DetailRow(label = "Nomor Induk Berusaha (NIB)", value = "912030491024")
                                DetailRow(label = "NPWP Badan Usaha", value = "42.102.391.2-013.000")
                                DetailRow(label = "Alamat Kantor & Outlet", value = "Ruko Permata Hijau Blok C No. 5, Kebayoran Lama, Jakarta Barat")
                                DetailRow(label = "Sektor Usaha", value = "Kuliner F&B (Sektor Riil)")
                            }
                        }
                    }
                    else -> { // nasabah (Investor)
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.AccountCircle, null, tint = JagoTeal, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("PROFIL NASABAH PEMODAL (INVESTOR)", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = JagoTeal, letterSpacing = 1.sp)
                                }
                                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))
                                DetailRow(label = "Nama Lengkap", value = "Rifan Ashir")
                                DetailRow(label = "Nomor KTP (NIK)", value = "3273100204980003")
                                DetailRow(label = "Alamat Rumah Tinggal", value = "Jl. Diponegoro No. 14, Citarum, Bandung, Jawa Barat")
                                DetailRow(label = "Email", value = "rifanashir34@gmail.com")
                                DetailRow(label = "Pekerjaan", value = "Senior IT Specialist / Consultant")
                                DetailRow(label = "Sumber Penghasilan Utama", value = "Gaji Pekerjaan Tetap")
                                DetailRow(label = "Sertifikasi Kompetensi", value = "Sertifikasi Kompetensi Pemodal Retail Syariah (Active)")
                            }
                        }
                    }
                }

                Text(
                    text = "Layanan Ekosistem Bank Jago Syariah",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Kantong Jago Syariah Yang Terhubung", fontSize = 11.sp, color = Color.Gray)
                            Surface(color = JagoTeal.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                                Text("AKTIF", color = JagoTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val wallets by viewModel.wallets.collectAsState()
                        wallets.forEach { w ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.AccountBalanceWallet, null, tint = JagoTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(w.name, fontSize = 12.sp, color = Color.DarkGray)
                                }
                                Text(viewModel.formatCurrency(w.balance), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JagoTeal)
                            }
                        }
                    }
                }

                // --- CONDITIONALLY SHOW ACTION CARDS BASED ON USER ROLE ---
                // Only "penerbit" gets to view and access Page 5: "Ajukan Proposal"
                if (loggedInUser == "penerbit") {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Pecah Usaha / Kemitraan UMKM?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setProposalStep(1) } // Start Page 5 Flow!
                            .testTag("proposal_onboarding_trigger"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFAF7FE0).copy(alpha = 0.1f)), // elegant JagoPurple subtheme invitation
                        border = BorderStroke(2.dp, JagoPurple)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Storefront, "UMKM", tint = JagoPurple, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Beralih ke Panel Pengajuan UMKM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JagoPurple)
                                        Text("Ajukan modal pembiayaan syariah s.d 500 Juta rupiah.", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                                Icon(Icons.Filled.ChevronRight, "Go", tint = JagoPurple)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Layanan pembiayaan syariah terintegrasi khusus badan usaha berdokumen lengkap (NIB/NPWP). Dapatkan nisbah bagi hasil yang adil untuk kemandirian finansial Anda.",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                // Only "admin" (DPS) gets to view and access Page 4: "Dasbor Opini & Penelaahan"
                if (loggedInUser == "admin") {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Dewan Pengawas Syariah (DPS) & Audit?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setDpsDashboardActive(true) } // Start Page 4 Flow!
                            .testTag("dps_dashboard_trigger"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)), // neutral slate
                        border = BorderStroke(2.dp, JagoGold)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.VerifiedUser, "DPS", tint = JagoGold, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Dasbor Opini & Penelaahan Syariah", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                        Text("Tinjau kepatuhan syariah proposal UMKM masuk.", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                                Icon(Icons.Filled.ChevronRight, "Go", tint = JagoGold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Fasilitas penelaahan proposal penggalangan dana aktif untuk memastikan kepatuhan bebas gharar, maysir, dan riba secara formal dengan rilis opini syariah digital.",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDE8E8)),
                    border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Keluar",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KELUAR DARI AKUN (LOGOUT)",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
