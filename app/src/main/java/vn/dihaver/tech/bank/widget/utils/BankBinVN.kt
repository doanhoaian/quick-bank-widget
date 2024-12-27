package vn.dihaver.tech.bank.widget.utils

// Update 23-12-2024 - 63 banks & 2 wallet

object BankBinVN {
    private val bankMapping = mapOf(
        "970425" to Triple("Ngân hàng TMCP An Bình", "ABBANK", "ABB"),
        "970416" to Triple("Ngân hàng TMCP Á Châu", "ACB", "ACB"),
        "970409" to Triple("Ngân hàng TMCP Bắc Á", "BacABank", "BAB"),
        "970418" to Triple("Ngân hàng TMCP Đầu tư và Phát triển Việt Nam", "BIDV", "BIDV"),
        "970438" to Triple("Ngân hàng TMCP Bảo Việt", "BaoVietBank", "BVB"),
        "970444" to Triple("Ngân hàng Thương mại TNHH MTV Xây dựng Việt Nam", "CBBank", "CBB"),
        "422589" to Triple("Ngân hàng TNHH MTV CIMB Việt Nam", "CIMB", "CIMB"),
        "796500" to Triple("DBS Bank Ltd - Chi nhánh Thành phố Hồ Chí Minh", "DBSBank", "DBS"),
        "970406" to Triple("Ngân hàng TMCP Đông Á", "DongABank", "DOB"),
        "970431" to Triple("Ngân hàng TMCP Xuất Nhập khẩu Việt Nam", "Eximbank", "EIB"),
        "970408" to Triple("Ngân hàng Thương mại TNHH MTV Dầu Khí Toàn Cầu", "GPBank", "GPB"),
        "970437" to Triple("Ngân hàng TMCP Phát triển Thành phố Hồ Chí Minh", "HDBank", "HDB"),
        "970442" to Triple("Ngân hàng TNHH MTV Hong Leong Việt Nam", "HongLeong", "HLBVN"),
        "458761" to Triple("Ngân hàng TNHH MTV HSBC (Việt Nam)", "HSBC", "HSBC"),
        "970455" to Triple(
            "Ngân hàng Công nghiệp Hàn Quốc - Chi nhánh Hà Nội",
            "IBKHN",
            "IBK"
        ),
        "970456" to Triple(
            "Ngân hàng Công nghiệp Hàn Quốc - Chi nhánh TP. Hồ Chí Minh",
            "IBKHCM",
            "IBK"
        ),
        "970415" to Triple("Ngân hàng TMCP Công thương Việt Nam", "VietinBank", "ICB"),
        "970434" to Triple("Ngân hàng TNHH Indovina", "IndovinaBank", "IVB"),
        "970452" to Triple("Ngân hàng TMCP Kiên Long", "KienLongBank", "KLB"),
        "970449" to Triple("Ngân hàng TMCP Lộc Phát Việt Nam", "LPBank", "LPB"),
        "970422" to Triple("Ngân hàng TMCP Quân đội", "MBBank", "MB"),
        "970426" to Triple("Ngân hàng TMCP Hàng Hải", "MSB", "MSB"),
        "970428" to Triple("Ngân hàng TMCP Nam Á", "NamABank", "NAB"),
        "970419" to Triple("Ngân hàng TMCP Quốc Dân", "NCB", "NCB"),
        "801011" to Triple("Ngân hàng Nonghyup - Chi nhánh Hà Nội", "Nonghyup", "NHB"),//
        "970448" to Triple("Ngân hàng TMCP Phương Đông", "OCB", "OCB"),
        "970414" to Triple("Ngân hàng Thương mại TNHH MTV Đại Dương", "Oceanbank", "OCEANBANK"),
        "970439" to Triple("Ngân hàng TNHH MTV Public Việt Nam", "PublicBank", "PBVN"),
        "970430" to Triple("Ngân hàng TMCP Xăng dầu Petrolimex", "PGBank", "PGB"),
        "970412" to Triple("Ngân hàng TMCP Đại Chúng Việt Nam", "PVcomBank", "PVCB"),
        "970429" to Triple("Ngân hàng TMCP Sài Gòn", "SCB", "SCB"),
        "970410" to Triple(
            "Ngân hàng TNHH MTV Standard Chartered Bank Việt Nam",
            "StandardChartered",
            "SCVN"
        ),
        "970440" to Triple("Ngân hàng TMCP Đông Nam Á", "SeABank", "SEAB"),
        "970400" to Triple("Ngân hàng TMCP Sài Gòn Công Thương", "SaigonBank", "SGICB"),//
        "970443" to Triple("Ngân hàng TMCP Sài Gòn - Hà Nội", "SHB", "SHB"),
        "970403" to Triple("Ngân hàng TMCP Sài Gòn Thương Tín", "Sacombank", "STB"),//
        "970424" to Triple("Ngân hàng TNHH MTV Shinhan Việt Nam", "ShinhanBank", "SHBVN"),
        "970407" to Triple("Ngân hàng TMCP Kỹ thương Việt Nam", "Techcombank", "TCB"),
        "970423" to Triple("Ngân hàng TMCP Tiên Phong", "TPBank", "TPB"),
        "970458" to Triple(
            "Ngân hàng United Overseas - Chi nhánh TP. Hồ Chí Minh",
            "UnitedOverseas",
            "UOB"
        ),//
        "970427" to Triple("Ngân hàng TMCP Việt Á", "VietABank", "VAB"),
        "970405" to Triple(
            "Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam",
            "Agribank",
            "VBA"
        ),
        "970436" to Triple("Ngân hàng TMCP Ngoại Thương Việt Nam", "Vietcombank", "VCB"),
        "970454" to Triple("Ngân hàng TMCP Bản Việt", "VietCapitalBank", "VCCB"),
        "970441" to Triple("Ngân hàng TMCP Quốc tế Việt Nam", "VIB", "VIB"),
        "970433" to Triple("Ngân hàng TMCP Việt Nam Thương Tín", "VietBank", "VIETBANK"),//
        "970432" to Triple("Ngân hàng TMCP Việt Nam Thịnh Vượng", "VPBank", "VPB"),
        "970421" to Triple("Ngân hàng Liên doanh Việt - Nga", "VRB", "VRB"),//
        "970457" to Triple("Ngân hàng TNHH MTV Woori Việt Nam", "Woori", "WVN"),
        "970462" to Triple("Ngân hàng Kookmin - Chi nhánh Hà Nội", "KookminHN", "KB"),
        "970463" to Triple(
            "Ngân hàng Kookmin - Chi nhánh Thành phố Hồ Chí Minh",
            "KookminHCM",
            "KB"
        ),
        "970446" to Triple("Ngân hàng Hợp tác xã Việt Nam", "COOPBANK", "COOPBANK"),//
        "546034" to Triple(
            "TMCP Việt Nam Thịnh Vượng - Ngân hàng số CAKE by VPBank",
            "CAKE",
            "CAKE"
        ),
        "546035" to Triple(
            "TMCP Việt Nam Thịnh Vượng - Ngân hàng số Ubank by VPBank",
            "Ubank",
            "UBANK"
        ),//
        "668888" to Triple("Ngân hàng Đại chúng TNHH Kasikornbank", "KBank", "KBANK"),//
        "971011" to Triple("VNPT Money", "VNPTMoney", "VNPTMONEY"),
        "971005" to Triple(
            "Tổng Công ty Dịch vụ số Viettel - Chi nhánh tập đoàn công nghiệp viễn thông Quân Đội",
            "ViettelMoney",
            "VTLMONEY"
        ),
        "963388" to Triple(
            "Ngân hàng số Timo by Ban Viet Bank (Timo by Ban Viet Bank)",
            "Timo",
            "TIMO"
        ),
        "533948" to Triple("Ngân hàng Citibank, N.A. - Chi nhánh Hà Nội", "Citibank", "CITIBANK"),
        "970466" to Triple(
            "Ngân hàng KEB Hana – Chi nhánh Thành phố Hồ Chí Minh",
            "KEBHanaHCM",
            "KEBHANA"
        ),
        "970467" to Triple("Ngân hàng KEB Hana – Chi nhánh Hà Nội", "KEBHanaHN", "KEBHANA"),
        "977777" to Triple("Công ty Tài chính TNHH MTV Mirae Asset (Việt Nam)", "MAFC", "MAFC"),//
        "999888" to Triple("Ngân hàng Chính sách Xã hội", "VBSP", "VBSP"),//
        "momo" to Triple("Ví điện tử MoMo", "Momo", "MOMO"),
        "viettelmoney" to Triple(
            "Viettel Money - Tài khoản tiền di động",
            "Viettel Money",
            "VIETTELMONEY"
        )
    )

    fun getBankInfo(input: String): Pair<String, Triple<String, String, String>>? {
        for (bin in bankMapping.keys) {
            if (input.contains(bin)) {
                return Pair(bin, bankMapping[bin]!!)
            }
        }
        return null
    }

    fun getBankLogo(bankCode: String): String {
        return "https://raw.githubusercontent.com/doanhoaian/support-quick-bank-widget/refs/heads/main/img/${bankCode}.svg"
    }

    fun getBankLogoFull(bankCode: String): String {
        return "https://vietqr.co/storage/banks/${bankCode.lowercase()}.png"
    }

}