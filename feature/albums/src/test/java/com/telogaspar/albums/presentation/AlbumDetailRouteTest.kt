package com.telogaspar.albums.presentation

import junit.framework.TestCase.assertEquals
import org.junit.Test

class AlbumDetailRouteTest {

    @Test
    fun `GIVEN album detail route WHEN reading its argument name THEN it matches the saved state key`() {
        val argumentName = AlbumDetailRoute.serializer().descriptor.getElementName(0)

        assertEquals(AlbumDetailRoute.ALBUM_ID_KEY, argumentName)
    }
}
