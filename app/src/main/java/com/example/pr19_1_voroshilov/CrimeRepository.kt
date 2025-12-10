package com.example.pr19_1_voroshilov

import android.content.Context
import androidx.room.Room
import database.CrimeDatabase
import database.migration_1_2
import java.util.UUID


private const val DATABASE_NAME = "CrimeDatabase"
class CrimeRepository private constructor(context: Context) {

    private val database : CrimeDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(migration_1_2).build()

    private val crimeDao = database.crimeDao()

    fun getCrimes():List<Crime> = crimeDao.getCrimes()
    fun getCrime(id:UUID):Crime? = crimeDao.getCrime(id)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?:
            throw
            IllegalStateException("CrimeRepository must be initialized")
        }
    }

}
