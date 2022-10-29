package eu.kanade.domain.category.model

import eu.kanade.domain.library.model.LibraryUpdateInterval
import java.io.Serializable

data class Category(
    val id: Long,
    val name: String,
    val order: Long,
    val updateInterval: LibraryUpdateInterval,
    val lastUpdate: Long,
    val flags: Long,
) : Serializable {

    val isSystemCategory: Boolean = id == UNCATEGORIZED_ID

    companion object {

        const val UNCATEGORIZED_ID = 0L
    }
}

internal fun List<Category>.anyWithName(name: String): Boolean {
    return any { name == it.name }
}
