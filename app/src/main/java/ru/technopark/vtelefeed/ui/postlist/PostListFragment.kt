package ru.technopark.vtelefeed.ui.postlist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.technopark.vtelefeed.ui.FragmentInteractor
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.databinding.FragmentPostListBinding
import ru.technopark.vtelefeed.ui.auth.AuthFragment
import ru.technopark.vtelefeed.utils.viewBinding

class PostListFragment : Fragment(R.layout.fragment_post_list) {

    private val binding: FragmentPostListBinding by viewBinding {
        FragmentPostListBinding.bind(requireView())
    }

    private var fragmentInteractor: FragmentInteractor? = null

    private val postStorage: PostStorage by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentInteractor = activity as FragmentInteractor
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

        val adapter = PostAdapter(PostDiffer())
        binding.recyclerView.adapter = adapter

        postStorage.vkAuthState.observe(viewLifecycleOwner) { logged ->
            val isReady = logged == true

            binding.pleaseAuthText.isGone = isReady
            binding.recyclerView.isVisible = isReady

            if (isReady) {
                postStorage.pagedListLiveData.observe(
                    viewLifecycleOwner,
                    adapter::submitList
                )
            }
        }

        postStorage.refresh.observe(viewLifecycleOwner) { refreshing ->
            binding.postsRefreshLayout.isRefreshing = refreshing
        }

        binding.postsRefreshLayout.setOnRefreshListener {
            postStorage.refreshPostsDatabase()
        }
    }

    companion object {
        fun newInstance(): PostListFragment {
            return PostListFragment()
        }
    }
}
