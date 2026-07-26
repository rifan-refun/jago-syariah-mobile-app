package com.example.model

data class Campaign(
    val id: String,
    val title: String,
    val sector: String, // Culinary ("Kuliner"), Fashion ("Fashion"), Agritech, etc.
    val type: String,   // "Musyarakah" or "Mudharabah"
    val targetAmount: Double,
    val collectedAmount: Double,
    val nisbahInvestor: Double, // e.g. 0.60 for 60%
    val nisbahUmkm: Double,      // e.g. 0.40 for 40%
    val tenorMonths: Int,
    val countdownDays: Int,
    val description: String,
    val backersCount: Int,
    val imageResId: Int = 0 // Using fallback local vector shapes since image files are mock dynamic
) {
    val progress: Float
        get() = if (targetAmount > 0) (collectedAmount / targetAmount).toFloat() else 0f
}

data class PayoutLog(
    val monthIndex: Int,
    val date: String,
    val amount: Double,
    val status: String = "Selesai dibagikan"
)

data class Investment(
    val id: String,
    val campaign: Campaign,
    val capital: Double,
    val currentMonth: Int,
    val totalPayoutsReceived: Double,
    val lastPayoutAmount: Double,
    val payoutLogs: List<PayoutLog>,
    val isFailedAndRefunded: Boolean = false,
    val status: String = "AKTIF" // "AKTIF", "GAGAL_REFUNDED"
)

data class Proposal(
    val id: String,
    val businessName: String,
    val sector: String,
    val address: String,
    val description: String,
    val capitalTarget: Double,
    val contractType: String,
    val tenureMonths: Int,
    val status: String, // "Menunggu Review Admin", "Revisi Diperlukan", "Disetujui/Aktif"
    val submittedAt: String,
    val rejectionReason: String? = null,
    val isSignedByDps: Boolean = false,
    val dpsSignatureName: String? = null,
    val dpsSignedAt: String? = null
)

data class MentoringSession(
    val id: String,
    val umkmName: String,
    val date: String,
    val time: String,
    val topic: String,
    val material: String,
    val status: String = "Dijadwalkan"
)

data class ShariaWallet(
    val name: String,
    val balance: Double,
    val accountNumber: String
)

data class UmkmTransaction(
    val id: String,
    val type: String, // "BAGI_HASIL" or "REFUND"
    val date: String,
    val amount: Double,
    val description: String,
    val walletSource: String
)
