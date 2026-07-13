package com.motisoft.herd.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.motisoft.herd.data.local.dao.BreedingDao
import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.dao.DailyLogDao
import com.motisoft.herd.data.local.dao.HealthDao
import com.motisoft.herd.data.local.dao.MilkDao
import com.motisoft.herd.data.local.dao.ProfileDao
import com.motisoft.herd.data.local.entity.BreedingInfoEntity
import com.motisoft.herd.data.local.entity.CalvingHistoryEntity
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.data.local.entity.DailyLogEntity
import com.motisoft.herd.data.local.entity.HealthStatusEntity
import com.motisoft.herd.data.local.entity.IllnessLogEntity
import com.motisoft.herd.data.local.entity.MilkRecordEntity
import com.motisoft.herd.data.local.entity.ProfileEntity
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
        ProfileEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class HerdDatabase : RoomDatabase() {
    abstract fun cowDao(): CowDao
    abstract fun breedingDao(): BreedingDao
    abstract fun milkDao(): MilkDao
    abstract fun healthDao(): HealthDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun profileDao(): ProfileDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `profiles` (
                        `ownerId` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `farmName` TEXT NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `dirty` INTEGER NOT NULL,
                        PRIMARY KEY(`ownerId`)
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
