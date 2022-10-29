package eu.kanade.tachiyomi.data.backup.models

import eu.kanade.domain.category.model.Category
import eu.kanade.domain.library.model.LibraryUpdateInterval
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class BackupCategory(
    @ProtoNumber(1) var name: String,
    @ProtoNumber(2) var order: Long = 0,
    @ProtoNumber(3) val updateInterval: LibraryUpdateInterval = LibraryUpdateInterval.UPDATE_DEFAULT,
    @ProtoNumber(4) val lastUpdate: Long = -1,
    // @ProtoNumber(3) val updateInterval: Int = 0, 1.x value not used in 0.x
    // Bump by 100 to specify this is a 0.x value
    @ProtoNumber(100) var flags: Long = 0,
) {
    fun getCategory(): Category {
        return Category(
            id = 0,
            name = this@BackupCategory.name,
            updateInterval = this@BackupCategory.updateInterval,
            lastUpdate = this@BackupCategory.lastUpdate,
            order = this@BackupCategory.order,
            flags = this@BackupCategory.flags,
        )
    }
}

val backupCategoryMapper = { category: Category ->
    BackupCategory(
        name = category.name,
        order = category.order,
        updateInterval = category.updateInterval,
        lastUpdate = category.lastUpdate,
        flags = category.flags,
    )
}
