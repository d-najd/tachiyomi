package eu.kanade.domain.library.model

import androidx.annotation.StringRes
import eu.kanade.tachiyomi.R

/**
 * Define update interval for the library and categories
 */
enum class LibraryUpdateInterval(val perfValue: Int, @StringRes val strRes: Int) {
    /**
     * Will use the default interval for library update
     */
    UPDATE_DEFAULT(-1, R.string.update_default),
    UPDATE_NEVER(0, R.string.update_never),
    UPDATE_12_HOUR(12, R.string.update_12hour),
    UPDATE_24_HOUR(24, R.string.update_24hour),
    UPDATE_48_HOUR(48, R.string.update_48hour),
    UPDATE_72_HOUR(72, R.string.update_72hour),
    UPDATE_168_HOUR(168, R.string.update_weekly),
}
