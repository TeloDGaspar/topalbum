package com.telogaspar.albums.di

import com.telogaspar.albums.data.api.TopAlbumsApi
import com.telogaspar.albums.data.remote.TopAlbumsListRemoteDataSource
import com.telogaspar.albums.data.remote.TopAlbumsListRemoteDataSourceImpl
import com.telogaspar.albums.data.repository.AlbumListRepositoryImpl
import com.telogaspar.albums.domain.repository.AlbumListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class AlbumModule {

    @Binds
    @Singleton
    internal abstract fun bindTopAlbumsListRemoteDataSource(
        impl: TopAlbumsListRemoteDataSourceImpl
    ): TopAlbumsListRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindAlbumListRepository(
        impl: AlbumListRepositoryImpl
    ): AlbumListRepository

    companion object {
        @Provides
        @Singleton
        fun provideAlbumEventApi(retrofit: Retrofit): TopAlbumsApi =
            retrofit.create(TopAlbumsApi::class.java)
    }
}