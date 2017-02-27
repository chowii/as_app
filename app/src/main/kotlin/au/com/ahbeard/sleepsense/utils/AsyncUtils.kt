package au.com.ahbeard.sleepsense.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.Executors

/**
 * Created by luisramos on 30/01/2017.
 *
 * This is mostly taken from anko library Async.kt file
 *
 * https://github.com/Kotlin/anko
 */

class SSAsyncContext<T>(val weakRef: WeakReference<T>)

fun <T> SSAsyncContext<T>.uiThread(f: (T) -> Unit): Boolean {
    val ref = weakRef.get() ?: return false
    if (ContextHelper.mainThread == Thread.currentThread()) {
        f(ref)
    } else {
        ContextHelper.handler.post { f(ref) }
    }
    return true
}

fun <T: Activity> SSAsyncContext<T>.activityUiThread(f: (T) -> Unit): Boolean {
    val activity = weakRef.get() ?: return false
    if (activity.isFinishing) return false
    activity.runOnUiThread { f(activity) }
    return true
}

fun <T: Activity> SSAsyncContext<T>.activityUiThreadWithContext(f: Context.(T) -> Unit): Boolean {
    val activity = weakRef.get() ?: return false
    if (activity.isFinishing) return false
    activity.runOnUiThread { activity.f(activity) }
    return true
}

fun <T: Fragment> SSAsyncContext<T>.fragmentUiThread(f: (T) -> Unit): Boolean {
    val fragment = weakRef.get() ?: return false
    if (fragment.isDetached) return false
    val activity = fragment.activity ?: return false
    activity.runOnUiThread { f(fragment) }
    return true
}

fun <T: Fragment> SSAsyncContext<T>.fragmentUiThreadWithContext(f: Context.(T) -> Unit): Boolean {
    val fragment = weakRef.get() ?: return false
    if (fragment.isDetached) return false
    val activity = fragment.activity ?: return false
    activity.runOnUiThread { activity.f(fragment) }
    return true
}



private val crashLogger = { throwable: Throwable -> throwable.printStackTrace() }

fun <T> T.doAsync(
        exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
        task: SSAsyncContext<T>.() -> Unit
) : Future<Unit> {
    val context = SSAsyncContext(WeakReference(this))
    return BackgroundExecutor.submit {
        try {
            context.task()
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr) ?: Unit
        }
    }
}

internal object BackgroundExecutor {
    var executor: ExecutorService =
            Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors())

    fun <T> submit(task: () -> T): Future<T> {
        return executor.submit(task)
    }
}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
    val mainThread: Thread = Looper.getMainLooper().thread
}