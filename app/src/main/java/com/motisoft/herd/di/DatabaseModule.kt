package com.motisoft.herd.di

import android.content.Context
import androidx.room.Room
import com.motisoft.herd.data.local.HerdDatabase
import com.motisoft.herd.data.local.dao.BreedingDao
import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.dao.DailyLogDao
import com.motisoft.herd.data.local.dao.HealthDao
import com.motisoft.herd.data.local.dao.MilkDao
import com.motisoft.herd.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HerdDatabase =
        Room.databaseBuilder(context, HerdDatabase::class.java, "herd.db")
            .addMigrations(HerdDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideCowDao(db: HerdDatabase): CowDao = db.cowDao()

    @Provides
    fun provideBreedingDao(db: HerdDatabase): BreedingDao = db.breedingDao()

    @Provides
    fun provideMilkDao(db: HerdDatabase): MilkDao = db.milkDao()

    @Provides
    fun provideHealthDao(db: HerdDatabase): HealthDao = db.healthDao()

    @Provides
    fun provideDailyLogDao(db: HerdDatabase): DailyLogDao = db.dailyLogDao()

    @Provides
    fun provideProfileDao(db: HerdDatabase): ProfileDao = db.profileDao()
}
