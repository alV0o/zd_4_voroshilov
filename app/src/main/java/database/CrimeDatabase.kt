package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pr19_1_voroshilov.Crime

@Database(entities = [Crime::class], version=1)
@TypeConverters(CrimeConverters::class)
abstract class CrimeDatabase:RoomDatabase() {
    abstract fun crimeDao(): CrimeDao
}