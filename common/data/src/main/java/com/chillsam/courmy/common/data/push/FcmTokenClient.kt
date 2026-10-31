package com.chillsam.courmy.common.data.push

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FcmTokenClient
    @Inject
    constructor() {
        fun enableAutoInit() {
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
        }

        fun disableAutoInit() {
            FirebaseMessaging.getInstance().isAutoInitEnabled = false
        }

        suspend fun token(): String =
            suspendCancellableCoroutine { continuation ->
                val messaging = FirebaseMessaging.getInstance()
                messaging.token.addOnCompleteListener { task ->
                    if (continuation.isActive) {
                        if (task.isSuccessful) {
                            continuation.resume(task.result)
                        } else {
                            continuation.resumeWithException(
                                task.exception ?: IllegalStateException("FCM token unavailable"),
                            )
                        }
                    }
                }
            }

        suspend fun delete() =
            suspendCancellableCoroutine { continuation ->
                val messaging = FirebaseMessaging.getInstance()
                messaging.isAutoInitEnabled = false
                messaging.deleteToken().addOnCompleteListener { task ->
                    if (continuation.isActive) {
                        if (task.isSuccessful) {
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWithException(
                                task.exception ?: IllegalStateException("FCM deletion failed"),
                            )
                        }
                    }
                }
            }
    }
