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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import com.example.dndMobile.gvcarpool.R.string.email
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.R.attr.entries
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // Reference to firebase authenticator abd DB
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private val rides : ArrayList<DataSnapshot> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Initialize firebase reference
        auth = FirebaseAuth.getInstance()

        // Set DB refernece to Rides DB
        databaseReference = FirebaseDatabase.getInstance().reference.child("Rides")

        viewManager = LinearLayoutManager(this.context)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // The Value event and loop through all Rides adding them to an array
    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            rides.clear()
            for (snapshot in snapshot.children) {
                val ride : DataSnapshot = snapshot
                rides.add(ride)
            }

            // Set adapter once data has been pulled from DB
            viewAdapter = HomeFeedAdapter(rides.toTypedArray()) {rideId: String -> itemClicked(rideId)}

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
        override fun onCancelled(databaseError: DatabaseError) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dividerItemDecoration = DividerItemDecoration(feed_recycler.context, DividerItemDecoration.VERTICAL)
        feed_recycler.addItemDecoration(dividerItemDecoration)

        makeButton.setOnClickListener{ _ ->
            val intent = Intent(this.context, NewCarpoolActivity::class.java)
            startActivity(intent)
        }
    }

    // The function that is called whenever an item is clicked.
    private fun itemClicked(rideId: String) {
        // Make intent to Feed Detail Activity and pass the data
        val intent = Intent(activity, FeedDetailActivity::class.java)
        intent.putExtra("rideId", rideId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Add the value event to the DB refernece
        databaseReference = FirebaseDatabase.getInstance().reference.child("Rides")
        databaseReference!!.addValueEventListener(valueEventListener)
    }

    override fun onPause() {
        super.onPause()
        // Remove the value event to the DB refernece so it does not trigger when new items are being added to the DB
        databaseReference = FirebaseDatabase.getInstance().reference.child("Rides")
        databaseReference!!.removeEventListener(valueEventListener)
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
        fun onFragmentInteraction(uri: Uri)
    }

}

class HomeFeedAdapter(private val dataset: Array<DataSnapshot>, val clickListener: (String) -> Unit) : RecyclerView.Adapter<HomeFeedAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val layout: CardView) : RecyclerView.ViewHolder(layout)
    private var databaseReference: DatabaseReference? = null

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

        // Set DB reference to Users and get the Users info
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userReference = databaseReference!!.child(dataset[position].child("user").value as String)

        // Get User name and Profile Picture
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                 holder.layout.nameField.text = snapshot.child("fullName").value as String
                //TODO: Add user photo URI to user DB entry and pull it down here
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // Get ride into from dataset
        holder.layout.typeField.text = dataset[position].child("type").value as String
        holder.layout.toField.text = dataset[position].child("to").value as String
        holder.layout.fromField.text = dataset[position].child("from").value as String
        holder.layout.whenField.text = dataset[position].child("when").value as String


        // Set the typeField background based on type (Offer/Request)
        if(holder.layout.typeField.text == "Offer"){
            holder.layout.typeField.setBackgroundResource(R.drawable.offer_background)
        }else if(holder.layout.typeField.text == "Request"){
            holder.layout.typeField.setBackgroundResource(R.drawable.request_background)
        }

        // Set the clickListener for each item. Using the function passed as a parameter to the Adapter
        holder.layout.setOnClickListener {
            // Send the ride Id (key) to the clickListner
            clickListener(dataset[position].key!!)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size
}
