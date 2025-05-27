package hr.tvz.android.fragmentiostrunic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PowerConnectedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Punjač je spojen!", Toast.LENGTH_SHORT).show()
    }
}
