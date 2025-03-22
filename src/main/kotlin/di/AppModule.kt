package com.example.plugins.di

import com.example.plugins.dao.follows.FollowsDao
import com.example.plugins.dao.follows.FollowsDaoImpl
import com.example.plugins.dao.user.UserDao
import com.example.plugins.dao.user.UserDaoImpl
import com.example.plugins.repository.auth.AuthRepository
import com.example.plugins.repository.auth.AuthRepositoryImpl
import com.example.plugins.repository.follows.FollowsRepository
import com.example.plugins.repository.follows.FollowsRepositoryImpl
import org.koin.dsl.module

// dependency injections
val appModule = module {
    single<AuthRepository> {AuthRepositoryImpl(get())}
    single<UserDao> {UserDaoImpl()}
    single<FollowsRepository> {FollowsRepositoryImpl(get(), get())}
    single<FollowsDao> {FollowsDaoImpl()}
}