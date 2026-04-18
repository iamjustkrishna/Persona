package com.krishnajeena.persona.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.krishnajeena.persona.MusicDataSource
import com.krishnajeena.persona.auth.GoogleAuthUiClient
import com.krishnajeena.persona.components.MusicControllerImpl
import com.krishnajeena.persona.data_layer.BlogUrlDatabase
import com.krishnajeena.persona.data_layer.MusicRepository
import com.krishnajeena.persona.data_layer.MusicRepositoryImpl
import com.krishnajeena.persona.data_layer.NoteDatabase
import com.krishnajeena.persona.model.MusicViewModel
import com.krishnajeena.persona.other.QuoteRepository
import com.krishnajeena.persona.services.MusicController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideNoteDatabase(application: Application): NoteDatabase {
        return Room.databaseBuilder(
            application,
            NoteDatabase::class.java,
            "note_database.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBlogUrlDatabase(application: Application): BlogUrlDatabase {
        return Room.databaseBuilder(
            application,
            BlogUrlDatabase::class.java,
            "blog_url_database.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFocusDatabase(application: Application): com.krishnajeena.persona.data_layer.FocusDatabase {
        return com.krishnajeena.persona.data_layer.FocusDatabase.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideFocusSessionDao(database: com.krishnajeena.persona.data_layer.FocusDatabase): com.krishnajeena.persona.data_layer.FocusSessionDao {
        return database.focusSessionDao()
    }

    @Singleton
    @Provides
    fun provideMusicRepository(
        musicDataSource: MusicDataSource
    ): MusicRepository = MusicRepositoryImpl(musicDataSource)

    @Singleton
    @Provides
    fun provideMusicDataSource(): MusicDataSource = MusicDataSource()


    @Singleton
    @Provides
    fun provideMusicController(@ApplicationContext context: Context): MusicController =
        MusicControllerImpl(context)


    @Provides
    @Singleton
    fun provideJson(): Json{
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            prettyPrint = true
        }
    }

    @Provides
    @Singleton
    fun provideOneTapClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(
        @ApplicationContext context: Context,
        oneTapClient: SignInClient
    ): GoogleAuthUiClient {
        return GoogleAuthUiClient(context, oneTapClient)
    }

    @Provides
    @Singleton
    fun provideThemeManager(@ApplicationContext context: Context): com.krishnajeena.persona.data_layer.ThemeManager {
        return com.krishnajeena.persona.data_layer.ThemeManager(context)
    }

}
