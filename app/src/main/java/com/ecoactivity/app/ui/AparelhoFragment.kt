package com.ecoactivity.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStore
import com.ecoactivity.app.databinding.FragmentAparelhoBinding
import com.google.firebase.firestore.FirebaseFirestore

class AparelhoFragment : Fragment() {

    private lateinit var binding: FragmentAparelhoBinding
    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAparelhoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obter automaticamente o userId e carregar dados do aparelho
        obterUsuarioAtivo { userId ->
            carregarDadosAparelho(userId)
        }
    }

    /**
     * Função para carregar os dados do aparelho do Firestore.
     */
    private fun carregarDadosAparelho(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("aparelhos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Obtém o primeiro aparelho (ou ajuste conforme necessário)
                    val aparelhoDocument = querySnapshot.documents[0]
                    val aparelho = aparelhoDocument.toObject(Aparelho::class.java)

                    if (aparelho != null) {
                        // Define os valores nos TextViews
                        binding.textTipoAparelho.text = "Tipo: ${aparelho.tipo}"
                        binding.textDataCadastro.text = "Data: ${converterData(aparelho.dataCadastro)}"
                        binding.textQuantidade.text = "Qtd: ${aparelho.quantidade}"

                        // Configura o botão para excluir
                        binding.buttonExcluirAparelho.setOnClickListener {
                            excluirAparelho(userId, aparelho.id)
                        }
                    }
                } else {
                    Toast.makeText(context, "Nenhum aparelho encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao carregar aparelhos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Função para excluir um aparelho do Firestore.
     */
    private fun excluirAparelho(userId: String, aparelhoId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("aparelhos")
            .document(aparelhoId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Aparelho excluído com sucesso!", Toast.LENGTH_SHORT).show()
                // Atualizar ou reiniciar o fragmento para refletir as alterações
                carregarDadosAparelho(userId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao excluir aparelho: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Função para buscar o ID do usuário ativo.
     */
    private fun obterUsuarioAtivo(callback: (String) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("status", true) // Busca o usuário com status ativo
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val userId = userDocument.id
                    callback(userId)
                } else {
                    Toast.makeText(context, "Nenhum usuário ativo encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao buscar usuário ativo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Converte um timestamp para uma string de data legível.
     */
    private fun converterData(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return format.format(date)
    }
}
