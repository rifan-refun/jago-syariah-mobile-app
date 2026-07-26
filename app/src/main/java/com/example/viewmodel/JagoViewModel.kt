package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.Campaign
import com.example.model.Investment
import com.example.model.PayoutLog
import com.example.model.Proposal
import com.example.model.ShariaWallet
import com.example.model.UmkmTransaction
import com.example.model.MentoringSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JagoViewModel : ViewModel() {

    // Simulated Authentication State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loggedInUser = MutableStateFlow<String?>(null) // "admin", "investor", "nasabah"
    val loggedInUser: StateFlow<String?> = _loggedInUser.asStateFlow()

    private val _loggedInName = MutableStateFlow("")
    val loggedInName: StateFlow<String> = _loggedInName.asStateFlow()

    private val _loggedInRoleName = MutableStateFlow("")
    val loggedInRoleName: StateFlow<String> = _loggedInRoleName.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Bottom Navigation tab identifier: 0 = Beranda, 1 = Jago Modal Active, 2 = Portofolio, 3 = Profil/UMKM Mode
    private val _currentSelectedTab = MutableStateFlow(0)
    val currentSelectedTab: StateFlow<Int> = _currentSelectedTab.asStateFlow()

    // Mode Switch: "investor" or "umkm"
    private val _appMode = MutableStateFlow("investor") // "investor" or "umkm"
    val appMode: StateFlow<String> = _appMode.asStateFlow()

    // Detailed Navigation: if not null, we render Page 2 (Detail Kampanye) instead of the list
    private val _selectedCampaign = MutableStateFlow<Campaign?>(null)
    val selectedCampaign: StateFlow<Campaign?> = _selectedCampaign.asStateFlow()

    // Category Filter in Beranda ("Semua", "Musyarakah", "Mudharabah", "Sektor Kuliner", "Sektor Fashion")
    private val _filterCategory = MutableStateFlow("Semua")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    // Jago Bank "Kantong Syariah" Wallet list
    private val _wallets = MutableStateFlow(
        listOf(
            ShariaWallet("Kantong Utama Syariah", 5500000.0, "JAGO-10234567"),
            ShariaWallet("Kantong Investasi Berkah", 2500000.0, "JAGO-10234599"),
            ShariaWallet("Kantong Darurat Syariah", 350000.0, "JAGO-10234511")
        )
    )
    val wallets: StateFlow<List<ShariaWallet>> = _wallets.asStateFlow()

    // Selected Wallet during checkout
    private val _selectedWallet = MutableStateFlow<ShariaWallet>(_wallets.value[1])
    val selectedWallet: StateFlow<ShariaWallet> = _selectedWallet.asStateFlow()

    // List of Active campaigns
    private val _campaigns = MutableStateFlow(
        listOf(
            Campaign(
                id = "KMP-001",
                title = "Kopi Kenangan Senopati",
                sector = "Sektor Kuliner",
                type = "Musyarakah",
                targetAmount = 500000000.0,
                collectedAmount = 375000000.0,
                nisbahInvestor = 0.65,
                nisbahUmkm = 0.35,
                tenorMonths = 12,
                countdownDays = 12,
                description = "Ekspansi kedai kopi susu kekinian di wilayah strategis Senopati dengan kapasitas duduk bertambah 100%. Pembagian hasil dihitung dari omset bulanan usaha.",
                backersCount = 142
            ),
            Campaign(
                id = "KMP-002",
                title = "Batik Solihin Premium",
                sector = "Sektor Fashion",
                type = "Mudharabah",
                targetAmount = 300000000.0,
                collectedAmount = 180000000.0,
                nisbahInvestor = 0.60,
                nisbahUmkm = 0.40,
                tenorMonths = 8,
                countdownDays = 8,
                description = "Modal kerja untuk pengumpulan bahan sutra premium musiman dan pembuatan corak batik tulis eksklusif menyambut Idul Fitri.",
                backersCount = 78
            ),
            Campaign(
                id = "KMP-003",
                title = "Martabak Jago Rasa",
                sector = "Sektor Kuliner",
                type = "Musyarakah",
                targetAmount = 150000000.0,
                collectedAmount = 142500000.0,
                nisbahInvestor = 0.70,
                nisbahUmkm = 0.30,
                tenorMonths = 6,
                countdownDays = 4,
                description = "Pembukaan outlet martabak manis dan telur premium baru dengan sistem kemitraan di Kota Bandung.",
                backersCount = 59
            ),
            Campaign(
                id = "KMP-004",
                title = "Hijab Syar'i Anggun",
                sector = "Sektor Fashion",
                type = "Mudharabah",
                targetAmount = 200000000.0,
                collectedAmount = 50000000.0,
                nisbahInvestor = 0.55,
                nisbahUmkm = 0.45,
                tenorMonths = 10,
                countdownDays = 25,
                description = "Produksi massal hijab syar'i berbahan voal premium anti kusut dengan pangsa pasar ekspor ke Malaysia dan Brunei.",
                backersCount = 31
            ),
            Campaign(
                id = "KMP-005",
                title = "Ayam Bakar Sambal Korek",
                sector = "Sektor Kuliner",
                type = "Musyarakah",
                targetAmount = 120000000.0,
                collectedAmount = 110000000.0,
                nisbahInvestor = 0.60,
                nisbahUmkm = 0.40,
                tenorMonths = 12,
                countdownDays = 2,
                description = "Digitalisasi rantai logistik pasokan ayam hidup dari peternak langsung untuk menyajikan harga ayam bakar bersaing.",
                backersCount = 94
            )
        )
    )
    val campaigns: StateFlow<List<Campaign>> = _campaigns.asStateFlow()

    // Portofolio List
    private val _investments = MutableStateFlow<List<Investment>>(
        listOf(
            Investment(
                id = "INV-091",
                campaign = Campaign(
                    id = "KMP-101",
                    title = "Bakso Boedjang Bandung",
                    sector = "Sektor Kuliner",
                    type = "Musyarakah",
                    targetAmount = 400000000.0,
                    collectedAmount = 400000000.0,
                    nisbahInvestor = 0.65,
                    nisbahUmkm = 0.35,
                    tenorMonths = 12,
                    countdownDays = 0,
                    description = "Outlet Bakso Boedjang.",
                    backersCount = 205
                ),
                capital = 5000000.0,
                currentMonth = 4,
                totalPayoutsReceived = 812500.0,
                lastPayoutAmount = 203125.0,
                payoutLogs = listOf(
                    PayoutLog(1, "12 Feb 2026", 203125.0),
                    PayoutLog(2, "12 Mar 2026", 203125.0),
                    PayoutLog(3, "12 Apr 2026", 203125.0),
                    PayoutLog(4, "12 Mei 2026", 203125.0)
                )
            ),
            Investment(
                id = "INV-092",
                campaign = Campaign(
                    id = "KMP-102",
                    title = "Gamis Kidz Al-Fatih",
                    sector = "Sektor Fashion",
                    type = "Mudharabah",
                    targetAmount = 150000000.0,
                    collectedAmount = 150000000.0,
                    nisbahInvestor = 0.60,
                    nisbahUmkm = 0.40,
                    tenorMonths = 6,
                    countdownDays = 0,
                    description = "Gamis anak.",
                    backersCount = 85
                ),
                capital = 2000000.0,
                currentMonth = 6, // Completed!
                totalPayoutsReceived = 360000.0,
                lastPayoutAmount = 60000.0,
                payoutLogs = listOf(
                    PayoutLog(1, "05 Jan 2026", 60000.0),
                    PayoutLog(2, "05 Feb 2026", 60000.0),
                    PayoutLog(3, "05 Mar 2026", 60000.0),
                    PayoutLog(4, "05 Apr 2026", 60000.0),
                    PayoutLog(5, "05 Mei 2026", 60000.0),
                    PayoutLog(6, "05 Jun 2026", 60000.0)
                )
            ),
            Investment(
                id = "INV-093",
                campaign = Campaign(
                    id = "KMP-103",
                    title = "Sate Maranggi Purwakarta",
                    sector = "Sektor Kuliner",
                    type = "Mudharabah",
                    targetAmount = 250000000.0,
                    collectedAmount = 140000000.0, // Failed to reach target!
                    nisbahInvestor = 0.60,
                    nisbahUmkm = 0.40,
                    tenorMonths = 6,
                    countdownDays = 0,
                    description = "Pelebaran outlet sate maranggi legendaris di rest area Tol Cipularang.",
                    backersCount = 22
                ),
                capital = 1500000.0,
                currentMonth = 0,
                totalPayoutsReceived = 0.0,
                lastPayoutAmount = 0.0,
                payoutLogs = emptyList(),
                status = "GAGAL_BELUM_REFUND",
                isFailedAndRefunded = false
            )
        )
    )
    val investments: StateFlow<List<Investment>> = _investments.asStateFlow()

    fun refundInvestment(investmentId: String): Boolean {
        val inv = _investments.value.find { it.id == investmentId } ?: return false
        if (inv.status != "GAGAL_BELUM_REFUND") return false

        // Refund capital back to the first wallet (Kantong Utama Syariah)
        _wallets.value = _wallets.value.mapIndexed { idx, wallet ->
            if (idx == 0) {
                wallet.copy(balance = wallet.balance + inv.capital)
            } else wallet
        }

        // Update investments list status to REFUNDED
        _investments.value = _investments.value.map {
            if (it.id == investmentId) {
                it.copy(
                    status = "GAGAL_REFUNDED",
                    isFailedAndRefunded = true
                )
            } else it
        }
        return true
    }

    // Page 2 Simulator Investment Amount (Min 100.000, Multi-step 50.000)
    private val _simulatedAmount = MutableStateFlow(500000.0)
    val simulatedAmount: StateFlow<Double> = _simulatedAmount.asStateFlow()

    // Interactive multi-step Checkout State (PAGE 3)
    // Mode transitions: "NONE", "STEP_1", "STEP_2", "STEP_3", "SUCCESS"
    private val _checkoutState = MutableStateFlow("NONE")
    val checkoutState: StateFlow<String> = _checkoutState.asStateFlow()

    private val _checkoutStepNum = MutableStateFlow(1) // 1, 2, 3
    val checkoutStepNum: StateFlow<Int> = _checkoutStepNum.asStateFlow()

    private val _isAgreedToAkad = MutableStateFlow(false)
    val isAgreedToAkad: StateFlow<Boolean> = _isAgreedToAkad.asStateFlow()

    private val _isContractScrolledBottom = MutableStateFlow(false)
    val isContractScrolledBottom: StateFlow<Boolean> = _isContractScrolledBottom.asStateFlow()

    private val _pinNumber = MutableStateFlow("")
    val pinNumber: StateFlow<String> = _pinNumber.asStateFlow()

    private val _checkoutError = MutableStateFlow<String?>(null)
    val checkoutError: StateFlow<String?> = _checkoutError.asStateFlow()

    // Interactive UMKM Proposal Submission Form State (PAGE 5)
    private val _proposalStep = MutableStateFlow(1) // 1, 2, 3, 4, or 5 (means Application Application Tracking Dashboard)
    val proposalStep: StateFlow<Int> = _proposalStep.asStateFlow()

    // Form inputs for proposal
    val proposalBusinessName = MutableStateFlow("")
    val proposalSector = MutableStateFlow("Sektor Kuliner")
    val proposalAddress = MutableStateFlow("")
    val proposalDescription = MutableStateFlow("")
    val proposalCapitalTarget = MutableStateFlow("75000000")
    val proposalContractType = MutableStateFlow("Musyarakah")
    val proposalTenureMonths = MutableStateFlow(12f) // slider 3-24 months

    // Document validation uploads
    private val _proposalNibUploaded = MutableStateFlow(false)
    val proposalNibUploaded: StateFlow<Boolean> = _proposalNibUploaded.asStateFlow()

    private val _proposalNpwpUploaded = MutableStateFlow(false)
    val proposalNpwpUploaded: StateFlow<Boolean> = _proposalNpwpUploaded.asStateFlow()

    private val _proposalFinancialUploaded = MutableStateFlow(false)
    val proposalFinancialUploaded: StateFlow<Boolean> = _proposalFinancialUploaded.asStateFlow()

    // State for DPS Dashboard view
    private val _dpsDashboardActive = MutableStateFlow(false)
    val dpsDashboardActive: StateFlow<Boolean> = _dpsDashboardActive.asStateFlow()

    // Currently submitted proposal list
    // Currently submitted proposal list
    private val _submittedProposals = MutableStateFlow<List<Proposal>>(
        listOf(
            Proposal(
                id = "PRP-203",
                businessName = "Butik Hijab Cantik",
                sector = "Sektor Fashion",
                address = "Jl. Diponegoro No 14, Bandung",
                description = "Ekspansi galeri butik offline untuk koleksi busana muslim & hijab sutra syar'i.",
                capitalTarget = 150000000.0,
                contractType = "Musyarakah",
                tenureMonths = 12,
                status = "Menunggu Review",
                submittedAt = "04 Jul 2026"
            ),
            Proposal(
                id = "PRP-204",
                businessName = "Katering Sehat Organik",
                sector = "Sektor Kuliner",
                address = "Ruko Permata Hijau Blok C3, Jakarta",
                description = "Penyediaan menu katering harian berbahan dasar 100% organik bersertifikasi halal.",
                capitalTarget = 75000000.0,
                contractType = "Mudharabah",
                tenureMonths = 6,
                status = "Menunggu Review",
                submittedAt = "05 Jul 2026"
            ),
            Proposal(
                id = "PRP-101",
                businessName = "Kedai Kopi Barokah",
                sector = "Sektor Kuliner",
                address = "Jl. Margonda Raya No. 10, Depok",
                description = "Ekspansi kedai kopi susu gula aren dengan gerobak listrik ramah lingkungan.",
                capitalTarget = 50000000.0,
                contractType = "Musyarakah",
                tenureMonths = 6,
                status = "Ditolak (DPS - Rasio Leverage Terlalu Tinggi)",
                submittedAt = "12 Apr 2026"
            ),
            Proposal(
                id = "PRP-202",
                businessName = "Warung Sate Barokah",
                sector = "Sektor Kuliner",
                address = "Jl. Sudirman No 42, Karawang",
                description = "Modernisasi panggangan sate hemat asap dan ekosistem rantai sate higienis.",
                capitalTarget = 85000000.0,
                contractType = "Mudharabah",
                tenureMonths = 12,
                status = "Disetujui/Aktif",
                submittedAt = "10 Mei 2026"
            )
        )
    )
    val submittedProposals: StateFlow<List<Proposal>> = _submittedProposals.asStateFlow()

    // DPS Mentoring Sessions
    private val _mentoringSessions = MutableStateFlow<List<MentoringSession>>(
        listOf(
            MentoringSession(
                id = "MTR-001",
                umkmName = "Butik Hijab Cantik",
                date = "15 Jul 2026",
                time = "10:00",
                topic = "Penyusunan Laporan Keuangan Syariah",
                material = "Materi Modul Penyusunan Posisi Keuangan & Bagi Hasil sesuai Standar Akuntansi Syariah (PSAK 101/105)."
            ),
            MentoringSession(
                id = "MTR-002",
                umkmName = "Katering Sehat Organik",
                date = "18 Jul 2026",
                time = "14:00",
                topic = "Implementasi Fatwa DSN-MUI tentang Akad Mudharabah",
                material = "Panduan praktis pembagian porsi nisbah keuntungan riil & mekanisme penanganan kerugian usaha."
            )
        )
    )
    val mentoringSessions: StateFlow<List<MentoringSession>> = _mentoringSessions.asStateFlow()

    fun scheduleMentoring(umkmName: String, date: String, time: String, topic: String, material: String) {
        val newSession = MentoringSession(
            id = "MTR-${System.currentTimeMillis() % 1000}",
            umkmName = umkmName,
            date = date,
            time = time,
            topic = topic,
            material = material
        )
        _mentoringSessions.value = listOf(newSession) + _mentoringSessions.value
    }

    // UMKM (Penerbit) specific states for dashboard controls
    private val _umkmWallets = MutableStateFlow(
        listOf(
            ShariaWallet("Kantong Operasional Bisnis", 125000000.0, "JAGO-B2938172"),
            ShariaWallet("Kantong Cadangan Bagi Hasil", 24500000.0, "JAGO-B2938199")
        )
    )
    val umkmWallets: StateFlow<List<ShariaWallet>> = _umkmWallets.asStateFlow()

    private val _umkmTransactions = MutableStateFlow<List<UmkmTransaction>>(
        listOf(
            UmkmTransaction(
                id = "TX-RTR-091",
                type = "BAGI_HASIL",
                date = "28 Jun 2026",
                amount = 3500000.0,
                description = "Bagi hasil bulanan periode Juni 2026",
                walletSource = "Kantong Cadangan Bagi Hasil"
            )
        )
    )
    val umkmTransactions: StateFlow<List<UmkmTransaction>> = _umkmTransactions.asStateFlow()

    private val _umkmCampaignRefunded = MutableStateFlow(false)
    val umkmCampaignRefunded: StateFlow<Boolean> = _umkmCampaignRefunded.asStateFlow()

    fun reApplyRejectedProposal(proposal: Proposal) {
        proposalBusinessName.value = proposal.businessName
        proposalSector.value = proposal.sector
        proposalAddress.value = proposal.address
        proposalDescription.value = proposal.description
        proposalCapitalTarget.value = proposal.capitalTarget.toInt().toString()
        proposalContractType.value = proposal.contractType
        proposalTenureMonths.value = proposal.tenureMonths.toFloat()
        
        _proposalNibUploaded.value = true
        _proposalNpwpUploaded.value = true
        _proposalFinancialUploaded.value = true
        
        _proposalStep.value = 1
        _currentSelectedTab.value = 1 // Switch to Ajukan Proposal Tab!
    }

    fun transferReturnToInvestors(amount: Double, walletName: String): Boolean {
        val walletsList = _umkmWallets.value
        val wallet = walletsList.find { it.name == walletName } ?: return false
        if (wallet.balance < amount) return false

        // Deduct from UMKM wallet
        _umkmWallets.value = walletsList.map {
            if (it.name == walletName) {
                it.copy(balance = it.balance - amount)
            } else it
        }

        // Add to transactions
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val dateStr = dateFormat.format(Date())
        val newTx = UmkmTransaction(
            id = "TX-RTR-${System.currentTimeMillis() % 10000}",
            type = "BAGI_HASIL",
            date = dateStr,
            amount = amount,
            description = "Bagi Hasil untuk Investor (Ayam Bakar Sambal Korek)",
            walletSource = walletName
        )
        _umkmTransactions.value = listOf(newTx) + _umkmTransactions.value

        // Increase investor's received payouts in active investments
        _investments.value = _investments.value.map { inv ->
            if (inv.campaign.id == "KMP-005" || inv.campaign.title == "Ayam Bakar Sambal Korek") {
                val updatedLogs = inv.payoutLogs + PayoutLog(
                    monthIndex = inv.payoutLogs.size + 1,
                    date = dateStr,
                    amount = amount / 10.0 // assume a share distribution
                )
                inv.copy(
                    totalPayoutsReceived = inv.totalPayoutsReceived + amount / 10.0,
                    lastPayoutAmount = amount / 10.0,
                    payoutLogs = updatedLogs
                )
            } else inv
        }

        return true
    }

    fun refundCampaignToInvestors(walletName: String): Boolean {
        val walletsList = _umkmWallets.value
        val wallet = walletsList.find { it.name == walletName } ?: return false
        val refundAmount = 110000000.0 // KMP-005 collected amount
        if (wallet.balance < refundAmount) return false

        // Deduct from UMKM wallet
        _umkmWallets.value = walletsList.map {
            if (it.name == walletName) {
                it.copy(balance = it.balance - refundAmount)
            } else it
        }

        // Set refunded status
        _umkmCampaignRefunded.value = true

        // Modify campaigns list: reduce collected amount or mark refunded
        _campaigns.value = _campaigns.value.map { cmp ->
            if (cmp.id == "KMP-005") {
                cmp.copy(collectedAmount = 0.0, description = "KAMPANYE DIBATALKAN & DANA DIREFUND PENUH OLEH PENERBIT.")
            } else cmp
        }

        // Add to transactions
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val dateStr = dateFormat.format(Date())
        val newTx = UmkmTransaction(
            id = "TX-RFD-${System.currentTimeMillis() % 10000}",
            type = "REFUND",
            date = dateStr,
            amount = refundAmount,
            description = "Refund Seluruh Modal Penggalangan Dana ke Investor (Kinerja Buruk)",
            walletSource = walletName
        )
        _umkmTransactions.value = listOf(newTx) + _umkmTransactions.value

        // Mark investor's investment in this campaign as GAGAL_REFUNDED and refund capital back
        _investments.value = _investments.value.map { inv ->
            if (inv.campaign.id == "KMP-005" || inv.campaign.title == "Ayam Bakar Sambal Korek") {
                // Refund capital to investor
                _wallets.value = _wallets.value.mapIndexed { idx, w ->
                    if (idx == 1) { // Kantong Investasi Berkah
                        w.copy(balance = w.balance + inv.capital)
                    } else w
                }
                inv.copy(
                    status = "GAGAL_REFUNDED",
                    isFailedAndRefunded = true
                )
            } else inv
        }

        return true
    }


    // Helper functions
    fun selectTab(tab: Int) {
        _currentSelectedTab.value = tab
        // Reset sub page navigation when clicking main tab
        if (tab != 1) {
            _selectedCampaign.value = null
        }
    }

    fun setAppMode(mode: String) {
        _appMode.value = mode
    }

    fun selectCampaign(campaign: Campaign) {
        _selectedCampaign.value = campaign
        // Reset simulation to recommended default or minimum
        _simulatedAmount.value = 1000000.0
    }

    fun clearCampaignSelection() {
        _selectedCampaign.value = null
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun updateSimulatedAmount(amount: Double) {
        _simulatedAmount.value = amount
    }

    fun startCheckout() {
        _checkoutState.value = "CHECKOUT"
        _checkoutStepNum.value = 1
        _isAgreedToAkad.value = false
        _isContractScrolledBottom.value = false
        _pinNumber.value = ""
        _checkoutError.value = null
    }

    fun cancelCheckout() {
        _checkoutState.value = "NONE"
    }

    fun setCheckoutWallet(wallet: ShariaWallet) {
        _selectedWallet.value = wallet
    }

    fun toggleAkadAgreement(agreed: Boolean) {
        _isAgreedToAkad.value = agreed
    }

    fun toggleContractScrolledBottom(scrolled: Boolean) {
        _isContractScrolledBottom.value = scrolled
    }

    fun simulateScrollToBottom() {
        _isContractScrolledBottom.value = true
    }

    fun resetScrollToBottom() {
        _isContractScrolledBottom.value = false
    }

    fun selectWalletByName(name: String) {
        val w = _wallets.value.find { it.name == name }
        if (w != null) {
            _selectedWallet.value = w
        }
    }

    fun appendPin(char: Char) {
        if (_pinNumber.value.length < 6) {
            _pinNumber.value += char
            _checkoutError.value = null
        }
        if (_pinNumber.value.length == 6) {
            verifyPin()
        }
    }

    fun deletePinChar() {
        if (_pinNumber.value.isNotEmpty()) {
            _pinNumber.value = _pinNumber.value.dropLast(1)
            _checkoutError.value = null
        }
    }

    fun verifyPin() {
        if (_pinNumber.value == "123456") {
            // Success! Process payment and create real Investment asset
            val cost = _simulatedAmount.value
            val currentWallet = _selectedWallet.value
            if (currentWallet.balance < cost) {
                _checkoutError.value = "Saldo Tidak Mencukupi di ${_selectedWallet.value.name} (Saldo: ${formatCurrency(currentWallet.balance)})"
                _pinNumber.value = ""
                return
            }

            // Deduct from wallet balance
            _wallets.value = _wallets.value.map {
                if (it.name == currentWallet.name) {
                    it.copy(balance = it.balance - cost)
                } else it
            }
            // Update current selected wallet balance local state
            _selectedWallet.value = _selectedWallet.value.copy(balance = _selectedWallet.value.balance - cost)

            // Register investment
            val camp = _selectedCampaign.value ?: _campaigns.value.first()
            
            // Mark collected amount in Campaign
            _campaigns.value = _campaigns.value.map {
                if (it.id == camp.id) {
                    it.copy(collectedAmount = it.collectedAmount + cost, backersCount = it.backersCount + 1)
                } else it
            }

            val newInvestment = Investment(
                id = "INV-${System.currentTimeMillis() % 10000}",
                campaign = camp.copy(collectedAmount = camp.collectedAmount + cost, backersCount = camp.backersCount + 1),
                capital = cost,
                currentMonth = 1,
                totalPayoutsReceived = 0.0,
                lastPayoutAmount = 0.0,
                payoutLogs = emptyList()
            )

            _investments.value = listOf(newInvestment) + _investments.value
            _checkoutStepNum.value = 4 // Success state indicator
            _checkoutError.value = null
        } else {
            _checkoutError.value = "PIN Jago Syariah Salah! Harap masukkan PIN yang benar (Tips penguji: gunakan PIN: 123456)"
            _pinNumber.value = ""
        }
    }

    fun nextCheckoutStep() {
        val step = _checkoutStepNum.value
        if (step == 1) {
            val cost = _simulatedAmount.value
            if (_selectedWallet.value.balance < cost) {
                _checkoutError.value = "Saldo Tidak Mencukupi! Dana Kantong ${_selectedWallet.value.name} (${formatCurrency(_selectedWallet.value.balance)}) kurang untuk investasi sebesar ${formatCurrency(cost)}."
                return
            }
            _checkoutError.value = null
            _checkoutStepNum.value = 2
        } else if (step == 2) {
            if (_isAgreedToAkad.value && _isContractScrolledBottom.value) {
                _checkoutError.value = null
                _checkoutStepNum.value = 3
            }
        }
    }

    fun prevCheckoutStep() {
        val step = _checkoutStepNum.value
        if (step > 1) {
            _checkoutStepNum.value = step - 1
            _checkoutError.value = null
        }
    }

    // UMKM actions
    fun setProposalStep(step: Int) {
        _proposalStep.value = step
    }

    fun submitProposal() {
        val target = proposalCapitalTarget.value.toDoubleOrNull() ?: 50000000.0
        val newProposal = Proposal(
            id = "PRP-${System.currentTimeMillis() % 10000}",
            businessName = proposalBusinessName.value.ifBlank { "Bisnis UMKM Mandiri" },
            sector = proposalSector.value,
            address = proposalAddress.value.ifBlank { "Jl. Kemitraan Amanah No. 9" },
            description = proposalDescription.value.ifBlank { "Deskripsi pengajuan pengembangan usaha UMKM Syariah." },
            capitalTarget = target,
            contractType = proposalContractType.value,
            tenureMonths = proposalTenureMonths.value.toInt(),
            status = "Menunggu Review",
            submittedAt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
        )
        // Add to list and navigate to review/dashboard tracking
        _submittedProposals.value = listOf(newProposal) + _submittedProposals.value
        _proposalStep.value = 5 // Dashboard post-submission status tracker view
    }

    fun setDpsDashboardActive(active: Boolean) {
        _dpsDashboardActive.value = active
    }

    fun approveProposal(proposalId: String, dpsSignatureName: String) {
        val prop = _submittedProposals.value.find { it.id == proposalId } ?: return
        
        val signedDate = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID")).format(Date())
        
        _submittedProposals.value = _submittedProposals.value.map {
            if (it.id == proposalId) {
                it.copy(
                    status = "Disetujui/Aktif",
                    isSignedByDps = true,
                    dpsSignatureName = dpsSignatureName,
                    dpsSignedAt = signedDate
                )
            } else it
        }

        // Create and add new active Campaign from this proposal!
        val freshCampaign = Campaign(
            id = "KMP-${System.currentTimeMillis() % 10000}",
            title = prop.businessName,
            sector = prop.sector,
            type = prop.contractType,
            targetAmount = prop.capitalTarget,
            collectedAmount = 0.0,
            nisbahInvestor = 0.65, // Standard approved investor share rate
            nisbahUmkm = 0.35,
            tenorMonths = prop.tenureMonths,
            countdownDays = 30, // fresh campaign timeline
            description = prop.description,
            backersCount = 0
        )
        
        _campaigns.value = listOf(freshCampaign) + _campaigns.value
    }

    fun rejectProposal(proposalId: String, reason: String) {
        _submittedProposals.value = _submittedProposals.value.map {
            if (it.id == proposalId) {
                it.copy(
                    status = "Revisi Diperlukan",
                    rejectionReason = reason
                )
            } else it
        }
    }

    fun toggleNib() { _proposalNibUploaded.value = !_proposalNibUploaded.value }
    fun toggleNpwp() { _proposalNpwpUploaded.value = !_proposalNpwpUploaded.value }
    fun toggleFinancial() { _proposalFinancialUploaded.value = !_proposalFinancialUploaded.value }

    fun clearProposalFormAndReset() {
        proposalBusinessName.value = ""
        proposalAddress.value = ""
        proposalDescription.value = ""
        proposalCapitalTarget.value = "75000000"
        proposalTenureMonths.value = 12f
        _proposalNibUploaded.value = false
        _proposalNpwpUploaded.value = false
        _proposalFinancialUploaded.value = false
        _proposalStep.value = 1
    }

    // Helper formatter
    fun formatCurrency(amount: Double): String {
        val formatter = DecimalFormat("#,###")
        formatter.minimumFractionDigits = 0
        return "Rp" + formatter.format(amount).replace(",", ".")
    }

    fun calculateMonthlyEstimate(nominal: Double, nisbahInvestor: Double, tenor: Int): Double {
        // Sharia formula simplified: (Nominal * Nisbah Investor * Tenor / 12) modified with typical profit rate factor e.g. 15% annual yield
        val expectedAnnualRate = 0.15
        return (nominal * expectedAnnualRate * nisbahInvestor * tenor) / 12.0 / tenor
    }

    fun calculateTotalExpectedReturn(nominal: Double, nisbahInvestor: Double, tenor: Int): Double {
        val expectedAnnualRate = 0.15
        val expectedProfit = (nominal * expectedAnnualRate * nisbahInvestor * tenor) / 12.0
        return nominal + expectedProfit
    }

    fun login(username: String, password: String): Boolean {
        _loginError.value = null
        val uLower = username.trim().lowercase()
        val p = password.trim()
        when {
            uLower == "admin" && p == "admin123" -> {
                _loggedInUser.value = "admin"
                _loggedInName.value = "KH. Ahmad Syarifuddin, M.A."
                _loggedInRoleName.value = "Ketua Dewan Pengawas Syariah (DPS)"
                _appMode.value = "investor"
                _isLoggedIn.value = true
                _currentSelectedTab.value = 0 // Start on Audit Proposal tab 0
                _dpsDashboardActive.value = false
                return true
            }
            uLower == "penerbit" && p == "penerbit123" -> {
                _loggedInUser.value = "penerbit"
                _loggedInName.value = "Siti Khadijah"
                _loggedInRoleName.value = "Penerbit UMKM (Owner Ayam Bakar Sambal Korek)"
                _appMode.value = "umkm"
                _isLoggedIn.value = true
                _dpsDashboardActive.value = false
                _currentSelectedTab.value = 0 // Go to UMKM Dashboard (Tab 0)
                _proposalStep.value = 0 // show proposal tracker dashboard
                return true
            }
            (uLower == "nasabah" || uLower == "investor") && (p == "nasabah123" || p == "investor123") -> {
                _loggedInUser.value = "nasabah"
                _loggedInName.value = "Rifan Ashir"
                _loggedInRoleName.value = "Nasabah Pemodal (Premium Investor)"
                _appMode.value = "investor"
                _isLoggedIn.value = true
                _dpsDashboardActive.value = false
                _currentSelectedTab.value = 0 // Go to Beranda
                return true
            }
            else -> {
                _loginError.value = "Username atau password salah! Silakan coba lagi."
                return false
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _loggedInUser.value = null
        _loggedInName.value = ""
        _loggedInRoleName.value = ""
        _loginError.value = null
        _dpsDashboardActive.value = false
        _currentSelectedTab.value = 0
    }
}
