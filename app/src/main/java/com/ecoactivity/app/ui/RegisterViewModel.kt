import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ecoactivity.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.ecoactivity.app.ui.Usuarios

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _registrationError = MutableLiveData<String>()
    val registrationError: LiveData<String> get() = _registrationError

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> get() = _registrationSuccess

    /**
     * Registra um novo usuário com validações.
     */
    fun registerUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _registrationError.value = "Preencha todos os campos!"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registrationError.value = "E-mail inválido!"
            return
        }

        if (!isPasswordValid(password)) {
            _registrationError.value = "A senha não atende aos critérios."
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    saveUserToFirestore(it.uid, email)
                }
            } else {
                handleRegistrationError(task.exception)
            }
        }
    }

    /**
     * Salva os dados do usuário no Firestore usando a classe `Usuarios`.
     */
    private fun saveUserToFirestore(userId: String, email: String) {
        val usuario = Usuarios(
            id = userId,
            email = email,
            dateCreated = System.currentTimeMillis(),
            status = true // Define o status como ativo inicialmente
        )

        firestore.collection("users")
            .document(userId)
            .set(usuario) // Salva o objeto da classe `Usuarios` diretamente
            .addOnSuccessListener {
                _registrationSuccess.value = true
            }
            .addOnFailureListener { e ->
                _registrationError.value = "Erro ao salvar dados no Firestore: ${e.message}"
            }
    }

    /**
     * Valida os critérios de senha.
     */
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    /**
     * Atualiza dinamicamente as condições de senha.
     */
    fun validatePasswordConditions(
        password: String,
        conditionLength: TextView,
        conditionUppercase: TextView,
        conditionLowercase: TextView,
        conditionDigit: TextView,
        conditionSpecial: TextView
    ) {
        conditionLength.setTextColor(getConditionColor(password.length >= 8, conditionLength.context))
        conditionUppercase.setTextColor(getConditionColor(password.any { it.isUpperCase() }, conditionUppercase.context))
        conditionLowercase.setTextColor(getConditionColor(password.any { it.isLowerCase() }, conditionLowercase.context))
        conditionDigit.setTextColor(getConditionColor(password.any { it.isDigit() }, conditionDigit.context))
        conditionSpecial.setTextColor(getConditionColor(password.any { !it.isLetterOrDigit() }, conditionSpecial.context))
    }

    /**
     * Define a cor para os critérios.
     */
    private fun getConditionColor(isValid: Boolean, context: Context): Int {
        return if (isValid) {
            context.getColor(R.color.green)
        } else {
            context.getColor(R.color.error_red)
        }
    }

    /**
     * Trata erros de registro.
     */
    private fun handleRegistrationError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "E-mail inválido."
            is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso."
            else -> "Erro desconhecido. Tente novamente."
        }
        _registrationError.value = errorMessage
    }
}
