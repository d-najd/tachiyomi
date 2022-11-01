package eu.kanade.domain.category.model

data class CategoryUpdate(
    val id: Long,
    val name: String? = null,
    val order: Long? = null,
    val updateInterval: Long? = null,
    val lastUpdate: Long? = null,
    val flags: Long? = null,
)
