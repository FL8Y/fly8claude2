package com.fly8.clone

import com.fly8.clone.data.RejoinTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RejoinTargetTest {
    @Test fun numericPlaceId() {
        assertEquals("roblox://placeId=123", RejoinTarget.normalize("123"))
        assertEquals("roblox://placeId=123", RejoinTarget.normalize("  123 "))
    }

    @Test fun httpsGamesUrl() {
        assertEquals("roblox://placeId=920587237",
            RejoinTarget.normalize("https://www.roblox.com/games/920587237/Adopt-Me"))
        assertEquals("roblox://placeId=606849621",
            RejoinTarget.normalize("https://roblox.com/games/606849621"))
    }

    @Test fun robloxScheme() {
        assertEquals("roblox://placeId=42", RejoinTarget.normalize("roblox://placeId=42"))
    }

    @Test fun rejectsJunk() {
        assertNull(RejoinTarget.normalize(""))
        assertNull(RejoinTarget.normalize("0"))
        assertNull(RejoinTarget.normalize("-5"))
        assertNull(RejoinTarget.normalize("https://evil.com/games/1"))
        assertNull(RejoinTarget.normalize("https://www.roblox.com.evil.com/games/1"))
        assertNull(RejoinTarget.normalize("roblox://placeId=1; rm -rf"))
        assertNull(RejoinTarget.normalize("javascript:alert(1)"))
        assertNull(RejoinTarget.normalize("123 456"))
    }
}
