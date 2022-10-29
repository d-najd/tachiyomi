package eu.kanade.data.category

import eu.kanade.domain.category.model.Category

val categoryMapper: (Long, String, Long, Long, Long, Long) -> Category = { id, name, order, updateInterval, lastUpdate, flags ->
    Category(
        id = id,
        name = name,
        order = order,
        updateInterval = updateInterval,
        lastUpdate = lastUpdate,
        flags = flags,
    )
}
