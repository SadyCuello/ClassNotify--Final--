import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.example.classnotify.ui_presentation.ui.theme.RedColor
import com.example.classnotify.ui_presentation.ui.theme.WhiteColor

private val DarkColorScheme = darkColorScheme(
    primary = RedColor,
    secondary = RedColor,
   background = WhiteColor, // Fondo blanco en tema oscuro
   surface = WhiteColor, // Fondo blanco en superficie
    onPrimary = WhiteColor,
    onSecondary = WhiteColor,
    onBackground = Color.White, // Texto negro sobre fondo blanco en modo oscuro
    onSurface = Color.White // Texto negro sobre superficie blanca en modo oscuro
)
