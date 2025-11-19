package xyz.sattar.javid.proqueue.di

/**
 * Platform-specific Koin initialization. Provide a platformContext (Android Context on Android,
 * null on iOS) when calling.
 */
expect fun initKoin(platformContext: Any?)
