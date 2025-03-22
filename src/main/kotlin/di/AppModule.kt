package com.example.plugins.di

import com.example.plugins.dao.follows.FollowsDao
import com.example.plugins.dao.follows.FollowsDaoImpl
import com.example.plugins.dao.post.PostDao
import com.example.plugins.dao.post.PostDaoImpl
import com.example.plugins.dao.post_likes.PostLikeDao
import com.example.plugins.dao.post_likes.PostLikeDaoImpl
import com.example.plugins.dao.user.UserDao
import com.example.plugins.dao.user.UserDaoImpl
import com.example.plugins.repository.auth.AuthRepository
import com.example.plugins.repository.auth.AuthRepositoryImpl
import com.example.plugins.repository.follows.FollowsRepository
import com.example.plugins.repository.follows.FollowsRepositoryImpl
import com.example.plugins.repository.post.PostRepository
import com.example.plugins.repository.post.PostRepositoryImpl
import com.example.plugins.repository.profile.ProfileRepository
import com.example.plugins.repository.profile.ProfileRepositoryImpl
import org.koin.dsl.module

// dependency injections
val appModule = module {
    single<AuthRepository> {AuthRepositoryImpl(get())}
    single<UserDao> {UserDaoImpl()}

    single<FollowsRepository> {FollowsRepositoryImpl(get(), get())}
    single<FollowsDao> {FollowsDaoImpl()}

    single<PostLikeDao> {PostLikeDaoImpl()}

    single<PostDao> {PostDaoImpl()}
    single<PostRepository> {PostRepositoryImpl(get(), get(), get())}

    single<ProfileRepository> {ProfileRepositoryImpl(get(), get())}
}