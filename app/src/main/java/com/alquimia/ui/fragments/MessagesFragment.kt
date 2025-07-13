package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alquimia.databinding.FragmentMessagesBinding
import com.alquimia.ui.chat.MessageViewModel
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import androidx.navigation.fragment.findNavController
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import com.alquimia.ui.chat.MessageAdapter
import com.alquimia.data.remote.TokenManager // Importe TokenManager
import com.alquimia.data.remote.models.MessageData // Importe MessageData
import com.google.gson.Gson // Para parsear JSON de mensagens do Socket.IO
import com.alquimia.di.NetworkModule // Para obter a BASE_URL

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val messageViewModel: MessageViewModel by viewModels()
    private val args: MessagesFragmentArgs by navArgs()

    private lateinit var socket: Socket
    private lateinit var messageAdapter: MessageAdapter
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = TokenManager.currentUserId // Obtenha o ID do usuário logado
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
             findNavController().popBackStack() // Voltar se não houver ID de usuário
            return
        }

        val conversationId = args.conversationId
        val otherUserId = args.otherUserId

        Log.d("MessagesFragment", "MessagesFragment recebido:")
        Log.d("MessagesFragment", "  Conversation ID: $conversationId")
        Log.d("MessagesFragment", "  Other User ID: $otherUserId")
        Log.d("MessagesFragment", "  Current User ID: $currentUserId")

        binding.tvConversationId.text = "Conversa ID: $conversationId"
        binding.tvOtherUserId.text = "Outro Usuário ID: $otherUserId"

        setupRecyclerView()
        setupSocketIo(conversationId)
        messageViewModel.fetchMessages(conversationId) // Carrega mensagens iniciais

        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(currentUserId!!) // currentUserId não será nulo aqui
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true // Para que as novas mensagens apareçam na parte inferior
            }
            adapter = messageAdapter
        }
    }

    private fun setupSocketIo(conversationId: String) {
        try {
            val options = IO.Options()
            options.forceNew = true // Garante uma nova conexão
            options.reconnection = true
            options.reconnectionAttempts = 5
            options.reconnectionDelay = 1000
            options.timeout = 10000
            options.transports = arrayOf("websocket") // Prioriza WebSockets

            // Use a mesma BASE_URL do NetworkModule
            val baseUrl = NetworkModule.provideRetrofit(NetworkModule.provideOkHttpClient(
                NetworkModule.provideHttpLoggingInterceptor(),
                NetworkModule.provideAuthInterceptor(NetworkModule.provideSharedPreferencesManager(requireContext()))
            )).baseUrl().toString()

            socket = IO.socket(baseUrl, options)

            socket.on(Socket.EVENT_CONNECT, Emitter.Listener {
                Log.d("Socket.IO", "Conectado ao Socket.IO!")
                socket.emit("joinConversation", conversationId) // Entra na sala da conversa
            })
                .on("newMessage", Emitter.Listener { args ->
                    val data = args[0]
                    Log.d("Socket.IO", "Nova mensagem recebida: $data")
                    if (data is String) {
                        try {
                            val message = Gson().fromJson(data, MessageData::class.java)
                            activity?.runOnUiThread {
                                val currentList = messageAdapter.currentList.toMutableList()
                                currentList.add(message)
                                messageAdapter.submitList(currentList) {
                                    binding.rvMessages.scrollToPosition(currentList.size - 1) // Rola para a última mensagem
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Socket.IO", "Erro ao parsear mensagem JSON: ${e.message}")
                        }
                    } else if (data is Map<*, *>) { // Se for um objeto JSON diretamente
                        try {
                            val message = Gson().fromJson(Gson().toJson(data), MessageData::class.java)
                            activity?.runOnUiThread {
                                val currentList = messageAdapter.currentList.toMutableList()
                                currentList.add(message)
                                messageAdapter.submitList(currentList) {
                                    binding.rvMessages.scrollToPosition(currentList.size - 1)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Socket.IO", "Erro ao parsear mensagem Map: ${e.message}")
                        }
                    }
                })
                .on(Socket.EVENT_DISCONNECT, Emitter.Listener {
                    Log.d("Socket.IO", "Desconectado do Socket.IO.")
                })
                .on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener { args ->
                    Log.e("Socket.IO", "Erro de conexão: ${args[0]}")
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Erro de conexão com o chat: ${args[0]}", Toast.LENGTH_LONG).show()
                    }
                })

            socket.connect()
        } catch (e: Exception) {
            Log.e("Socket.IO", "Erro ao inicializar Socket.IO: ${e.message}")
            Toast.makeText(requireContext(), "Erro ao inicializar chat: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupObservers() {
        messageViewModel.messages.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // O RecyclerView pode mostrar um spinner ou estado de carregamento
                    // binding.tvMessagesStatus.text = "Carregando mensagens..." // Removido, agora o RV gerencia
                }
                is Resource.Success -> {
                    val messages = resource.data
                    if (!messages.isNullOrEmpty()) {
                        messageAdapter.submitList(messages) {
                            binding.rvMessages.scrollToPosition(messages.size - 1) // Rola para a última mensagem
                        }
                    } else {
                        messageAdapter.submitList(emptyList()) // Limpa a lista se não houver mensagens
                        Toast.makeText(requireContext(), "Nenhuma mensagem ainda.", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Erro ao carregar mensagens: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        messageViewModel.sendMessageState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSendMessage.isEnabled = false
                    binding.etMessageInput.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnSendMessage.isEnabled = true
                    binding.etMessageInput.isEnabled = true
                    binding.etMessageInput.text?.clear()
                    // A mensagem já será adicionada via Socket.IO, então não precisamos chamar fetchMessages novamente
                    // messageViewModel.fetchMessages(args.conversationId) // Removido
                }
                is Resource.Error -> {
                    binding.btnSendMessage.isEnabled = true
                    binding.etMessageInput.isEnabled = true
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
        socket.disconnect() // Desconecta do Socket.IO
        socket.off() // Remove todos os listeners
        _binding = null
    }
}
