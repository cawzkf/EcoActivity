import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ecoactivity.app.ui.Aparelho
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AparelhoViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _devices = MutableLiveData<List<Aparelho>>()
    val devices: LiveData<List<Aparelho>> = _devices

    private val _tariff = MutableLiveData<Double>()
    val tariff: LiveData<Double> = _tariff

    init {
        loadDevices()
        loadTariff()
    }

    /**
     * Carrega os aparelhos do Firestore.
     */
    private fun loadDevices() {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("AparelhoViewModel", "Usuário não autenticado.")
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("aparelhos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AparelhoViewModel", "Erro ao carregar aparelhos: ${e.message}")
                    return@addSnapshotListener
                }

                val devicesList = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Aparelho::class.java)?.copy(id = document.id)
                } ?: emptyList()

                // Atualiza o LiveData com todos os aparelhos
                Log.d("AparelhoViewModel", "Aparelhos carregados: $devicesList")
                _devices.value = devicesList
            }
    }


    /**
     * Carrega a tarifa do Firestore.
     */
    private fun loadTariff() {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("AparelhoViewModel", "Usuário não autenticado.")
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val tariff = document.getDouble("tariff") ?: 0.0
                _tariff.value = tariff
                Log.d("AparelhoViewModel", "Tarifa carregada: $tariff")
            }
            .addOnFailureListener { e ->
                Log.e("AparelhoViewModel", "Erro ao carregar tarifa: ${e.message}")
            }
    }

    /**
     * Exclui um aparelho do Firestore.
     */
    fun deleteAparelho(aparelho: Aparelho) {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("AparelhoViewModel", "Usuário não autenticado.")
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("aparelhos")
            .document(aparelho.id)
            .delete()
            .addOnSuccessListener {
                Log.d("AparelhoViewModel", "Aparelho excluído: ${aparelho.id}")
            }
            .addOnFailureListener { e ->
                Log.e("AparelhoViewModel", "Erro ao excluir aparelho: ${e.message}")
            }
    }

    /**
     * Exclui uma lista de aparelhos do Firestore.
     */
    fun deleteAparelhos(aparelhos: List<Aparelho>) {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("AparelhoViewModel", "Usuário não autenticado.")
            return
        }

        for (aparelho in aparelhos) {
            firestore.collection("users")
                .document(userId)
                .collection("aparelhos")
                .document(aparelho.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("AparelhoViewModel", "Aparelho excluído: ${aparelho.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("AparelhoViewModel", "Erro ao excluir aparelho: ${e.message}")
                }
        }
    }
}
