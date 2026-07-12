package com.motisoft.herd.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.motisoft.herd.data.local.dao.BreedingDao
import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.dao.DailyLogDao
import com.motisoft.herd.data.local.dao.HealthDao
import com.motisoft.herd.data.local.dao.MilkDao
import com.motisoft.herd.data.local.entity.BreedingInfoEntity
import com.motisoft.herd.data.local.entity.CalvingHistoryEntity
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.data.local.entity.DailyLogEntity
import com.motisoft.herd.data.local.entity.HealthStatusEntity
import com.motisoft.herd.data.local.entity.IllnessLogEntity
import com.motisoft.herd.data.local.entity.MilkRecordEntity
import com.motisoft.herd.data.local.entity.VaccinationEntity

@Database(
    entities = [
        CowEntity::class,
        BreedingInfoEntity::class,
        CalvingHistoryEntity::class,
        MilkRecordEntity::class,
        HealthStatusEntity::class,
        VaccinationEntity::class,
        IllnessLogEntity::class,
        DailyLogEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class HerdDatabase : RoomDatabase() {
    abstract fun cowDao(): CowDao
    abstract fun breedingDao(): BreedingDao
    abstract fun milkDao(): MilkDao
    abstract fun healthDao(): HealthDao
    abstract fun dailyLogDao(): DailyLogDao
}
