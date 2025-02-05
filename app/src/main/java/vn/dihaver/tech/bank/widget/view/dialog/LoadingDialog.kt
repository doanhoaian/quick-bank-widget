package vn.dihaver.tech.bank.widget.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import vn.dihaver.tech.bank.widget.R
import java.lang.ref.WeakReference

object LoadingDialog {
    private var dialog: Dialog? = null
    private var contextRef: WeakReference<Context>? = null

    @SuppressLint("InflateParams")
    fun show(context: Context) {
        if (dialog != null && contextRef?.get() === context) {
            if (!dialog!!.isShowing) {
                dialog!!.show()
            }
            return
        }
        contextRef = WeakReference(context)
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, null))
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun isShow(): Boolean {
        return dialog?.isShowing == true
    }
}