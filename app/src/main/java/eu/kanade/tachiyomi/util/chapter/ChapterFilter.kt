package eu.kanade.tachiyomi.util.chapter

import eu.kanade.domain.chapter.model.Chapter
import eu.kanade.domain.manga.model.Manga
import eu.kanade.domain.manga.model.TriStateFilter
import eu.kanade.domain.manga.model.isLocal
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.model.Download


private fun List<Chapter>.applyFilters(manga: Manga, downloadManager: DownloadManager): Sequence<Chapter> {
    val isLocalManga = manga.isLocal()
    val unreadFilter = manga.unreadFilter
    val downloadedFilter = manga.downloadedFilter
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
        .filter { chapter ->
            val downloaded = downloadManager.isChapterDownloaded(chapter.name, chapter.scanlator, manga.title, manga.source)
            val downloadState = when {
                downloaded -> Download.State.DOWNLOADED
                else -> Download.State.NOT_DOWNLOADED
            }
            when (downloadedFilter) {
                TriStateFilter.DISABLED -> true
                TriStateFilter.ENABLED_IS -> downloadState == Download.State.DOWNLOADED  || isLocalManga
                TriStateFilter.ENABLED_NOT -> downloadState != Download.State.DOWNLOADED && !isLocalManga
            }
        }.sortedWith { (chapter1), (chapter2) -> getChapterSort(manga).invoke(ch) }
}
