package com.example.mangiaebasta.model

import android.content.Context
import androidx.room.*

@Entity(tableName = "menu_images")
data class MenuImageEntity(
    @PrimaryKey val mid: Int,
    val imageBase64: String,
    val imageVersion: Int
)

@Dao
interface MenuImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuImage(menuImage: MenuImageEntity)

    @Query("SELECT * FROM menu_images WHERE mid = :mid")
    suspend fun getMenuImage(mid: Int): MenuImageEntity?

    @Query("SELECT * FROM menu_images")
    suspend fun getAllImages(): List<MenuImageEntity>
    // Funzione per svuotare la tabella menu_images
    @Query("DELETE FROM menu_images")
    suspend fun deleteAllMenuImages()
}

@Database(entities = [MenuImageEntity::class], version = 1)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun menuImageDao(): MenuImageDao

    companion object {
        @Volatile
        private var INSTANCE: MenuDatabase? = null

        fun getDatabase(context: Context): MenuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MenuDatabase::class.java,
                    "menu_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
