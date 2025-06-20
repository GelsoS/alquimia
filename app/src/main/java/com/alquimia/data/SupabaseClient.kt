//package com.alquimia.data
//
//import com.alquimia.BuildConfig
//import io.github.jan.supabase.SupabaseClient
//import io.github.jan.supabase.createSupabaseClient
//import io.github.jan.supabase.gotrue.GoTrue
//import io.github.jan.supabase.postgrest.Postgrest
//import io.github.jan.supabase.realtime.Realtime
//import io.github.jan.supabase.storage.Storage
//import javax.inject.Singleton
//
//fun createSupabase(): SupabaseClient {
//    return createSupabaseClient(
//        supabaseUrl = BuildConfig.SUPABASE_URL,
//        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
//    ) {
//        install(GoTrue)
//        install(Postgrest)
//        install(Storage)
//        install(Realtime)
//    }
//}
