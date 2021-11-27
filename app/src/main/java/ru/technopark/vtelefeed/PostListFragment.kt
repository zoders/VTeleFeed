package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.databinding.FragmentPostListBinding
import ru.technopark.vtelefeed.utils.viewBinding

class PostListFragment : Fragment(R.layout.fragment_post_list) {

    private val binding: FragmentPostListBinding by viewBinding {
        FragmentPostListBinding.bind(requireView())
    }

    private val postStorage: PostStorage by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = PostAdapter(postStorage.posts, PostDiffer())
        binding.recyclerView.adapter = adapter

        postStorage.authState.observe(viewLifecycleOwner) { state ->
            if (state is TdApi.AuthorizationStateReady) {
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
