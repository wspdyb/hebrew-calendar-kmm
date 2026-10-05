package wspdyb.calculator.zmanim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import wspdyb.calculator.zmanim.ui.theme.ZmanimTheme
import wspdyb.calculator.zmanim.ui.ZmanimApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZmanimTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ZmanimApp()
                }
            }
        }
    }
}
