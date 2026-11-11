package com.chillsam.courmy.common.domain.error

import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.domain.helper.StringResource

/** Local input failures carry resource keys; server-provided messages remain unchanged. */
class LocalizedValidationException(
    val resource: StringResource,
    vararg val formatArgs: Any,
) : IllegalArgumentException(resource.name)

inline fun requireLocalized(
    value: Boolean,
    failure: () -> LocalizedValidationException,
) {
    if (!value) throw failure()
}

fun Throwable.localizedValidationMessage(resources: ResourceHelper): String? =
    (this as? LocalizedValidationException)?.let { resources.getString(it.resource, formatArgs = it.formatArgs) }
