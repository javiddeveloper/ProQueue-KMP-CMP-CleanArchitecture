package xyz.sattar.javid.proqueue.di

import org.koin.dsl.module

// Common module: keep common bindings here. Platform-specific bindings are provided
// in androidMain/iosMain modules.
val commonModule = module {
    // سایر وابستگی‌های مشترک
    // ...existing code...
    // اضافه کردن dataModule
    includes(xyz.sattar.javid.proqueue.data.dataModule)
}
