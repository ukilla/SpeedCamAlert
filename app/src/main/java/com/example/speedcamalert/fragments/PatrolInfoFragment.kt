package com.example.speedcamalert.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.speedcamalert.R
import com.example.speedcamalert.classes.Patrol
import com.example.speedcamalert.classes.Review
import com.example.speedcamalert.databinding.FragmentPatrolInfoBinding
import com.example.speedcamalert.viewmodels.LoggedUserViewModel
import com.example.speedcamalert.viewmodels.PatrolViewModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale


class PatrolInfoFragment : DialogFragment() {
    private var _binding: FragmentPatrolInfoBinding?=null
    private val binding get() = _binding!!
    private var userRating : Int=0

    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()
    private  val patrolViewModel: PatrolViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatrolInfoBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formatted= formatter.format(patrolViewModel.patrol?.date)
        binding.textViewPatrolDate.text = formatted
        binding.textViewPatrolDescription.text = patrolViewModel.patrol?.description
        binding.textViewPatrolNameAndType.text="${patrolViewModel.patrol?.name} (${patrolViewModel.patrol?.type})"

        return binding.root
    }

    fun onStarClick(view: View) {
        val clickedStar = view as ImageView
        val star1 = binding.star1
        val star2 = binding.star2
        val star3 = binding.star3
        val star4 = binding.star4
        val star5 = binding.star5

        star1.setImageResource(R.drawable.star_rate_icon)
        star2.setImageResource(R.drawable.star_rate_icon)
        star3.setImageResource(R.drawable.star_rate_icon)
        star4.setImageResource(R.drawable.star_rate_icon)
        star5.setImageResource(R.drawable.star_rate_icon)

        when (clickedStar.id) {
            R.id.star5 -> {
                userRating = 5
                star5.setImageResource(R.drawable.star_rate_fill_icon)
                star4.setImageResource(R.drawable.star_rate_fill_icon)
                star3.setImageResource(R.drawable.star_rate_fill_icon)
                star2.setImageResource(R.drawable.star_rate_fill_icon)
                star1.setImageResource(R.drawable.star_rate_fill_icon)
            }
            R.id.star4 -> {
                userRating = 4
                star4.setImageResource(R.drawable.star_rate_fill_icon)
                star3.setImageResource(R.drawable.star_rate_fill_icon)
                star2.setImageResource(R.drawable.star_rate_fill_icon)
                star1.setImageResource(R.drawable.star_rate_fill_icon)
            }
            R.id.star3 -> {
                userRating = 3
                star3.setImageResource(R.drawable.star_rate_fill_icon)
                star2.setImageResource(R.drawable.star_rate_fill_icon)
                star1.setImageResource(R.drawable.star_rate_fill_icon)
            }
            R.id.star2 -> {
                userRating = 2
                star2.setImageResource(R.drawable.star_rate_fill_icon)
                star1.setImageResource(R.drawable.star_rate_fill_icon)
            }
            R.id.star1 -> {
                userRating = 1
                star1.setImageResource(R.drawable.star_rate_fill_icon)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view?.findViewById<ImageView>(R.id.imageViewPatrolPhoto)

        Glide.with(this)
            .load(patrolViewModel.patrol?.photo)
            .into(imageView!!)
        binding.star1.setOnClickListener{
            onStarClick(it)
        }
        binding.star2.setOnClickListener{
            onStarClick(it)
        }
        binding.star3.setOnClickListener{
            onStarClick(it)
        }
        binding.star4.setOnClickListener{
            onStarClick(it)
        }
        binding.star5.setOnClickListener{
            onStarClick(it)
        }
        binding.addCommentButton.setOnClickListener{
            addComment()
        }
        val patrolList=patrolViewModel.getReviewsForPatrol()
        displayListData(patrolList)
    }

    private fun displayListData(dataList: List<Review>) {
        val reviewsLayout = binding.reviewsLayout
        reviewsLayout?.removeAllViews()
        for (review in dataList) {
            val reviewLayout = LinearLayout(requireContext())
            reviewLayout.orientation = LinearLayout.VERTICAL
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            layoutParams.setMargins(0,20,0,0)
            reviewLayout.layoutParams=layoutParams

            val userTextView = TextView(requireContext())
            userTextView.text = review.publisher
            userTextView.textSize = 16f
            userTextView.setTypeface(null, Typeface.BOLD)
            reviewLayout.addView(userTextView)

            val ratingCommentLayout = LinearLayout(requireContext())
            ratingCommentLayout.orientation = LinearLayout.VERTICAL


            val ratingTextView = TextView(requireContext())
            ratingTextView.text = "Ocena: ${review.rating}"
            ratingTextView.textSize = 14f
            ratingCommentLayout.addView(ratingTextView)


            val separator = View(requireContext())
            separator.layoutParams = LinearLayout.LayoutParams(
                0,
                1,
                1f
            )
            separator.setBackgroundColor(Color.BLACK)
            ratingCommentLayout.addView(separator)


            val commentTextView = TextView(requireContext())
            commentTextView.text = "Komentar: ${review.comment}"
            commentTextView.textSize = 14f
            ratingCommentLayout.addView(commentTextView)


            reviewLayout.addView(ratingCommentLayout)

            // Add a separator between reviews
            val reviewSeparator = View(requireContext())
            reviewSeparator.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            )
            reviewSeparator.setBackgroundColor(Color.BLACK)
            reviewLayout.addView(reviewSeparator)

            reviewsLayout?.addView(reviewLayout)
        }
    }

    private fun addComment()
    {
        var comment=binding.commentEditText.text.toString()
        if(userRating!=0 && comment!= "") {
            var points:Long=0
            var newPoints=0
            var review=Review(userRating,comment,loggedUserViewModel.user?.username!!)
            val ownerUsername= patrolViewModel.patrol?.publisher!!

            if(userRating<3) {
                newPoints = -1
            }
            else if(userRating>=3)
                newPoints = 1

            val databaseUser = FirebaseDatabase.getInstance().getReference("Users")
            databaseUser.child(ownerUsername).get().addOnCompleteListener { task ->
                val dataSnapshot = task.result
                points=dataSnapshot.child("points").getValue(Long::class.java)!!
                val total=points+newPoints
                databaseUser.child(ownerUsername).child("points").setValue(total)
            }

            patrolViewModel.addReviewToPatrol(review)
            binding.commentEditText.text?.clear()
            binding.star1.setImageResource(R.drawable.star_rate_icon)
            binding.star2.setImageResource(R.drawable.star_rate_icon)
            binding.star3.setImageResource(R.drawable.star_rate_icon)
            binding.star4.setImageResource(R.drawable.star_rate_icon)
            binding.star5.setImageResource(R.drawable.star_rate_icon)
            loggedUserViewModel.addPointsForComment()
            displayListData( patrolViewModel.getReviewsForPatrol())
        }
        else
        {
            Toast.makeText(this.activity, "Morate odabrati rating i dodati komentar", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

    }
}