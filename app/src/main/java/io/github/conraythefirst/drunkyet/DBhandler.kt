package io.github.conraythefirst.drunkyet


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by conray on 3/11/18.
 */


open class DBhandler(context: Context) : SQLiteOpenHelper(context, "drinks.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE drinks (name TEXT, amount INT)")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  db?.execSQL("DROP IF TABLE EXISTS drinks")
    }

    fun getAll(): List<Drink> {
        val db = this.writableDatabase
        val query = "SELECT * FROM drinks"
        val cursor = db.rawQuery(query, null)
        val drinks = mutableListOf<Drink>()

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    drinks.add(Drink(
                            //cursor.getInt(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getInt(cursor.getColumnIndex("amount"))))
                } while (cursor.moveToNext())
            }
        } else {
            println("cursor null")
        }
        cursor.close()

        return drinks.toList()
    }

    fun delOne(name: String) {
        val db = this.writableDatabase
        val query = "DELETE FROM drinks WHERE name = '$name'"
        db?.execSQL(query)
    }

    fun delAll() {

        val db = this.writableDatabase
        db?.execSQL("DELETE FROM drinks")

    }

    fun insert(data: ContentValues): Boolean {
        val db = this.writableDatabase
        val success = db.insert("drinks", null, data)
        return (Integer.parseInt("$success") != -1)
    }

    fun addAmount(name: String) {
        val db = this.writableDatabase
        val query = "UPDATE drinks SET amount = amount+1 WHERE name = '$name'"
        db?.execSQL(query)
    }

    fun delAmount(name: String) {
        val db = this.writableDatabase
        val query = "UPDATE drinks SET amount = amount-1 WHERE name = '$name'"
        db?.execSQL(query)
    }

}
