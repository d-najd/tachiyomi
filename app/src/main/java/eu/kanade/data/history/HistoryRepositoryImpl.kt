package eu.kanade.data.history

import eu.kanade.data.DatabaseHandler
import eu.kanade.data.chapter.chapterMapper
import eu.kanade.data.manga.mangaMapper
import eu.kanade.domain.chapter.model.Chapter
import eu.kanade.domain.history.model.HistoryUpdate
import eu.kanade.domain.history.model.HistoryWithRelations
import eu.kanade.domain.history.repository.HistoryRepository
import eu.kanade.domain.manga.model.Manga
import eu.kanade.domain.manga.model.TriStateFilter
import eu.kanade.tachiyomi.util.system.logcat
import kotlinx.coroutines.flow.Flow
import logcat.LogPriority

class HistoryRepositoryImpl(
    private val handler: DatabaseHandler,
) : HistoryRepository {

    override fun getHistory(query: String): Flow<List<HistoryWithRelations>> {
        return handler.subscribeToList {
            historyViewQueries.history(query, historyWithRelationsMapper)
        }
    }

    override suspend fun getLastHistory(): HistoryWithRelations? {
        return handler.awaitOneOrNull {
            historyViewQueries.getLatestHistory(historyWithRelationsMapper)
        }
    }

    override suspend fun getNextChapter(mangaId: Long): Chapter? {
        val manga = handler.awaitOne { mangasQueries.getMangaById(mangaId, mangaMapper) }

        val chapters = handler.awaitList { chaptersQueries.getChaptersByMangaId(mangaId, chapterMapper) }
            .applyFilters(manga)
            .sortedWith(sortFunction(manga))

        return chapters.find { !it.read }
    }

    private fun List<Chapter>.applyFilters(manga: Manga): Sequence<Chapter> {
        // val isLocalManga = manga.isLocal()
        val unreadFilter = manga.unreadFilter
        // val downloadedFilter = manga.downloadedFilter
        val bookmarkedFilter = manga.bookmarkedFilter

        return asSequence()
            .filter { chapter ->
                when (unreadFilter) {
                    TriStateFilter.DISABLED -> true
                    TriStateFilter.ENABLED_IS -> !chapter.read
                    TriStateFilter.ENABLED_NOT -> chapter.read
                }
            }
            .filter { chapter ->
                when (bookmarkedFilter) {
                    TriStateFilter.DISABLED -> true
                    TriStateFilter.ENABLED_IS -> chapter.bookmark
                    TriStateFilter.ENABLED_NOT -> !chapter.bookmark
                }
            }
            /* TODO implement download filter
            .filter { chapter ->
                when (downloadedFilter) {
                    TriStateFilter.DISABLED -> true
                    TriStateFilter.ENABLED_IS -> chapter.isDownloaded || isLocalManga
                    TriStateFilter.ENABLED_NOT -> !chapter.isDownloaded && !isLocalManga
                }
            }
             */
    }

    override suspend fun getNextChapter(mangaId: Long, chapterId: Long): Chapter? {
        val chapter = handler.awaitOne { chaptersQueries.getChapterById(chapterId, chapterMapper) }
        val manga = handler.awaitOne { mangasQueries.getMangaById(mangaId, mangaMapper) }

        if (!chapter.read) {
            return chapter
        }

        val chapters = handler.awaitList { chaptersQueries.getChaptersByMangaId(mangaId, chapterMapper) }
            .sortedWith(sortFunction(manga))

        val currChapterIndex = chapters.indexOfFirst { chapter.id == it.id }
        return when (manga.sorting) {
            Manga.CHAPTER_SORTING_SOURCE -> chapters.getOrNull(currChapterIndex + 1)
            Manga.CHAPTER_SORTING_NUMBER -> {
                val chapterNumber = chapter.chapterNumber

                ((currChapterIndex + 1) until chapters.size)
                    .map { chapters[it] }
                    .firstOrNull {
                        it.chapterNumber > chapterNumber &&
                            it.chapterNumber <= chapterNumber + 1
                    }
            }
            Manga.CHAPTER_SORTING_UPLOAD_DATE -> {
                chapters.drop(currChapterIndex + 1)
                    .firstOrNull { it.dateUpload >= chapter.dateUpload }
            }
            else -> throw NotImplementedError("Unknown sorting method")
        }
    }

    private fun sortFunction(manga: Manga) = Comparator<Chapter> { c1, c2 ->
        when (manga.sorting) {
            Manga.CHAPTER_SORTING_SOURCE -> { c2.sourceOrder.compareTo(c1.sourceOrder) }
            Manga.CHAPTER_SORTING_NUMBER -> { c1.chapterNumber.compareTo(c2.chapterNumber) }
            Manga.CHAPTER_SORTING_UPLOAD_DATE -> { c1.dateUpload.compareTo(c2.dateUpload) }
            else -> throw NotImplementedError("Unknown sorting method")
        }
    }

    override suspend fun resetHistory(historyId: Long) {
        try {
            handler.await { historyQueries.resetHistoryById(historyId) }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, throwable = e)
        }
    }

    override suspend fun resetHistoryByMangaId(mangaId: Long) {
        try {
            handler.await { historyQueries.resetHistoryByMangaId(mangaId) }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, throwable = e)
        }
    }

    override suspend fun deleteAllHistory(): Boolean {
        return try {
            handler.await { historyQueries.removeAllHistory() }
            true
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, throwable = e)
            false
        }
    }

    override suspend fun upsertHistory(historyUpdate: HistoryUpdate) {
        try {
            handler.await {
                historyQueries.upsert(
                    historyUpdate.chapterId,
                    historyUpdate.readAt,
                    historyUpdate.sessionReadDuration,
                )
            }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, throwable = e)
        }
    }
}
