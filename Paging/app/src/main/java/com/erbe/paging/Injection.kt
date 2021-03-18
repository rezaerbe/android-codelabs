package com.erbe.paging

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.erbe.paging.api.GithubService
import com.erbe.paging.data.GithubRepository
import com.erbe.paging.db.RepoDatabase
import com.erbe.paging.ui.ViewModelFactory

/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
@ExperimentalPagingApi
object Injection {

    /**
     * Creates an instance of [GithubRepository] based on the [GithubService] and a
     * [GithubLocalCache]
     */
    private fun provideGithubRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), RepoDatabase.getInstance(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideGithubRepository(context))
    }
}