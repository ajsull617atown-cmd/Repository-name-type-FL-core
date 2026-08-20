package com.factorylabs.reactor
import android.app.Application
import androidx.room.Room
import com.factorylabs.reactor.library.ReactorDatabase

class ReactorApplication : Application() {
    companion object { lateinit var db: ReactorDatabase }
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, ReactorDatabase::class.java, "reactor-os.db")
            .fallbackToDestructiveMigration().build()
    }
}