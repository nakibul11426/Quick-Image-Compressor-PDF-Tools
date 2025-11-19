package com.nakibul.hassan.quickcompress.di

import android.content.Context
import com.nakibul.hassan.quickcompress.data.repository.ImageRepositoryImpl
import com.nakibul.hassan.quickcompress.data.repository.PdfRepositoryImpl
import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context
    ): ImageRepository {
        return ImageRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun providePdfRepository(
        @ApplicationContext context: Context
    ): PdfRepository {
        return PdfRepositoryImpl(context)
    }
}
