package com.leroymerlin.pandroid.demo.main.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import com.leroymerlin.pandroid.demo.R
import com.leroymerlin.pandroid.extension.adapter
import com.leroymerlin.pandroid.ui.list.recyclerview.HolderFactory
import com.leroymerlin.pandroid.ui.list.recyclerview.PandroidAdapter
import com.leroymerlin.pandroid.ui.list.recyclerview.RecyclerHolder

/**
 * Created by florian on 28/02/2018.
 */
class KotlinRVFragment : ListFragment() {

    val rvMenu: RecyclerView by lazy { view.findViewById<RecyclerView>(R.id.recycler_view_rv) }
    val adapter: PandroidAdapter<String> by lazy { rvMenu.adapter as PandroidAdapter<String> }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //tag::KotlinDSL[]
        rvMenu.apply {
            this.adapter = adapter<String> {
                holder {
                    layout = R.layout.cell_list
                    itemType = 0 //your can use class has type, hashcode will be used
                    binder = { view, data, index -> (view as Button).text = data }
                }
                holder {
                    layout = R.layout.cell_list
                    itemType = 1
                    holderClass = CustomTxtHolder::class
                }
                holder {
                    itemType = 2
                    factory = CustomHolderFactory()
                }
                itemTypes { adapter: PandroidAdapter<String>, item: String, position: Int -> position % 3 }

            }
            this.layoutManager = LinearLayoutManager(activity)
        }
        this.adapter.addAll(data)
        //end::KotlinDSL[]


    }

    class CustomTxtHolder(view: View) : RecyclerHolder<String>(view) {
        override fun setContent(content: String?, index: Int) {
            (itemView as Button).text = "custom holder class  $content"
        }
    }

    class CustomHolderFactory() : HolderFactory.SimpleHolderFactory<String>(R.layout.cell_list) {
        override fun createHolder(cellView: View?): RecyclerHolder<String> {
            return object : RecyclerHolder<String>(cellView) {
                override fun setContent(content: kotlin.String?, index: kotlin.Int) {
                    (itemView as Button).text = "custom factory : $content"
                }
            }
        }
    }



}
