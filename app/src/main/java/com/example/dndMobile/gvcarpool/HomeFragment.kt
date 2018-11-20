package com.example.dndMobile.gvcarpool

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_item.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import android.support.v7.widget.DividerItemDecoration



class HomeFragment : Fragment() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        viewManager = LinearLayoutManager(this.context)
        // TODO: Remove when we don't need the dummy data anymore
        val testArray = ArrayList<String>()
        for (x in 1..10) {
            testArray.add("User $x")
        }
        viewAdapter = HomeFeedAdapter(testArray.toTypedArray())

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dividerItemDecoration = DividerItemDecoration(feed_recycler.context, DividerItemDecoration.VERTICAL)
        feed_recycler.addItemDecoration(dividerItemDecoration)

        makeButton.setOnClickListener{ _ ->
            val intent = Intent(this.context, NewCarpoolActivity::class.java)
            startActivity(intent)
        }

        feed_recycler.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}

class HomeFeedAdapter(private val dataset: Array<String>) : RecyclerView.Adapter<HomeFeedAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val layout: CardView) : RecyclerView.ViewHolder(layout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFeedAdapter.ViewHolder {
        // create a new view
        val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false) as CardView
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(layout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // TODO: This is where we update the field in each item with the correct info. For example, update nameField below
        holder.layout.nameField.text = dataset[position]

        //TODO: Remove this if code. Just to show sample data
        if(position % 2 == 0){
            holder.layout.typeField.text = "Request"
        }

        if(holder.layout.typeField.text == "Offer"){
            holder.layout.typeField.setBackgroundResource(R.drawable.offer_background)
        }else if(holder.layout.typeField.text == "Request"){
            holder.layout.typeField.setBackgroundResource(R.drawable.request_background)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size
}
