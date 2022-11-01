package eu.kanade.domain.category.interactor

import eu.kanade.domain.category.model.Category
import eu.kanade.domain.category.model.CategoryUpdate
import eu.kanade.domain.category.repository.CategoryRepository
import eu.kanade.tachiyomi.util.lang.withNonCancellableContext
import eu.kanade.tachiyomi.util.system.logcat
import logcat.LogPriority

class SetUpdateIntervalForCategory(
    private val categoryRepository: CategoryRepository,
) {
    suspend fun await(categoryId: Long, interval: Long) = withNonCancellableContext {
        val update = CategoryUpdate(
            id = categoryId,
            updateInterval = interval,
        )

        try {
            categoryRepository.updatePartial(update)
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.InternalError(e)
        }
    }

    suspend fun await(category: Category, interval: Long) = await(category.id, interval)

    sealed class Result {
        object Success : Result()
        data class InternalError(val error: Throwable) : Result()
    }
}
