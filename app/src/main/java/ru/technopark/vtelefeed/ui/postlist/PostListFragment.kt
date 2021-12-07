package ru.technopark.vtelefeed.ui.postlist

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.databinding.FragmentPostListBinding
import ru.technopark.vtelefeed.ui.FragmentInteractor
import ru.technopark.vtelefeed.ui.auth.AuthFragment
import ru.technopark.vtelefeed.ui.postlist.vk.VKPostAdapter
import ru.technopark.vtelefeed.ui.postlist.vk.VKPostDiffer
import ru.technopark.vtelefeed.ui.postlist.vk.VKPostStorage
import ru.technopark.vtelefeed.utils.viewBinding

class PostListFragment : Fragment(R.layout.fragment_post_list) {

    private val binding: FragmentPostListBinding by viewBinding {
        FragmentPostListBinding.bind(requireView())
    }

    private var fragmentInteractor: FragmentInteractor? = null

    private val vkPostStorage: VKPostStorage by viewModels()
    private val tgPostStorage: TgPostStorage by viewModels()
    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentInteractor = activity as FragmentInteractor
    }


    fun onCheckedChanged(checked: Boolean) {
        // implementation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postListToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account_menu_item -> {
                    fragmentInteractor?.openFragment(AuthFragment())
                    true
                }
                else -> false
            }
        }

        binding.postListToolbar.findViewById<SwitchCompat>(R.id.app_bar_switch).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tgPostStorage.refresh
                Toast.makeText(activity, "))))", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(activity, "((((", Toast.LENGTH_LONG).show()
            }
        }
        binding.postListToolbar.setNavigationOnClickListener {
            fragmentInteractor?.back()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val checked = binding.postListToolbar.findViewById<SwitchCompat>(R.id.app_bar_switch).isChecked
        if (checked) {
            setVkAdapter()
        }
        else {
            setTgAdapter()
        }
        binding.postListToolbar.findViewById<SwitchCompat>(R.id.app_bar_switch).setOnCheckedChangeListener { _, b ->
            if (b) {
                setVkAdapter()
            }
            else {
                setTgAdapter()
            }
        }
    }

    fun setVkAdapter() {
        val adapter = VKPostAdapter(VKPostDiffer())
        binding.recyclerView.adapter = adapter

        vkPostStorage.vkAuthState.observe(viewLifecycleOwner) { logged ->
            val isReady = logged == true

            binding.pleaseAuthText.isGone = isReady
            binding.recyclerView.isVisible = isReady

            if (isReady) {
                vkPostStorage.pagedListLiveData.observe(
                    viewLifecycleOwner,
                    adapter::submitList
                )
            }
        }

        vkPostStorage.refresh.observe(viewLifecycleOwner) { refreshing ->
            binding.postsRefreshLayout.isRefreshing = refreshing
        }

        binding.postsRefreshLayout.setOnRefreshListener {
            vkPostStorage.refreshPostsDatabase()
        }
    }

    fun setTgAdapter() {
        val adapter = TgPostAdapter(TgPostDiffer())
        binding.recyclerView.adapter = adapter

        tgPostStorage.authState.observe(viewLifecycleOwner) { state ->
            val isReady = state is TdApi.AuthorizationStateReady

            binding.pleaseAuthText.isGone = isReady
            binding.recyclerView.isVisible = isReady

            if (isReady) {
                tgPostStorage.pagedListLiveData.observe(
                    viewLifecycleOwner,
                    adapter::submitList
                )
            }
        }

        tgPostStorage.refresh.observe(viewLifecycleOwner) { refreshing ->
            binding.postsRefreshLayout.isRefreshing = refreshing
        }

        binding.postsRefreshLayout.setOnRefreshListener {
            tgPostStorage.refreshPostsDatabase()
        }
    }

    companion object {
        fun newInstance(): PostListFragment {
            return PostListFragment()
        }
    }
}
