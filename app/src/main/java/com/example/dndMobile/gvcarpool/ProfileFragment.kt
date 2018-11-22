package com.example.dndMobile.gvcarpool

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {
    private var root: View? = null   // create a global variable which will hold your layout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false)


        //TODO: Find out of profile belongs to current user or not
        //      Allow edits to profile if so

        //TODO: Pull profile info from db and populate fields
        //      :Name
        //      :bio
        //      :photo, if exists.  if not, use default
        //      :common routes
        // EXAMPLE of pull info from data passed to fragment and populating field. DB entry will be stored as argument to fragment

        //TODO: Stop focus when clicked off of bio edit text

        //TODO: Figure out if we want common departures/arrivals to be decided by actual data or user specified
        
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val name = arguments?.getString("data")
            root!!.nameTextView.text = name
        }
    }

    // This is used when a new instance of the profile fragment is made for the Profile Activity so we can send data and populate all the fields.
    // Reference http://chirag-limbachiya.blogspot.com/2017/08/pass-parameters-from-activity-to.html Use this for example of different types.
    companion object {

        // TODO: make data be of type database entry so we can get all info, not just name
        fun newInstance(data: String?): ProfileFragment {

            val args = Bundle()
            args.putString("data", data)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }


}
