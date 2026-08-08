package com.joshgm3z.netplayer.di

import android.content.Context
import androidx.room.Room
import com.joshgm3z.netplayer.repository.room.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext context: Context
    ): AppDb {
        return Room.databaseBuilder(
            context = context,
            klass = AppDb::class.java,
            name = "netplayer_app_db"
        ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideVideoLinkDao(appDb: AppDb) = appDb.videoLinkDao()
}