package eu.kanade.data.category

import eu.kanade.domain.category.model.Category
import eu.kanade.domain.library.model.LibraryUpdateInterval

val categoryMapper: (Long, String, Long, LibraryUpdateInterval, Long, Long) -> Category = { id, name, order, updateInterval, lastUpdated, flags ->
    Category(
        id = id,
        name = name,
        order = order,
        updateInterval = updateInterval,
        lastUpdated = lastUpdated,
        flags = flags,
    )
}
