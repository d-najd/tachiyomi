package eu.kanade.domain.category.model

import eu.kanade.domain.library.model.LibraryUpdateInterval

data class CategoryUpdate(
    val id: Long,
    val name: String? = null,
    val order: Long? = null,
    val updateInterval: LibraryUpdateInterval? = LibraryUpdateInterval.UPDATE_DEFAULT,
    val lastUpdate: Long? = null,
    val flags: Long? = null,
)
