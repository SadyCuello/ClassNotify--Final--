import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.example.classnotify.domain.viewModels.LoginState
import com.example.classnotify.domain.viewModels.LoginViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import com.example.classnotify.LoginActivity


@Composable
fun LoginView(loginViewModel: LoginViewModel, onGoogleSignInClick: () -> Unit) {

    val context = LocalContext.current
    val loginActivity = context as LoginActivity


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        val loginState by loginViewModel.loginState.collectAsState()

        if (loginState is LoginState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        }
        Button(
            onClick = { loginActivity.onGoogleSignInClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login with Google")
        }
    }
}