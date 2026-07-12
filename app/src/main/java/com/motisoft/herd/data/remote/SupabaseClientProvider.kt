package com.motisoft.herd.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// The anon/publishable key is safe to embed in client code - it only grants what
// Row Level Security policies on each table allow (see supabase/migrations/0001_init.sql).
private const val SUPABASE_URL = "https://thitdaiznbuxkejvitex.supabase.co"
private const val SUPABASE_ANON_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRoaXRkYWl6bmJ1eGtlanZpdGV4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODM4NjkyNDAsImV4cCI6MjA5OTQ0NTI0MH0.RRuBsC0DrpYPBnrmduFRUug9QD_gfs1-rpqWariKiG4"

object SupabaseClientProvider {
    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY,
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
