package vn.dihaver.tech.bank.widget.utils

import vn.dihaver.tech.bank.widget.data.model.QrEntity

object QrProcess {

    fun extraQrBank(qrContent: String): QrEntity {

        val bankInfoResult = BankBinVN.getBankInfo(qrContent)
        val (bankBin, objectBankBinVN) = bankInfoResult!!
        val (bankName, bankShortName, bankCode) = objectBankBinVN

        val accNumber = Regex("${bankBin}01\\d{2}(.*?)0208").find(qrContent)?.groups?.get(1)?.value!!
        val accHolderName = Regex("(?<=59\\d{2})[A-Z\\s]+").find(qrContent)?.value?.trim() ?: ""
        val newQrContent = BankBinVN.generateQrCode(bankBin, accNumber, accHolderName)

        return QrEntity(
            id = SystemUtils.generateId(),
            bankName = bankName,
            bankBin = bankBin,
            bankShortName = bankShortName,
            bankCode = bankCode,
            bankIconRes = BankBinVN.getBankIcon(bankCode),
            bankLogoRes = BankBinVN.getBankLogo(bankCode),
            accNumber = accNumber,
            accHolderName = accHolderName,
            accAlias = "",
            qrContent = newQrContent,
            cusQrColor = "#FF000000",
            cusQrIconPath = BitmapUtils.convertNameToPath(BankBinVN.getBankIcon(bankCode), BitmapUtils.PathType.RES),
            cusThemePath = BitmapUtils.convertNameToPath("bg_not_have", BitmapUtils.PathType.RES),
        )
    }

    fun isQrBank(qrData: String): Boolean {
        // Không theo chuẩn QR quốc tế -- ERROR
        if (!qrData.contains("QRIBFTTA")) return false
        // Không chứa mã BIN hỗ trợ -- ERROR
        val bankInfoResult = BankBinVN.getBankInfo(qrData) ?: return false
        // Không chứa số tài khoản -- ERROR
        Regex("${bankInfoResult.first}01\\d{2}(.*?)0208").find(qrData)?.groups?.get(1)?.value ?: return false
        return true
    }
}