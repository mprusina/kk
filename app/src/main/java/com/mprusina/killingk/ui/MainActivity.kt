package com.mprusina.killingk.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeDrawable.BOTTOM_END
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mprusina.killingk.R
import com.mprusina.killingk.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.menu_bottom_sheet.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Declare BottomSheetBehavior instance
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    // View Binding
    private lateinit var binding: ActivityMainBinding

    // ViewModel for this activity
    private val viewModel: MainViewModel by viewModels()

    // Notification badge on toggle menu button
    private lateinit var badge: BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set toolbar
        setSupportActionBar(toolbar)
        // Hide app name from toolbar
        supportActionBar?.title = ""

        // Initialize notification badge
        badge = BadgeDrawable.create(this)

        // Initialize the BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // Disable opening bottom sheet by dragging peek-ed part of sheet from bottom towards the top of screen
        bottomSheetBehavior.isDraggable = false

        // Add button onClick listener to initiate request to complete profile
        complete_profile_button.setOnClickListener {
            if (isOnline()) {
                if (!viewModel.isProfileCompleted()) {
                    updateUiCompleteProfileRequest()
                    viewModel.completeProfile()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_profile_completed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT)
                    .show()
            }

        }

        // Add observer for LiveData holder of response from complete profile request,
        // so that we are notified when we receive response and can do something with data received
        viewModel.getProfileResponse().observe(this) {
            if (it.success) {
                updateUiCompleteProfileSuccess()
                profile_title.text = it.data.title
                profile_message.text = it.data.message
                complete_profile_button.text = getString(R.string.complete_profile_complete_button)
            }
        }
    }

    // Function to hide profile title and message, and show loading indicator
    // when complete profile request started
    private fun updateUiCompleteProfileRequest() {
        // Animation - profile title, message animation (slide to bottom) to View.GONE,
        // progress indicator animation (slide from bottom) to View.VISIBLE
        val viewTransition: Transition = Slide(Gravity.BOTTOM)
        viewTransition.duration = 1000
        viewTransition.addTarget(profile_title).addTarget(profile_message)
            .addTarget(progress_indicator)
        TransitionManager.beginDelayedTransition(binding.root, viewTransition)
        profile_title.visibility = View.GONE
        profile_message.visibility = View.GONE
        progress_indicator.visibility = View.VISIBLE
    }

    // Function to show title and message, and hide loading indicator
    // when complete profile request completed
    private fun updateUiCompleteProfileSuccess() {
        // Animation - profile title, message animation (slide from bottom) to View.VISIBLE,
        // progress indicator animation (slide to bottom) to View.GONE
        val viewTransition: Transition = Slide(Gravity.BOTTOM)
        viewTransition.duration = 1000
        viewTransition.addTarget(progress_indicator).addTarget(profile_title)
            .addTarget(profile_message)
        TransitionManager.beginDelayedTransition(binding.root, viewTransition)
        progress_indicator.visibility = View.GONE
        profile_title.visibility = View.VISIBLE
        profile_message.visibility = View.VISIBLE

        // Animation - profile image border color animation (not completed -> completed)
        val colorFrom: Int = resources.getColor(R.color.kk_red, null)
        val colorTo: Int = resources.getColor(R.color.kk_green, null)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 1000
        colorAnimation.addUpdateListener { animator ->
            profile_image.borderColor = animator.animatedValue as Int
        }
        colorAnimation.start()

        // Animation - complete profile button background drawable animation (not completed -> completed)
        val transitionDrawable: TransitionDrawable =
            complete_profile_button.background as TransitionDrawable
        transitionDrawable.startTransition(1000)
        complete_profile_button.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.complete_profile_button_completed_style,
            null
        )
    }

    // Function to check if device is connected to the internet before initiating network request
    private fun isOnline(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            ) {
                return true
            }
        }
        return false
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        with(badge) {
            badgeGravity = BOTTOM_END
            horizontalOffset = 7
            verticalOffset = 15
            backgroundColor = getColor(R.color.kk_badge)
        }
        BadgeUtils.attachBadgeDrawable(badge, toolbar, menu.getItem(0).itemId)
        viewModel.getProfileResponse().observe(this) {
            if (it.success) {
                BadgeUtils.detachBadgeDrawable(badge, toolbar, menu.getItem(0).itemId)
            }
        }
        return true
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toggle_menu -> {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    // Bottom sheet is being collapsed:
                    // - Hiding icon, name (title), description (subtitle) and profile photo (icon)
                    // - Attaching notification badge if profile is not yet completed
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    if (!viewModel.isProfileCompleted()) {
                        BadgeUtils.attachBadgeDrawable(badge, toolbar, item.itemId)
                    }
                    item.setIcon(R.drawable.baseline_menu_24)
                    with(supportActionBar) {
                        this?.setIcon(null) // toolbar profile photo
                        this?.title = "" // toolbar title (name)
                        this?.subtitle = "" // toolbar subtitle (description)
                    }
                } else {
                    // Bottom sheet is being expanded:
                    // - Displaying icon, name (title), description (subtitle) and profile photo (icon)
                    // - Detaching notification badge, as per design mock
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    with(supportActionBar) {
                        this?.setIcon(R.drawable.profile_photo) // toolbar profile photo
                        this?.title = getString(R.string.profile_name) // toolbar title (name)
                        this?.subtitle =
                            getString(R.string.profile_message)  // toolbar subtitle (description)
                    }
                    BadgeUtils.detachBadgeDrawable(badge, toolbar, item.itemId)
                    item.setIcon(R.drawable.baseline_close_24)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}