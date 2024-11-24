package com.ecoactivity.app.ui.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R
import com.ecoactivity.app.databinding.FragmentNotificationBinding
import com.ecoactivity.app.ui.NotificationViewModel

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    // Constantes para permissão e canal de notificação
    companion object {
        private const val CHANNEL_ID = "ecoactivity_channel"
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observe o LiveData do ViewModel
        notificationViewModel.text.observe(viewLifecycleOwner) { text ->
            Log.d("NotificationFragment", "Texto atualizado: $text")
            binding.textNotification.text = text
        }

        // Crie o canal de notificação
        createNotificationChannel()

        // Configurar clique no botão para enviar notificação
        binding.buttonSave.setOnClickListener {
            sendTestNotification()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Método para criar um canal de notificação (obrigatório para Android 8+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "EcoActivity Notifications"
            val descriptionText = "Canal para notificações do EcoActivity"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Método para enviar uma notificação de teste
    private fun sendTestNotification() {
        // Verificar permissão antes de enviar notificação
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permissão ao usuário
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        // Construir e enviar a notificação
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_eco_black_background) // Ícone da notificação
            .setContentTitle("EcoActivity")
            .setContentText("Essa é uma notificação de teste!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(1, builder.build()) // Envia a notificação
        }
    }

    // Lidar com a resposta do usuário à solicitação de permissão
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, envie a notificação
                sendTestNotification()
            } else {
                // Permissão negada, informe ao usuário
                Toast.makeText(requireContext(), "Permissão para enviar notificações foi negada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
