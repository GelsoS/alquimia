package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.alquimia.databinding.FragmentMessagesBinding
import com.alquimia.ui.chat.MessageViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alquimia.data.remote.TokenManager // Importar TokenManager
import com.alquimia.ui.chat.MessageAdapter // Importar MessageAdapter
import io.socket.client.IO
import io.socket.client.Socket
import com.google.gson.Gson // Para parsear mensagens do Socket.IO
import com.alquimia.data.remote.models.MessageData // Para o modelo de dados da mensagem

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val messageViewModel: MessageViewModel by viewModels()
    private val args: MessagesFragmentArgs by navArgs()

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var socket: Socket
    private val gson = Gson() // Instância de Gson para desserialização

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
        val otherUserId = args.otherUserId
        val otherUserName = args.otherUserName // Agora o argumento existe
        val currentUserId = TokenManager.currentUserId // Obter o ID do usuário logado

        Log.d("MessagesFragment", "MessagesFragment recebido:")
        Log.d("MessagesFragment", "  Conversation ID: $conversationId")
        Log.d("MessagesFragment", "  Other User ID: $otherUserId")
        Log.d("MessagesFragment", "  Other User Name: $otherUserName")
        Log.d("MessagesFragment", "  Current User ID: $currentUserId")

        // Configurar Toolbar
        binding.toolbarMessages.title = otherUserName
        binding.toolbarMessages.setNavigationOnClickListener {
            findNavController().navigateUp() // Volta para a tela anterior
        }

        // Remover os TextViews de ID da Conversa e Outro Usuário ID, pois não estão mais no layout
        // binding.tvConversationId.text = "Conversa ID: $conversationId"
        // binding.tvOtherUserId.text = "Outro Usuário ID: $otherUserId"

        // Verificar se o currentUserId é nulo antes de passar para o adapter
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Erro: ID do usuário logado não encontrado.", Toast.LENGTH_LONG).show()
            findNavController().navigateUp() // Voltar se não houver usuário logado
            return
        }

        setupRecyclerView(currentUserId) // Passar currentUserId para o adapter
        messageViewModel.fetchMessages(conversationId)

        setupSocketIo(conversationId) // Configurar Socket.IO

        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView(currentUserId: String?) {
        messageAdapter = MessageAdapter(currentUserId) // Passar o ID do usuário atual (agora pode ser nulo)
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true // Mensagens mais recentes na parte inferior
            }
            adapter = messageAdapter
        }
    }

    private fun setupSocketIo(conversationId: String) {
        try {
            val opts = IO.Options()
            opts.forceNew = true
            opts.reconnection = true
            opts.query = "token=${TokenManager.authToken}" // Enviar token JWT se necessário para autenticação no Socket.IO

            // Use a mesma BASE_URL do NetworkModule
            val baseUrl = "http://192.168.3.19:3000/" // Certifique-se de que esta URL corresponde à do NetworkModule
            socket = IO.socket(baseUrl, opts)

            socket.on(Socket.EVENT_CONNECT) {
                Log.d("Socket.IO", "Conectado ao Socket.IO!")
                socket.emit("joinConversation", conversationId)
            }.on("newMessage") { args ->
                args.firstOrNull()?.let { data ->
                    try {
                        val message = gson.fromJson(data.toString(), MessageData::class.java)
                        Log.d("Socket.IO", "Nova mensagem recebida: ${message.content}")
                        // Adicionar a nova mensagem à lista e rolar para o final
                        val currentList = messageAdapter.currentList.toMutableList()
                        currentList.add(message)
                        messageAdapter.submitList(currentList) {
                            binding.rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
                        }
                    } catch (e: Exception) {
                        Log.e("Socket.IO", "Erro ao parsear mensagem: ${e.message}", e)
                    }
                }
            }.on(Socket.EVENT_DISCONNECT) {
                Log.d("Socket.IO", "Desconectado do Socket.IO.")
            }.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("Socket.IO", "Erro de conexão: ${args.firstOrNull()}")
            }

            socket.connect()
        } catch (e: Exception) {
            Log.e("Socket.IO", "Erro ao inicializar Socket.IO: ${e.message}", e)
            Toast.makeText(requireContext(), "Erro ao conectar ao chat em tempo real.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupObservers() {
        messageViewModel.messages.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Não há mais tvMessagesStatus, o RecyclerView estará vazio ou com um spinner
                    // Você pode adicionar um ProgressBar aqui se quiser
                }
                is Resource.Success -> {
                    val messages = resource.data
                    if (!messages.isNullOrEmpty()) {
                        messageAdapter.submitList(messages) {
                            binding.rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
                        }
                    } else {
                        messageAdapter.submitList(emptyList()) // Limpa a lista se não houver mensagens
                        Toast.makeText(requireContext(), "Nenhuma mensagem ainda.", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    messageAdapter.submitList(emptyList()) // Limpa a lista em caso de erro
                    Toast.makeText(requireContext(), "Erro ao carregar mensagens: ${resource.message}", Toast.LENGTH_LONG).show()
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
                    // A mensagem será adicionada via Socket.IO, não precisamos chamar fetchMessages novamente
                    // messageViewModel.fetchMessages(args.conversationId)
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
        socket.disconnect() // Desconectar do Socket.IO quando o fragmento for destruído
        _binding = null
    }
}
