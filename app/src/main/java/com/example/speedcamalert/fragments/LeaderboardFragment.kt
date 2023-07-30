package com.example.speedcamalert.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.speedcamalert.LeaderboardAdapter
import com.example.speedcamalert.R
import com.example.speedcamalert.databinding.FragmentLeaderboardBinding
import com.example.speedcamalert.viewmodels.UsersViewModel

class LeaderboardFragment : Fragment() {


    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private  val usersViewModel: UsersViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item=menu.findItem(R.id.action_show_scoreboard)
        item.isVisible=false
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =FragmentLeaderboardBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val leaderboardAdapter= LeaderboardAdapter(requireContext(),usersViewModel.users!!)
        Log.d("KORISNICI",usersViewModel.users?.size.toString())
        binding.listViewLeaderboard.adapter=leaderboardAdapter
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout ->{
                val sharedPreferences =
                    requireContext().getSharedPreferences("SpeedCamAlert", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                this.findNavController().navigate(R.id.action_leaderboardFragment_to_loginFragment)
                true
            }
            R.id.action_list->{
                this.findNavController().navigate(R.id.action_leaderboardFragment_to_patrolListFragment)
                true
            }
            R.id.action_show_map->{
                this.findNavController().navigate(R.id.action_leaderboardFragment_to_homeFragment)
                true
            }
            R.id.action_show_profile->{
                this.findNavController().navigate(R.id.action_leaderboardFragment_to_profileFragment)
                true
            }
            else->super.onContextItemSelected(item)
        }
    }

    companion object {}

}