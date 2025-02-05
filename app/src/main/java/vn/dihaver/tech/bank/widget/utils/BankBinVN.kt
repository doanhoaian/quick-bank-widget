package vn.dihaver.tech.bank.widget.utils

import vn.dihaver.tech.bank.widget.utils.CalculateUtils.generateCheckSumCRC16

// Update 26-01-2025 - 58 banks & 2 wallet

data class Bank(
    val bin: String,
    val name: String,
    val shortName: String,
    val code: String
)

object BankBinVN {
    private val bankMapping = mapOf(
        "970425" to Triple("NH TMCP An Bình", "ABBANK", "ABB"),
        "970416" to Triple("NH TMCP Á Châu", "ACB", "ACB"),
        "970409" to Triple("NH TMCP Bắc Á", "BacABank", "BAB"),
        "970418" to Triple("NH TMCP Đầu tư và Phát triển Việt Nam", "BIDV", "BIDV"),
        "970438" to Triple("NH TMCP Bảo Việt", "BaoVietBank", "BVB"),
        "546034" to Triple("TMCP Việt Nam Thịnh Vượng - NH số CAKE by VPBank", "CAKE", "CAKE"),
        "970444" to Triple("NH Thương mại TNHH MTV Xây dựng Việt Nam", "CBBank", "CBB"),
        "422589" to Triple("NH TNHH MTV CIMB Việt Nam", "CIMB", "CIMB"),
        "970446" to Triple("NH Hợp tác xã Việt Nam", "COOPBANK", "COOPBANK"),
        "796500" to Triple("DBS Bank Ltd - CN TP HCM", "DBSBank", "DBS"),
        "970406" to Triple("NH TMCP Đông Á", "DongABank", "DOB"),
        "970431" to Triple("NH TMCP Xuất Nhập khẩu Việt Nam", "Eximbank", "EIB"),
        "970408" to Triple("NH Thương mại TNHH MTV Dầu Khí Toàn Cầu", "GPBank", "GPB"),
        "970437" to Triple("NH TMCP Phát triển TP HCM", "HDBank", "HDB"),
        "970442" to Triple("NH TNHH MTV Hong Leong Việt Nam", "HongLeong", "HLBVN"),
        "458761" to Triple("NH TNHH MTV HSBC Việt Nam", "HSBC", "HSBC"),
        "970455" to Triple("NH Công nghiệp Hàn Quốc - CN Hà Nội", "IBKHN", "IBK"),
        "970456" to Triple("NH Công nghiệp Hàn Quốc - CN TP. Hồ Chí Minh", "IBKHCM", "IBK"),
        "970415" to Triple("NH TMCP Công thương Việt Nam", "VietinBank", "ICB"),
        "970434" to Triple("NH TNHH Indovina", "IndovinaBank", "IVB"),
        "970462" to Triple("NH Kookmin - CN HN", "KookminHN", "KB"),
        "970463" to Triple("NH Kookmin - CN TP HCM", "KookminHCM", "KB"),
        "668888" to Triple("NH Đại chúng TNHH Kasikornbank", "KBank", "KBANK"),
        "970452" to Triple("NH TMCP Kiên Long", "KienLongBank", "KLB"),
        "970449" to Triple("NH TMCP Lộc Phát Việt Nam", "LPBank", "LPB"),
        "970422" to Triple("NH TMCP Quân đội", "MBBank", "MB"),
        "970426" to Triple("NH TMCP Hàng Hải", "MSB", "MSB"),
        "970428" to Triple("NH TMCP Nam Á", "NamABank", "NAB"),
        "970419" to Triple("NH TMCP Quốc Dân", "NCB", "NCB"),
        "801011" to Triple("NH Nonghyup - CN HN", "Nonghyup", "NHB"),
        "970448" to Triple("NH TMCP Phương Đông", "OCB", "OCB"),
        "970414" to Triple("NH Thương mại TNHH MTV Đại Dương", "Oceanbank", "OCEANBANK"),
        "970439" to Triple("NH TNHH MTV Public Việt Nam", "PublicBank", "PBVN"),
        "970430" to Triple("NH TMCP Xăng dầu Petrolimex", "PGBank", "PGB"),
        "970412" to Triple("NH TMCP Đại Chúng Việt Nam", "PVcomBank", "PVCB"),
        "970429" to Triple("NH TMCP Sài Gòn", "SCB", "SCB"),
        "970410" to Triple(
            "NH TNHH MTV Standard Chartered Bank Việt Nam",
            "StandardChartered",
            "SCVN"
        ),
        "970440" to Triple("NH TMCP Đông Nam Á", "SeABank", "SEAB"),
        "970400" to Triple("NH TMCP Sài Gòn Công Thương", "SaigonBank", "SGICB"),
        "970443" to Triple("NH TMCP Sài Gòn - HN", "SHB", "SHB"),
        "970424" to Triple("NH TNHH MTV Shinhan Việt Nam", "ShinhanBank", "SHBVN"),
        "970403" to Triple("NH TMCP Sài Gòn Thương Tín", "Sacombank", "STB"),
        "970407" to Triple("NH TMCP Kỹ thương Việt Nam", "Techcombank", "TCB"),
        "963388" to Triple("NH số Timo by Ban Viet Bank", "Timo", "TIMO"),
        "970423" to Triple("NH TMCP Tiên Phong", "TPBank", "TPB"),
        "546035" to Triple("TMCP Việt Nam Thịnh Vượng - NH số Ubank by VPBank", "Ubank", "UBANK"),
        "970458" to Triple("NH United Overseas - CN TP. HCM", "UnitedOverseas", "UOB"),
        "970427" to Triple("NH TMCP Việt Á", "VietABank", "VAB"),
        "970405" to Triple("NH Nông nghiệp và Phát triển Nông thôn Việt Nam", "Agribank", "VBA"),
        "970436" to Triple("NH TMCP Ngoại Thương Việt Nam", "Vietcombank", "VCB"),
        "970454" to Triple("NH TMCP Bản Việt", "VietCapitalBank", "VCCB"),
        "970441" to Triple("NH TMCP Quốc tế Việt Nam", "VIB", "VIB"),
        "970433" to Triple("NH TMCP Việt Nam Thương Tín", "VietBank", "VIETBANK"),
        "971011" to Triple("VNPT Money", "VNPTMoney", "VNPTMONEY"),
        "970432" to Triple("NH TMCP Việt Nam Thịnh Vượng", "VPBank", "VPB"),
        "970421" to Triple("NH Liên doanh Việt - Nga", "VRB", "VRB"),
        "971005" to Triple("Tổng CT Dịch vụ số Viettel", "ViettelMoney", "VTLMONEY"),
        "970457" to Triple("NH TNHH MTV Woori Việt Nam", "Woori", "WVN"),
//        "momo" to Triple("Ví điện tử MoMo", "Momo", "MOMO"),
//        "viettelmoney" to Triple("Viettel Money - Tài khoản tiền di động", "Viettel Money", "VIETTELMONEY")
    )

    fun getBankList(): List<Bank> {
        return bankMapping.map { (bin, info) ->
            Bank(bin, info.first, info.second, info.third)
        }
    }

    fun getBankInfo(input: String): Pair<String, Triple<String, String, String>>? {
        for (bin in bankMapping.keys) {
            if (input.contains(bin)) {
                return Pair(bin, bankMapping[bin]!!)
            }
        }
        return null
    }

    fun getShortName(bin: String): String? {
        return bankMapping[bin]?.second
    }

    fun getName(bin: String): String? {
        return bankMapping[bin]?.first
    }

    fun getCode(bin: String): String? {
        return bankMapping[bin]?.third
    }


    fun getBankIcon(bankCode: String): String {
        return "icon_bank_${bankCode.lowercase()}"
    }

    fun getBankLogo(bankCode: String): String {
        return "logo_bank_${bankCode.lowercase()}"
    }

    fun generateQrCode(
        bin: String,
        accNumber: String,
        accHolderName: String = "",
        amount: Long = 0,
        content: String = ""
    ): String {
        return buildString {
            append("000201010212")
            val binLength = bin.length
            val aNumLength = accNumber.length
            val amoLength = amount.toString().length
            val aNameLength = accHolderName.length
            val conLength = content.length
            append("38%02d".format(38 + binLength + aNumLength))
            append("0010A00000072701%02d".format(8 + binLength + aNumLength))
            append("00%02d".format(binLength)).append(bin)
            append("01%02d".format(aNumLength)).append(accNumber)
            append("0208QRIBFTTA")
            if (amount > 0) {
                append("530370454%02d%02d".format(amoLength, amount))
            } else {
                append("5303704")
            }
            append("5802VN")
            if (accHolderName.isNotEmpty()) {
                append("59%02d".format(aNameLength)).append(accHolderName)
            }
            if (content.isNotEmpty()) {
                append("62%02d08%02d".format(conLength + 4, conLength)).append(content)
            }
            append("6304")
            append(calculateCRC(toString()))
        }
    }

    private fun calculateCRC(input: String): String {
        val bytes = input.toByteArray()
        val crc = bytes.generateCheckSumCRC16()
        return String.format("%04X", crc).uppercase()
    }


}