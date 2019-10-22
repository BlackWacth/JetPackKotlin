package com.bruce.jetpackkotlin.ui.search


import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bruce.jetpackkotlin.AppExecutors
import com.bruce.jetpackkotlin.R
import com.bruce.jetpackkotlin.binding.FragmentDataBindingComponent
import com.bruce.jetpackkotlin.databinding.FragmentSearchBinding
import com.bruce.jetpackkotlin.di.Injectable
import com.bruce.jetpackkotlin.ui.common.RepoListAdapter
import com.bruce.jetpackkotlin.ui.common.RetryCallback
import com.bruce.jetpackkotlin.util.autoCleared
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<FragmentSearchBinding>()

    var adapter by autoCleared<RepoListAdapter>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    val searchViewModel: SearchViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false,
            dataBindingComponent
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        initRecyclerView()

        val rvAdapter = RepoListAdapter(
            dataBindingComponent,
            appExecutors,
            true
        ) {
        }

        binding.query = searchViewModel.query
        binding.repoList.adapter = rvAdapter
        adapter = rvAdapter

        initSearchInputListener()

        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }

        }
    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }

        binding.input.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(v)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(view: View) {
        dismissKeyboard(view.windowToken)
        searchViewModel.setQuery(binding.input.text.toString())
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun initRecyclerView() {
        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })

        binding.searchResult = searchViewModel.results
        searchViewModel.results.observe(this) {
            adapter.submitList(it.data)
        }

        searchViewModel.loadMoreStatus.observe(this) { loadingMore ->
            binding.loadingMore = loadingMore.isRunning
            val error = loadingMore.errorMessageIfNotHandled
            if (error != null) {
                Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
