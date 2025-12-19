package xyz.sattar.javid.proqueue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import android.content.Intent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        handleNotificationIntent(intent)

        setContent {
            App()
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("from_notification", false) == true) {
            val businessId = intent.getLongExtra("businessId", -1)
            val visitorId = intent.getLongExtra("visitorId", -1)
            val customerName = intent.getStringExtra("customerName") ?: ""
            val businessName = intent.getStringExtra("businessName") ?: ""
            val minutesBefore = intent.getIntExtra("minutesBefore", 0)
            
            android.widget.Toast.makeText(
                this, 
                "نوتیفیکیشن: $customerName - $businessName ($minutesBefore دقیقه قبل) [Biz: $businessId, Vis: $visitorId]", 
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}