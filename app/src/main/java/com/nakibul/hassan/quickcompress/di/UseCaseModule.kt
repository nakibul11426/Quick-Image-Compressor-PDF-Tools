package com.nakibul.hassan.quickcompress.di

import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import com.nakibul.hassan.quickcompress.domain.usecase.CompressImagesUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.CreatePdfFromImagesUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.GetImageDetailsUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.GetPdfDetailsUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.GetPdfPagesUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.MergePdfUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.SaveCompressedImageUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.SplitPdfUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    
    @Provides
    @ViewModelScoped
    fun provideGetImageDetailsUseCase(
        imageRepository: ImageRepository
    ): GetImageDetailsUseCase {
        return GetImageDetailsUseCase(imageRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideCompressImagesUseCase(
        imageRepository: ImageRepository
    ): CompressImagesUseCase {
        return CompressImagesUseCase(imageRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideSaveCompressedImageUseCase(
        imageRepository: ImageRepository
    ): SaveCompressedImageUseCase {
        return SaveCompressedImageUseCase(imageRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideCreatePdfFromImagesUseCase(
        pdfRepository: PdfRepository
    ): CreatePdfFromImagesUseCase {
        return CreatePdfFromImagesUseCase(pdfRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideMergePdfUseCase(
        pdfRepository: PdfRepository
    ): MergePdfUseCase {
        return MergePdfUseCase(pdfRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideSplitPdfUseCase(
        pdfRepository: PdfRepository
    ): SplitPdfUseCase {
        return SplitPdfUseCase(pdfRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetPdfDetailsUseCase(
        pdfRepository: PdfRepository
    ): GetPdfDetailsUseCase {
        return GetPdfDetailsUseCase(pdfRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetPdfPagesUseCase(
        pdfRepository: PdfRepository
    ): GetPdfPagesUseCase {
        return GetPdfPagesUseCase(pdfRepository)
    }
}
