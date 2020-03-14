package ru.alexskvortsov.policlinic.ui.fragments.auth

import kotlinx.android.synthetic.main.item_authorization_title_holder.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.states.auth.AuthTitleItem
import ru.alexskvortsov.policlinic.ui.utils.delegate.DelegateAdapter

class AuthTitleAdapter : DelegateAdapter<AuthTitleItem>() {

    override fun onBind(item: AuthTitleItem, holder: DelegateViewHolder) =
        with(holder.itemView) {
            titleAuthorizationHolder.text = item.text
        }

    override fun getLayoutId(): Int = R.layout.item_authorization_title_holder

    override fun isForViewType(items: List<*>, position: Int): Boolean =
        items[position] is AuthTitleItem
}