package eu.kanade.data.category

import eu.kanade.domain.category.model.Category
import eu.kanade.domain.library.model.LibraryUpdateInterval

val categoryMapper: (Long, String, Long, LibraryUpdateInterval, Long, Long) -> Category = { id, name, order, updateInterval, lastUpdate, flags ->
    Category(
        id = id,
        name = name,
        order = order,
        updateInterval = updateInterval,
        lastUpdate = lastUpdate,
        flags = flags,
    )
}
