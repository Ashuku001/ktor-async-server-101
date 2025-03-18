package com.example.plugins.di

import com.example.plugins.dao.user.UserDao
import com.example.plugins.dao.user.UserDaoImpl
import com.example.plugins.repository.user.UserRepository
import com.example.plugins.repository.user.UserRepositoryImpl
import org.koin.dsl.module

// dependency injections
val appModule = module {
    single<UserRepository> {UserRepositoryImpl(get())}
    single<UserDao> {UserDaoImpl()}
}