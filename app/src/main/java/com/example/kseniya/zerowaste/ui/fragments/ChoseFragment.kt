package com.example.kseniya.zerowaste.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import butterknife.BindView
import com.example.kseniya.zerowaste.R
import com.example.kseniya.zerowaste.ui.activities.MainActivity
import com.example.kseniya.zerowaste.utils.BitmapUtil
import com.example.kseniya.zerowaste.utils.GestureListener
import kotlinx.android.synthetic.main.fragment_chose.*


class ChoseFragment : BaseFragment(), GestureListener.Callback, View.OnClickListener {
    override fun onClick(v: View?) {
        val points_fragment = PointsInfoFragment()
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    var activity: MainActivity? = null

    var expandedContentYPos: Float = 0f
    var collapsedContentYPos: Float = 0f

    private var gestureListener: GestureListener? = null

    override fun getViewLayout(): Int {
        return R.layout.fragment_chose
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCheckbox()

        contentView.setOnClickListener {
            gestureListener?.let {
                if (it.isCollapsed) {
                    expandView()
                } else {
                    collapseView()
                }
            }
        }
    }

    private fun initCheckbox() {
        checkboxBottle.setOnClickListener(this)
        checkboxGlass.setOnClickListener(this)
        checkboxPaper.setOnClickListener(this)
        checkboxShirt.setOnClickListener(this)
        checkboxBag.setOnClickListener(this)
        checkboxApple.setOnClickListener(this)
        checkboxCow.setOnClickListener(this)
        checkboxMashine.setOnClickListener(this)
    }

    override fun collapseView() {

        if (gestureListener?.isAnimating == false) {
            gestureListener?.isCollapsed = true

            contentView.animate()
                    .translationY(collapsedContentYPos)
                    .setListener(gestureListener?.contentAnimListener)
                    .start()
            collapsedView.alpha = 1f
            expandedView.alpha = 0f
            collapsedView.visibility = View.VISIBLE
            expandedView.visibility = View.GONE

        }
    }

    override fun expandView() {
        if (gestureListener?.isAnimating == false) {
            gestureListener?.isCollapsed = false

            collapsedView.alpha = 0f
            expandedView.alpha = 1f
            collapsedView.visibility = View.GONE
            expandedView.visibility = View.VISIBLE

            contentView.animate()
                    .translationY(expandedContentYPos)
                    .setListener(gestureListener?.contentAnimListener)
                    .start()
        }
    }

    override fun changeAlpha(alpha: Float) {
        collapsedView.alpha = alpha
        expandedView.alpha = 1 - alpha
    }

    override fun getY(): Float {
        return contentView.y
    }

    override fun setY(y: Float) {
        contentView.y = y
    }

    override fun onResume() {
        super.onResume()
        contentView?.post {
            if (isUIAvailable() == true && gestureListener == null) {
                expandedContentYPos = contentView.y
                collapsedContentYPos = expandedContentYPos + expandedView.height - BitmapUtil.dp2px(context, 40)
                gestureListener = GestureListener(collapsedContentYPos, expandedContentYPos, this@ChoseFragment)
                contentView?.setOnTouchListener(gestureListener)
            }

        }

    }
}