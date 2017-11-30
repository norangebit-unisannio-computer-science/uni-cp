package it.unisannio.cp.orange.score30.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ListFragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import it.unisannio.cp.orange.score30.R
import it.unisannio.cp.orange.score30.Serie
import it.unisannio.cp.orange.score30.SerieAdapter
import it.unisannio.cp.orange.score30.activity.MainActivity
import java.util.*

/*
 *  Author: Raffaele Mignone
 *  Mat: 863/747
 *  Date: 18/11/17
 *
 */


class SerieMaster : ListFragment() {
    private val map : TreeMap<String, Serie> = TreeMap()
    private var onClickListener : OnClickListener? = null
    private var order = 1

    interface OnClickListener{
        fun onClickListener(item: Serie)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is OnClickListener)
            onClickListener = context
        else
            Log.e(MainActivity.ERROR, "unmatching type")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        read()
        val sp = activity.getSharedPreferences("settings", Activity.MODE_PRIVATE)
        if(!sp.getBoolean("pro", false))
            map.put(getString(R.string.buy_pro), Serie("Buy Pro", 10f))
        listAdapter = SerieAdapter(sortByValue(map), context)
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        registerForContextMenu(listView)
    }

    override fun onStart() {
        super.onStart()
        val sp = context.getSharedPreferences("settings", Activity.MODE_PRIVATE)
        order = sp.getInt("order", 1)
        listAdapter = SerieAdapter(sortByValue(map), context)
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val item = listView.getItemAtPosition(position) as Serie
        if(item.nome == getString(R.string.buy_pro)) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.bitcoin_uri)))
            if(intent.resolveActivity(activity.packageManager) != null)
                startActivity(intent)
            else
                paymentDialog().show()
        }else
            onClickListener?.onClickListener(listView.getItemAtPosition(position) as Serie)
    }

    fun refresh(item: Serie){
        map.put(item.nome, item)
        listAdapter = SerieAdapter(sortByValue(map), context)
        save(item)
    }

    fun add(name: String): Boolean{
        if(map.containsKey(name)) return false
        map.put(name, Serie(name, 0f))
        listAdapter = SerieAdapter(sortByValue(map), context)
        save(Serie(name, 0f))
        return true
    }

    fun delete(item: Serie): Boolean{
        map.remove(item.nome)
        listAdapter = SerieAdapter(sortByValue(map), context)
        remove(item.nome)
        return true
    }

    fun toZero(item: Serie): Boolean{
        if(item.score==0f) true
        item.score=0f
        map.put(item.nome, item)
        listAdapter = SerieAdapter(sortByValue(map), context)
        save(item)
        return true
    }

    private fun sortByValue(unsort: TreeMap<String, Serie>): TreeMap<String, Serie> {
        val sort = TreeMap<String, Serie>(Comparator<String> { o1, o2 ->
            val a1 = (unsort[o1] as Serie).score
            val a2 = (unsort[o2] as Serie).score
            var result = a2.compareTo(a1)*order
            if (result == 0)
                result = o1.compareTo(o2)
            result
        })
        sort.putAll(unsort)
        return sort
    }

    fun save(item: Serie){
        Log.d("SAVE", "in")
        val sp = context.getSharedPreferences("list", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putFloat(item.nome, item.score)
        editor.apply()
    }

    fun remove(nome: String){
        Log.d("SAVE", "in")
        val sp = context.getSharedPreferences("list", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove(nome)
        editor.apply()
    }

    fun read(){
        Log.d("Read", "in")
        val sp = context.getSharedPreferences("list", Activity.MODE_PRIVATE)
        for(s in sp.all.keys)
            map.put(s, Serie(s, sp.getFloat(s, 0f)))
    }

    private fun paymentDialog(): AlertDialog{
        val view = activity.layoutInflater.inflate(R.layout.payment_dialog, null)
        val qr = view.findViewById<ImageView>(R.id.qr)
        val path = "android.resource://" + activity.packageName + "/" + R.raw.qr_code
        qr.setImageURI(Uri.parse(path))
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setTitle(getString(R.string.buy_pro))
        builder.setPositiveButton("ok", null)
        return builder.create()
    }
}