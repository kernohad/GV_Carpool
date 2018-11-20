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

//        val s = ScrollingMovementMethod()
        viewOfLayout.aboutTextView.movementMethod = ScrollingMovementMethod()


        //TODO: Find out of profile belongs to current user or not
        //      Allow edits to profile if so
        return viewOfLayout
    }


}
