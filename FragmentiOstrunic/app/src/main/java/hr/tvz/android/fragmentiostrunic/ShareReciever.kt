package hr.tvz.android.fragmentiostrunic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ShareReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "📤 Podijeljeno s uspjehom!", Toast.LENGTH_SHORT).show()
    }
}
