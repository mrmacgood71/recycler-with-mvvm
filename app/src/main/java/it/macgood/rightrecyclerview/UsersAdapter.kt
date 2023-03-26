package it.macgood.rightrecyclerview

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.macgood.rightrecyclerview.databinding.ItemUserBinding
import it.macgood.rightrecyclerview.model.User
import it.macgood.rightrecyclerview.screens.UserListItem

interface UserActionListener {

    fun onUserMove(user: User, moveBy: Int)

    fun onUserDelete(user: User)

    fun onUserDetails(user: User)

}

class UsersDiffCallback(
    private val oldList: List<UserListItem>,
    private val newList: List<UserListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]

        return oldUser.user.id == newUser.user.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser == newUser
    }
}

class UsersAdapter(
    private val actionListener: UserActionListener
) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener{

var users: List<UserListItem> = emptyList()

    set(newValue){
        val diffCallback = UsersDiffCallback(field, newValue)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        field = newValue
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)

        binding.moreImageButton.setOnClickListener(this)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val userItem = users[position]
        with(holder.binding) {
            holder.itemView.tag = userItem
            moreImageButton.tag = userItem

            if (userItem.isInProgress) {
                moreImageButton.visibility = View.INVISIBLE
                moreProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            } else {
                moreImageButton.visibility = View.VISIBLE
                moreProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@UsersAdapter)
            }

            usernameText.text = userItem.user.name
            companyNameText.text = userItem.user.company
            if (userItem.user.photo.isNotBlank()) {
                Glide.with(photoImageView)
                    .load(userItem.user.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .into(photoImageView)
            } else {
                Glide.with(photoImageView.context).clear(photoImageView)
                photoImageView.setImageResource(R.drawable.ic_user_avatar)
            }
        }
    }

    override fun onClick(v: View) {
        val user = v.tag as UserListItem

        when(v.id){
            R.id.more_image_button -> {
                showPopupMenu(v)
            }
            else -> {
                actionListener.onUserDetails(user.user)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val userListItem = view.tag as UserListItem
        val position = users.indexOfFirst { it.user.id == userListItem.user.id }

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, "Move up").apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, "Move down").apply {
            isEnabled = position < users.size - 1
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "Remove")

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                ID_MOVE_UP -> {
                    actionListener.onUserMove(userListItem.user, -1)
                }
                ID_MOVE_DOWN -> {
                    actionListener.onUserMove(userListItem.user, 1)
                }
                ID_REMOVE -> {
                    actionListener.onUserDelete(userListItem.user)
                }

            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()

    }

    override fun getItemCount(): Int = users.size

    class UsersViewHolder(
        val binding: ItemUserBinding
    ):RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }

}