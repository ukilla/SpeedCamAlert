package com.example.speedcamalert.fragments

import android.content.Context
import android.os.Bundle
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
import com.example.speedcamalert.PatrolAdapter
import com.example.speedcamalert.R
import com.example.speedcamalert.databinding.FragmentPatrolListBinding
import com.example.speedcamalert.viewmodels.LoggedUserViewModel
import com.example.speedcamalert.viewmodels.PatrolViewModel

class PatrolListFragment : Fragment() {
    private var _binding: FragmentPatrolListBinding? = null
    private val binding get() = _binding!!
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()
    private  val patrolViewModel: PatrolViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatrolListBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()


        return binding.root
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item=menu.findItem(R.id.action_list)
        item.isVisible=false
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val patrolListAdapter= PatrolAdapter(requireContext(),patrolViewModel.patrols)
        binding.patrolListView.adapter=patrolListAdapter

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout ->{
                val sharedPreferences =
                    requireContext().getSharedPreferences("SpeedCamAlert", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                this.findNavController().navigate(R.id.action_patrolListFragment_to_loginFragment)
                true
            }
//            R.id.action_show_scoreboard->{
//                this.findNavController().navigate(R.id.action_strayListFragment_to_leaderboardFragment)
//                true
//            }
            R.id.action_show_map->{
                this.findNavController().navigate(R.id.action_patrolListFragment_to_homeFragment)
                true
            }
            else->super.onContextItemSelected(item)
        }
    }
    companion object {
    }
}