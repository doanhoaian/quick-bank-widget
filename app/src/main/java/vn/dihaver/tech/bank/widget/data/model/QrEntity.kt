package vn.dihaver.tech.bank.widget.data.model

import com.google.gson.annotations.SerializedName

data class QrEntity(
    @SerializedName("id")
    val id: String,

    @SerializedName("bank_name") // Tên pháp nhân ngân hàng (Ngân hàng TMCP Kỹ thương Việt Nam)
    val bankName: String,
    @SerializedName("bank_bin") // Mã BIN ngân hàng (970407)
    val bankBin: String,
    @SerializedName("bank_code") // Tên mã ngân hàng (TCB)
    val bankCode: String,
    @SerializedName("bank_short_name") // Tên ngắn gọn thường gọi (Techcombank)
    val bankShortName: String,
    @SerializedName("bank_logo") // Đường dẫn đến logo ngân hàng
    val bankLogo: String,
    @SerializedName("bank_logo_full") // Đường dẫn đến logo ngân hàng đầy đủ
    val bankLogoFull: String,

    @SerializedName("account_number") // Số tài khoản ngân hàng
    val accountNumber: String,
    @SerializedName("account_name") // Tên chủ tài khoản ngân hàng
    val accountName: String,

    @SerializedName("qr_data") // Dữ liệu QR dạng chuỗi
    val qrData: String,
)
