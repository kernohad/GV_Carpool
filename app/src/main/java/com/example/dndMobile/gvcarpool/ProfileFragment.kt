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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewOfLayout = inflater.inflate(R.layout.fragment_profile, container, false)



        //TODO: Find out of profile belongs to current user or not
        //      Allow edits to profile if so

        //TODO: Pull profile info from db
        //      :Name
        //      :bio
        //      :photo, if exists.  if not, use default
        //      :common routes

        //TODO: Stop focus when clicked off of bio edit text

        //TODO: Figure out if we want common departures/arrivals to be decided by actual data or user specified
        
        return viewOfLayout
    }


}
