package it.macgood.rightrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import it.macgood.rightrecyclerview.databinding.ActivityMainBinding
import it.macgood.rightrecyclerview.model.User
import it.macgood.rightrecyclerview.model.UsersListener
import it.macgood.rightrecyclerview.model.UsersService
import it.macgood.rightrecyclerview.screens.UserDetailsFragment
import it.macgood.rightrecyclerview.screens.UsersListFragment

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    private val actions = mutableListOf<() -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, UsersListFragment())
                .commit()
        }
    }

    override fun showDetails(user: User) {
        runWhenActive {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, UserDetailsFragment.newInstance(user.id))
                .commit()
        }
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun toast(messagesRes: Int) {
        Toast.makeText(this, messagesRes, Toast.LENGTH_SHORT).show()
    }
    /**
     * Avoiding [IllegalStateException] if navigation action has been called after restoring app from background.
     * For example: press "Delete" button in details screen, minimize app and then restore it.
     */
    private fun runWhenActive(action: () -> Unit) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            // activity is active -> just execute the action
            action()
        } else {
            // activity is not active -> add action to queue
            actions += action
        }
    }
}