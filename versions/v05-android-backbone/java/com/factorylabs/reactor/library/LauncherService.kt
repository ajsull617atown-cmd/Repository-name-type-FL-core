package com.factorylabs.reactor.library
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.room.*

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets") suspend fun getAll(): List<GameModel>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(games: List<GameModel>)
}

@Database(entities = [GameModel::class], version = 5)
abstract class ReactorDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
}

class LibraryRepository(private val db: ReactorDatabase) {
    suspend fun syncInstalled(installed: List<GameModel>) = db.assetDao().upsertAll(installed)
}

class PackageScanner(private val ctx: Context) {
    fun scanInstalledApps(): List<GameModel> {
        val pm = ctx.packageManager
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA).map { it.packageName }.toSet()
        val registry = listOf(
            GameModel("palworld-01","PALWORLD","com.factoryline.palworld", installed.contains("com.factoryline.palworld"), true, false,"FactoryLine","#a855f7","100%",120,"PL"),
            GameModel("eldenring-01","ELDEN RING","com.factoryline.eldenring", installed.contains("com.factoryline.eldenring"), true, true,"FactoryLine","#facc15","PLAYED",97,"ER"),
            GameModel("cyberpunk-01","CYBERPUNK 2077","com.factoryline.cyberpunk", installed.contains("com.factoryline.cyberpunk"), true, false,"FactoryLine","#06b6d4","NEW",134,"CP"),
            GameModel("fortnite-01","FORTNITE","com.epicgames.fortnite", installed.contains("com.epicgames.fortnite"), true, false,"FactoryLine","#f97316","SYNC",231,"FN")
        )
        val extra = installed.take(300).map { pkg -> GameModel(pkg, pkg.split(".").last().uppercase(), pkg, true, true, false, "ANDROID", "#333", "INSTALLED", 60, pkg.take(2).uppercase()) }
        return (registry + extra).distinctBy { it.packageId }
    }
}

class LauncherService(private val ctx: Context) {
    fun launchOrStoreFallback(game: GameModel) {
        if (!game.isInstalled) { openStore(game.packageId); return }
        try {
            val launchIntent = ctx.packageManager.getLaunchIntentForPackage(game.packageId)
            if (launchIntent != null) ctx.startActivity(launchIntent) else openStore(game.packageId)
        } catch (e: Exception) { openStore(game.packageId) }
    }
    private fun openStore(packageId: String) {
        try {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageId")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageId")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}