package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.slf4j.LoggerFactory


@Database(
    entities = [TodoEntity::class],
    version = 3, // РўРµРєСѓС‰Р°СЏ РІРµСЂСЃРёСЏ Р±Р°Р·С‹ РґР°РЅРЅС‹С…
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        private const val DATABASE_NAME = "todo_database"
        private val logger = LoggerFactory.getLogger(AppDatabase::class.java)

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration() // Р”Р»СЏ СЂР°Р·СЂР°Р±РѕС‚РєРё - СѓРґР°Р»СЏРµРј СЃС‚Р°СЂСѓСЋ Р‘Р” РїСЂРё РїСЂРѕР±Р»РµРјР°С…
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                logger.info("Р’С‹РїРѕР»РЅСЏРµРј РјРёРіСЂР°С†РёСЋ Р±Р°Р·С‹ РґР°РЅРЅС‹С… СЃ РІРµСЂСЃРёРё 1 РЅР° 2")

                db.execSQL("ALTER TABLE todos ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE todos ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")

                val currentTime = System.currentTimeMillis()
                db.execSQL("UPDATE todos SET createdAt = , updatedAt = ")

                logger.info("РњРёРіСЂР°С†РёСЏ 1->2 Р·Р°РІРµСЂС€РµРЅР°")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                logger.info("Р’С‹РїРѕР»РЅСЏРµРј РјРёРіСЂР°С†РёСЋ Р±Р°Р·С‹ РґР°РЅРЅС‹С… СЃ РІРµСЂСЃРёРё 2 РЅР° 3")

                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_isDone ON todos(isDone)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_importance ON todos(importance)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_createdAt ON todos(createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_deadline ON todos(deadline)")

                db.execSQL("ALTER TABLE todos ADD COLUMN searchText TEXT")

                db.execSQL("""
                    UPDATE todos SET searchText =
                    CASE
                        WHEN text IS NOT NULL THEN LOWER(text || ' ' || importance)
                        ELSE LOWER(importance)
                    END
                """)

                logger.info("РњРёРіСЂР°С†РёСЏ 2->3 Р·Р°РІРµСЂС€РµРЅР°")
            }
        }
    }
}
