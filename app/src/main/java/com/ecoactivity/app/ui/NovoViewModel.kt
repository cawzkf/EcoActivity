import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ecoactivity.app.ui.Aparelho
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NovoViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // LiveData para notificar quando o modal deve ser fechado
    private val _closeModal = MutableLiveData<Boolean>()
    val closeModal: LiveData<Boolean> get() = _closeModal

    /**
     * Adiciona um novo aparelho como documento na subcoleção 'aparelhos' do usuário logado.
     */
    fun addAparelho(novoAparelho: Aparelho) {
        val userId = auth.currentUser?.uid // Obtém o ID do usuário logado

        if (userId.isNullOrBlank()) {
            Log.e("NovoViewModel", "Usuário não autenticado ou ID do usuário não encontrado.")
            return
        }

        // Referência à subcoleção "aparelhos" dentro do documento do usuário logado
        val aparelhosCollectionRef = firestore.collection("users")
            .document(userId)
            .collection("aparelhos")

        // Cria um novo documento na subcoleção
        val newDocRef = aparelhosCollectionRef.document()

        // Define o ID do aparelho no Firestore
        val aparelhoComId = novoAparelho.copy(id = newDocRef.id)

        // Salva o aparelho como um novo documento na subcoleção
        newDocRef.set(aparelhoComId)
            .addOnSuccessListener {
                Log.d("NovoViewModel", "Aparelho adicionado com sucesso: $aparelhoComId")
                _closeModal.value = true
            }
            .addOnFailureListener { e ->
                Log.e("NovoViewModel", "Erro ao adicionar aparelho: ${e.message}")
                _closeModal.value = false
            }
    }
}
