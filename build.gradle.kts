import com.android.builder.files.classpathToRelativeFileSet

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Declaramos los plugins, pero con apply false. Solo para que estén disponibles.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
}