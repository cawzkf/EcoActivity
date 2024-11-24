import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ecoactivity.app.ui.Aparelho
import com.ecoactivity.app.ui.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception


class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _registrationError = MutableLiveData<String>()
    val registrationError: LiveData<String> get() = _registrationError

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> get() = _registrationSuccess

    fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        saveUserData(userId, email)
                        _registrationSuccess.value = true
                        user.sendEmailVerification()
                    } else {
                        _registrationError.value = "Erro ao obter o ID do usuário."
                    }
                } else {
                    handleRegistrationError(task.exception)
                }
            }
    }

    private fun saveUserData(userId: String, email: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        val user = Usuarios(
            id = userId,
            email = email,
            dateCreated = System.currentTimeMillis(),
            aparelhos = emptyMap() // Inicializa aparelhos como vazio
        )

        userRef.setValue(user).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                _registrationError.value = "Erro ao salvar os dados no banco."
            }
        }
    }

    private fun handleRegistrationError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Email inválido."
            is FirebaseAuthUserCollisionException -> "Este email já está em uso."
            else -> "Erro desconhecido. Tente novamente."
        }
        _registrationError.value = errorMessage
    }
}
