package vn.dihaver.tech.bank.widget.utils

import vn.dihaver.tech.bank.widget.data.model.QrEntity
import java.util.UUID

object QrProcess {

    //        val codeBank = Regex("9704\\d{2}").find(qrData)?.value

    fun processQrData(qrData: String): QrEntity? {

        if (!qrData.contains("QRIBFTTA")) return null // Không theo chuẩn QR quốc tế -- ERROR

        val bankInfoResult = BankBinVN.getBankInfo(qrData)
            ?: return null // Không chứa mã BIN hỗ trợ -- ERROR

        val (bankBin, objectBankBinVN) = bankInfoResult
        val (bankName, bankShortName, bankCode) = objectBankBinVN

        val accountNumber = Regex("${bankBin}011\\d(.*?)0208").find(qrData)?.groups?.get(1)?.value
            ?: return null // Không chứa số tài khoản -- ERROR

        val accountName = Regex("(?<=5912)[A-Z\\s]+").find(qrData)?.value?.trim()
            ?: "Unknown Name" // Không chứa tên chủ tài khoản

        return QrEntity(
            id = generateId(),
            bankName = bankName,
            bankBin = bankBin,
            bankShortName = bankShortName,
            bankCode = bankCode,
            bankLogo = BankBinVN.getBankLogo(bankCode),
            bankLogoFull = BankBinVN.getBankLogoFull(bankCode),
            accountNumber = accountNumber,
            accountName = accountName,
            qrData = qrData
        )
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}