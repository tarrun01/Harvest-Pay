# Room entities and DAOs are processed at compile time. Keep generic signatures used by coroutines.
-keepattributes Signature
-keep class kotlin.Metadata { *; }
