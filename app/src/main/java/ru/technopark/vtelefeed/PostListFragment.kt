package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class PostListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var adapter: PostAdapter? = null
    private val postStorage: PostStorage by lazy {
        ViewModelProvider(this).get(PostStorage::class.java)
    }

    override fun onCreateView(
	    inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
        val view = inflater.inflate(R.layout.fragment_post_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PostAdapter(postStorage.posts, PostDiffer())
        val factory = PostSourceFactory(postStorage)
        val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(2).build()
        val pagedListLiveData = LivePagedListBuilder(factory, config).setFetchExecutor(Executors.newSingleThreadExecutor()).build()
        pagedListLiveData.observe(viewLifecycleOwner, object : Observer<PagedList<Post>> {
            override fun onChanged(t: PagedList<Post>?) {
                adapter?.submitList(t) ?: throw NullPointerException()
            }
        })
        recyclerView.adapter = adapter
        return view
    }

    companion object {
        fun newInstance(): PostListFragment {
            return PostListFragment()
        }
    }
}
