package com.motisoft.herd.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.motisoft.herd.data.repository.BreedingRepository
import com.motisoft.herd.data.repository.CowRepository
import com.motisoft.herd.data.repository.DailyLogRepository
import com.motisoft.herd.data.repository.DailyMilkTotalRepository
import com.motisoft.herd.data.repository.HealthRepository
import com.motisoft.herd.data.repository.MilkRepository
import com.motisoft.herd.data.repository.ProfileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cowRepository: CowRepository,
    private val breedingRepository: BreedingRepository,
    private val milkRepository: MilkRepository,
    private val healthRepository: HealthRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val profileRepository: ProfileRepository,
    private val dailyMilkTotalRepository: DailyMilkTotalRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Cows must sync first: child tables need the cow's remote UUID to push.
        cowRepository.syncDirty()
        breedingRepository.syncDirty()
        milkRepository.syncDirty()
        healthRepository.syncDirty()
        dailyLogRepository.syncDirty()
        profileRepository.syncDirty()
        dailyMilkTotalRepository.syncDirty()
        return Result.success()
    }
}
