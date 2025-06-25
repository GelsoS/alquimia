package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.alquimia.databinding.FragmentMessagesBinding // Crie este binding
import com.alquimia.ui.chat.MessageViewModel // Use o MessageViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val messageViewModel: MessageViewModel by viewModels() // Use MessageViewModel
    private val args: MessagesFragmentArgs by navArgs() // Usar navArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val conversationId = args.conversationId
        val otherUserId = args.otherUserId // Se você passar o otherUserId também

        // Exemplo de como usar os dados
        binding.tvConversationId.text = "Conversa ID: $conversationId"
        binding.tvOtherUserId.text = "Outro Usuário ID: $otherUserId"

        // Carregar mensagens
        messageViewModel.fetchMessages(conversationId)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        messageViewModel.messages.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvMessagesStatus.text = "Carregando mensagens..."
                }
                is Resource.Success -> {
                    val messages = resource.data
                    if (!messages.isNullOrEmpty()) {
                        binding.tvMessagesStatus.text = "Mensagens: \n" + messages.joinToString("\n") { "${it.senderName}: ${it.content}" }
                    } else {
                        binding.tvMessagesStatus.text = "Nenhuma mensagem ainda."
                    }
                }
                is Resource.Error -> {
                    binding.tvMessagesStatus.text = "Erro ao carregar mensagens: ${resource.message}"
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        messageViewModel.sendMessageState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Mostrar indicador de envio
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Mensagem enviada!", Toast.LENGTH_SHORT).show()
                    binding.etMessageInput.text?.clear()
                    // Recarregar mensagens ou adicionar a nova mensagem à lista
                    messageViewModel.fetchMessages(args.conversationId)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Erro ao enviar mensagem: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSendMessage.setOnClickListener {
            val content = binding.etMessageInput.text.toString().trim()
            if (content.isNotEmpty()) {
                messageViewModel.sendMessage(args.conversationId, content)
            } else {
                Toast.makeText(requireContext(), "A mensagem não pode estar vazia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
