package com.alquimia.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alquimia.databinding.FragmentChatListBinding
import com.alquimia.ui.chat.ChatListViewModel
import com.alquimia.ui.chat.ConversationAdapter
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log // Adicione esta importação no topo do arquivo

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val chatListViewModel: ChatListViewModel by viewModels()
    private lateinit var conversationAdapter: ConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        chatListViewModel.fetchUserConversations()
        setupObservers()
    }

    private fun setupRecyclerView() {
        conversationAdapter = ConversationAdapter { conversation ->
            val otherUserId = conversation.otherUser?.id ?: ""
            Log.d("ChatListFragment", "Navegando para MessagesFragment:")
            Log.d("ChatListFragment", "  Conversation ID: ${conversation.id}")
            Log.d("ChatListFragment", "  Other User ID: $otherUserId")

            val action = ChatListFragmentDirections.actionChatListFragmentToMessagesFragment(
                conversation.id,
                otherUserId
            )
            findNavController().navigate(action)
        }
        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }
    }

    private fun setupObservers() {
        chatListViewModel.conversations.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvChatListStatus.visibility = View.VISIBLE
                    binding.tvChatListStatus.text = "Carregando conversas..."
                }
                is Resource.Success -> {
                    binding.tvChatListStatus.visibility = View.GONE
                    resource.data?.let { conversations ->
                        if (conversations.isNotEmpty()) {
                            conversationAdapter.submitList(conversations)
                        } else {
                            binding.tvChatListStatus.visibility = View.VISIBLE
                            binding.tvChatListStatus.text = "Nenhuma conversa encontrada."
                        }
                    } ?: run {
                        binding.tvChatListStatus.visibility = View.VISIBLE
                        binding.tvChatListStatus.text = "Erro: Dados de conversas nulos inesperados."
                        Toast.makeText(requireContext(), "Erro: Dados de conversas nulos inesperados.", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    binding.tvChatListStatus.visibility = View.VISIBLE
                    binding.tvChatListStatus.text = "Erro ao carregar conversas: ${resource.message}"
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
