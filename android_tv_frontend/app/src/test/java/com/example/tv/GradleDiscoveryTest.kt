package com.example.tv

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PUBLIC_INTERFACE
 * GradleDiscoveryTest
 * A harmless unit test placed under :app/src/test to help tools discover
 * standard Gradle source sets for the Android project.
 *
 * It asserts true and has no runtime impact.
 */
class GradleDiscoveryTest {

    // PUBLIC_INTERFACE
    /**
     * Sanity test that always passes.
     */
    @Test
    fun projectStructureIsDetectable() {
        assertTrue(true)
    }
}
