package eu.kanade.tachiyomi.ui.library

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import eu.kanade.core.prefs.CheckboxState
import eu.kanade.domain.chapter.model.Chapter
import eu.kanade.domain.manga.interactor.GetMangaWithChapters
import eu.kanade.domain.manga.model.Manga
import eu.kanade.domain.manga.model.isLocal
import eu.kanade.domain.manga.model.toDbManga
import eu.kanade.presentation.components.ChangeCategoryDialog
import eu.kanade.presentation.components.DeleteLibraryMangaDialog
import eu.kanade.presentation.library.LibraryScreen
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.library.LibraryUpdateService
import eu.kanade.tachiyomi.ui.base.controller.FullComposeController
import eu.kanade.tachiyomi.ui.base.controller.RootController
import eu.kanade.tachiyomi.ui.base.controller.pushController
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.GlobalSearchController
import eu.kanade.tachiyomi.ui.category.CategoryController
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.ui.manga.MangaController
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import eu.kanade.tachiyomi.util.lang.launchIO
import eu.kanade.tachiyomi.util.system.logcat
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class LibraryController(
    bundle: Bundle? = null,
    private val getMangaAndChapters: GetMangaWithChapters = Injekt.get(),
) : FullComposeController<LibraryPresenter>(bundle), RootController {

    /**
     * Sheet containing filter/sort/display items.
     */
    private var settingsSheet: LibrarySettingsSheet? = null

    override fun createPresenter(): LibraryPresenter = LibraryPresenter()

    @Composable
    override fun ComposeContent() {
        val context = LocalContext.current
        LibraryScreen(
            presenter = presenter,
            onMangaClicked = ::openManga,
            onGlobalSearchClicked = {
                router.pushController(GlobalSearchController(presenter.searchQuery))
            },
            onChangeCategoryClicked = ::showMangaCategoriesDialog,
            onMarkAsReadClicked = { markReadStatus(true) },
            onMarkAsUnreadClicked = { markReadStatus(false) },
            onDownloadClicked = ::downloadUnreadChapters,
            onDeleteClicked = ::showDeleteMangaDialog,
            onClickFilter = ::showSettingsSheet,
            onClickRefresh = {
                val started = LibraryUpdateService.start(context, it)
                context.toast(if (started) R.string.updating_library else R.string.update_already_running)
                started
            },
            onClickInvertSelection = { presenter.invertSelection(presenter.activeCategory) },
            onClickSelectAll = { presenter.selectAll(presenter.activeCategory) },
            onClickUnselectAll = ::clearSelection,
        )

        val onDismissRequest = { presenter.dialog = null }
        when (val dialog = presenter.dialog) {
            is LibraryPresenter.Dialog.ChangeCategory -> {
                ChangeCategoryDialog(
                    initialSelection = dialog.initialSelection,
                    onDismissRequest = onDismissRequest,
                    onEditCategories = {
                        presenter.clearSelection()
                        router.pushController(CategoryController())
                    },
                    onConfirm = { include, exclude ->
                        presenter.clearSelection()
                        presenter.setMangaCategories(dialog.manga, include, exclude)
                    },
                )
            }
            is LibraryPresenter.Dialog.DeleteManga -> {
                DeleteLibraryMangaDialog(
                    containsLocalManga = dialog.manga.any(Manga::isLocal),
                    onDismissRequest = onDismissRequest,
                    onConfirm = { deleteManga, deleteChapter ->
                        presenter.removeMangas(dialog.manga.map { it.toDbManga() }, deleteManga, deleteChapter)
                        presenter.clearSelection()
                    },
                )
            }
            null -> {}
        }

        LaunchedEffect(presenter.selectionMode) {
            // Could perhaps be removed when navigation is in a Compose world
            if (router.backstackSize == 1) {
                (activity as? MainActivity)?.showBottomNav(presenter.selectionMode.not())
            }
        }
        LaunchedEffect(presenter.isLoading) {
            if (!presenter.isLoading) {
                (activity as? MainActivity)?.ready = true
            }
        }
    }

    override fun handleBack(): Boolean {
        return when {
            presenter.selection.isNotEmpty() -> {
                presenter.clearSelection()
                true
            }
            presenter.searchQuery != null -> {
                presenter.searchQuery = null
                true
            }
            else -> false
        }
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        settingsSheet = LibrarySettingsSheet(router) { group ->
            when (group) {
                is LibrarySettingsSheet.Filter.FilterGroup -> onFilterChanged()
                is LibrarySettingsSheet.Sort.SortGroup -> onSortChanged()
                is LibrarySettingsSheet.Display.DisplayGroup -> {}
                is LibrarySettingsSheet.Display.BadgeGroup -> onBadgeSettingChanged()
                is LibrarySettingsSheet.Display.TabsGroup -> {} // onTabsSettingsChanged()
            }
        }
    }

    override fun onChangeStarted(handler: ControllerChangeHandler, type: ControllerChangeType) {
        super.onChangeStarted(handler, type)
        if (type.isEnter) {
            presenter.subscribeLibrary()
        }
    }

    override fun onDestroyView(view: View) {
        settingsSheet?.sheetScope?.cancel()
        settingsSheet = null
        super.onDestroyView(view)
    }

    fun showSettingsSheet() {
        presenter.categories.getOrNull(presenter.activeCategory)?.let { category ->
            settingsSheet?.show(category)
        }
    }

    private fun onFilterChanged() {
        presenter.requestFilterUpdate()
        activity?.invalidateOptionsMenu()
    }

    private fun onBadgeSettingChanged() {
        presenter.requestBadgesUpdate()
    }

    private fun onSortChanged() {
        presenter.requestSortUpdate()
    }

    fun search(query: String) {
        presenter.searchQuery = query
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val settingsSheet = settingsSheet ?: return
        presenter.hasActiveFilters = settingsSheet.filters.hasActiveFilters()
    }

    private fun openManga(mangaId: Long) {
        openLastRead(mangaId)

        // Notify the presenter a manga is being opened.
        //presenter.onOpenManga()

        //router.pushController(MangaController(mangaId))
    }

    private fun openLastRead(mangaId: Long) {
        // Notify the presenter a manga is being opened.
        presenter.onOpenManga()

        val presenterScope: CoroutineScope = MainScope()

        presenterScope.launchIO {
            val manga = getMangaAndChapters.awaitManga(mangaId)
            val chapters = getMangaAndChapters.awaitChapters(mangaId)

            var latestUnread = chapters.findLast { !it.read }

            val optimized = chapters.map { it }.let { _ ->
                if (manga.sortDescending()) {
                    chapters.findLast { !it.read }
                } else {
                    chapters.find { !it.read }
                }
            }

            logcat { "te hello is $optimized" }
            logcat { "latestUnread hello is $latestUnread" }

            openChapter(latestUnread!!)
            /*
            if (te != null) {
                openChapter(te)
            }
             */
        }
    }

    //TODO this exists in MangaController so I am repeating myself
    private fun openChapter(chapter: Chapter) {
        activity?.run {
            startActivity(ReaderActivity.newIntent(this, chapter.mangaId, chapter.id))
        }
    }

    /**
     * Clear all of the manga currently selected, and
     * invalidate the action mode to revert the top toolbar
     */
    fun clearSelection() {
        presenter.clearSelection()
    }

    /**
     * Move the selected manga to a list of categories.
     */
    private fun showMangaCategoriesDialog() {
        viewScope.launchIO {
            // Create a copy of selected manga
            val mangaList = presenter.selection.map { it.manga }

            // Hide the default category because it has a different behavior than the ones from db.
            val categories = presenter.categories.filter { it.id != 0L }

            // Get indexes of the common categories to preselect.
            val common = presenter.getCommonCategories(mangaList)
            // Get indexes of the mix categories to preselect.
            val mix = presenter.getMixCategories(mangaList)
            val preselected = categories.map {
                when (it) {
                    in common -> CheckboxState.State.Checked(it)
                    in mix -> CheckboxState.TriState.Exclude(it)
                    else -> CheckboxState.State.None(it)
                }
            }
            presenter.dialog = LibraryPresenter.Dialog.ChangeCategory(mangaList, preselected)
        }
    }

    private fun downloadUnreadChapters() {
        val mangaList = presenter.selection.toList()
        presenter.downloadUnreadChapters(mangaList.map { it.manga })
        presenter.clearSelection()
    }

    private fun markReadStatus(read: Boolean) {
        val mangaList = presenter.selection.toList()
        presenter.markReadStatus(mangaList.map { it.manga }, read)
        presenter.clearSelection()
    }

    private fun showDeleteMangaDialog() {
        val mangaList = presenter.selection.map { it.manga }
        presenter.dialog = LibraryPresenter.Dialog.DeleteManga(mangaList)
    }
}
