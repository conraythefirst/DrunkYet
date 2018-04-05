package io.github.conraythefirst.drunkyet

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.ConditionVariable
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.activity_row.view.*
import kotlinx.android.synthetic.main.dialog_popup.*
import kotlinx.android.synthetic.main.dialog_popup.view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val db = DBhandler(this)
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->

            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_popup, null)
            val editText = dialogView.findViewById<View>(R.id.editText_dialog) as EditText
            dialogBuilder.setView(dialogView)
            dialogBuilder.setTitle("New Drink:")
            dialogBuilder.setPositiveButton("Okay", DialogInterface.OnClickListener { dialog, whichButton ->

                val name = editText.text.toString().replace("'","")
                if (!name.isNullOrBlank()) {
                    
                    if (db.doesExist(name) != true) {
                        val values = ContentValues()
                        values.put("name", name)
                        values.put("amount", 0)
                        db.insert(values)
                        recyclerView.adapter = MainAdapter(this@MainActivity, recyclerView)
                    }
                }
            })

            dialogBuilder.setNegativeButton("Nope", DialogInterface.OnClickListener { dialog, whichButton ->
                //do nothing
            })

            val dialog = dialogBuilder.create()
            dialog.show()

        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MainAdapter(this, recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_clear) {
            val db = DBhandler(this)
            db.delAll()
            recyclerView.adapter = MainAdapter(this, recyclerView)
            true
        } else super.onOptionsItemSelected(item)

    }

    class MainAdapter(val context: Context,val recycler: RecyclerView): RecyclerView.Adapter<MainViewHolder>() {

        val db = DBhandler(context)
        val drinklist = db.getAll()

        override fun getItemCount(): Int {
            return drinklist.count()
        }

        override fun onBindViewHolder(holder: MainViewHolder?, position: Int) {

            val thisdrink = drinklist.get(position)

            holder?.view?.setOnLongClickListener { v ->
                db.delOne(thisdrink.name)

                v.textView_drinkAmount.text
                recycler.adapter = MainAdapter(context,recycler)
                true
            }

            holder?.view?.textView_drinkAmount?.text = thisdrink.amount.toString()
            holder?.view?.textView_drinkName?.text = thisdrink.name

            holder?.view?.button_addDrink?.setOnClickListener { v ->
                db.addAmount(thisdrink.name)
                recycler.adapter = MainAdapter(context,recycler)
            }

            holder?.view?.button_removeDrink?.setOnClickListener { v ->
                val name = thisdrink.name
                val amount = thisdrink.amount

                if (amount.toInt() == 0) {
                    db.delOne(name)
                    recycler.adapter = MainAdapter(context,recycler)
                }
                else {
                    db.delAmount(name)
                    recycler.adapter = MainAdapter(context, recycler)
                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainViewHolder {

            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellReturn = layoutInflater.inflate(R.layout.activity_row, parent, false)
            return MainViewHolder(cellReturn)
        }
    }

    class MainViewHolder(val view: View): RecyclerView.ViewHolder(view)

}

class Drink(val name: String, val amount: Int)