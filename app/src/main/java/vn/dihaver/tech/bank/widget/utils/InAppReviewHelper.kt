package vn.dihaver.tech.bank.widget.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Helper triển khai In-App Review của Google.
 *
 * - Lưu trữ trạng thái review (đã review hay chưa) và số lần mở ứng dụng trong SharedPreferences.
 * - Chỉ hiển thị dialog review nếu số lần mở ứng dụng nằm trong khoảng từ 3 đến 5.
 */
class InAppReviewHelper(private val context: Context) {

    private val prefs = context.getSharedPreferences("in_app_review_prefs", Context.MODE_PRIVATE)
    private val KEY_REVIEWED = "key_reviewed"
    private val KEY_LAUNCH_COUNT = "key_launch_count"

    /**
     * Khi khởi tạo helper, cập nhật số lần mở ứng dụng.
     */
    init {
        val currentCount = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        prefs.edit().putInt(KEY_LAUNCH_COUNT, currentCount + 1).apply()
    }

    /**
     * Khởi chạy quá trình review.
     *
     * - Nếu người dùng đã review rồi, callback trả về [ReviewResult.AlreadyReviewed].
     * - Nếu số lần mở ứng dụng không nằm trong khoảng từ 3 đến 5, callback trả về [ReviewResult.NotEligible].
     * - Nếu điều kiện thỏa thì hiển thị dialog “Đánh giá ứng dụng” với 3 lựa chọn:
     *    + **Đánh giá**: Gọi API In-App Review, sau khi hoàn thành sẽ lưu trạng thái đã review và callback trả về [ReviewResult.Launched].
     *    + **Để sau**: Reset số lần mở ứng dụng (để dialog được hiển thị lại sau 3–5 lần mở) và callback trả về [ReviewResult.NotEligible].
     *    + **Không hiển thị nữa**: Đánh dấu đã review (không hiển thị lại dialog) và callback trả về [ReviewResult.AlreadyReviewed].
     *
     * @param activity Activity dùng để hiển thị dialog và launch review flow.
     * @param callback Callback nhận kết quả review.
     */
    fun launchReviewFlow(activity: Activity, callback: (ReviewResult) -> Unit) {
        if (prefs.getBoolean(KEY_REVIEWED, false)) {
            callback(ReviewResult.AlreadyReviewed)
            return
        }

        val launchCount = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        if (launchCount !in 3..5) {
            callback(ReviewResult.NotEligible)
            return
        }

        AlertDialog.Builder(activity)
            .setTitle("Đánh giá ứng dụng")
            .setMessage("Nếu bạn thấy ứng dụng hữu ích, hãy dành chút thời gian để đánh giá nhé :3")
            .setPositiveButton("Đánh giá") { _, _ ->
                val reviewManager = ReviewManagerFactory.create(context)
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                        flow.addOnCompleteListener {
                            prefs.edit().putBoolean(KEY_REVIEWED, true).apply()
                            callback(ReviewResult.Launched)
                        }
                    } else {
                        callback(ReviewResult.Failed)
                    }
                }
            }
            .setNeutralButton("Để sau") { _, _ ->
                prefs.edit().putInt(KEY_LAUNCH_COUNT, 0).apply()
                callback(ReviewResult.NotEligible)
            }
            .setOnCancelListener {
                prefs.edit().putInt(KEY_LAUNCH_COUNT, 0).apply()
                callback(ReviewResult.NotEligible)
            }
            .show()
    }

    /**
     * Các kết quả có thể của quá trình review.
     */
    sealed class ReviewResult {
        data object Launched : ReviewResult()
        data object Failed : ReviewResult()
        data object AlreadyReviewed : ReviewResult()
        data object NotEligible : ReviewResult()
    }
}