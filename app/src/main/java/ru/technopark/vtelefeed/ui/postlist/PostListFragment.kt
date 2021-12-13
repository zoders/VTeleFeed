package ru.technopark.vtelefeed.ui.postlist

import android.content.Context
import android.os.Bundle
import android.view.View
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
import ru.technopark.vtelefeed.ui.postlist.tg.TgPostAdapter
import ru.technopark.vtelefeed.ui.postlist.tg.TgPostDiffer
import ru.technopark.vtelefeed.ui.postlist.tg.TgPostStorage
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

        fragmentInteractor = activity as? FragmentInteractor
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

        binding.postListToolbar.setNavigationOnClickListener {
            fragmentInteractor?.back()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        when (pageType) {
            PageType.TG -> {
                setTgAdapter()
            }
            PageType.VK -> {
                setVkAdapter()
            }
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_tg -> {
                    setTgAdapter()
                    pageType = PageType.TG
                    true
                }
                R.id.page_vk -> {
                    setVkAdapter()
                    pageType = PageType.VK
                    true
                }
                else -> false
            }
        }
    }

    private fun setVkAdapter() {
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

    private fun setTgAdapter() {
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
        private var pageType = PageType.VK

        private enum class PageType {
            VK, TG
        }
        private const val TAG = "PostListFragment"
    }
}
