package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.technopark.vtelefeed.databinding.FragmentPostListBinding
import ru.technopark.vtelefeed.utils.viewBinding

class PostListFragment : Fragment(R.layout.fragment_post_list) {

    private val binding: FragmentPostListBinding by viewBinding {
        FragmentPostListBinding.bind(requireView())
    }

    private val postStorage: PostStorage by lazy {
        ViewModelProvider(this)[PostStorage::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.go_to_accounts -> {
            val rootFr = activity?.supportFragmentManager?.findFragmentByTag("PostListFragment")
            val id = rootFr?.id
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    id!!,
                    AuthFragment(),
                    "AuthFragment"
                )
                ?.addToBackStack(null)
                ?.commit()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = PostAdapter(postStorage.posts, PostDiffer())
        binding.recyclerView.adapter = adapter

        postStorage.authState.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                postStorage.pagedListLiveData.observe(
                    viewLifecycleOwner,
                    adapter::submitList
                )
            }
        }
    }

    companion object {
        fun newInstance(): PostListFragment {
            return PostListFragment()
        }
    }
}
