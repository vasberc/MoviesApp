package com.vasberc.data.di

import com.vasberc.data_remote.di.DataRemoteModule
import org.koin.core.annotation.Module

@Module(
    includes = [DataRemoteModule::class, DataRemoteModule::class]
)
class DataModule