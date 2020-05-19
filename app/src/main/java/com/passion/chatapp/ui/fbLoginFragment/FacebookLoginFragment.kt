package com.passion.chatapp.ui.fbLoginFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.passion.chatapp.R
import com.passion.chatapp.Utils.AuthUtil
import com.passion.chatapp.Utils.ErrorMessage
import com.passion.chatapp.Utils.eventbus_events.CallbackManagerEvent
import com.passion.chatapp.databinding.FacebookLoginFragmentBinding
import com.passion.chatapp.ui.main_activity.SharedViewModel
import org.greenrobot.eventbus.EventBus


class FacebookLoginFragment : Fragment() {
    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: FacebookLoginFragmentBinding

    companion object {
        fun newInstance() = FacebookLoginFragment()
    }

    private lateinit var viewModel: FacebookLoginViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.facebook_login_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FacebookLoginViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(this.activity!!).get(SharedViewModel::class.java)
        callbackManager = CallbackManager.Factory.create()
        EventBus.getDefault().post(CallbackManagerEvent(callbackManager))


        binding.FBloginButton.loginBehavior = LoginBehavior.WEB_ONLY
        binding.FBloginButton.setPermissions("email", "public_profile")
        binding.FBloginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                viewModel.handleFacebookAccessToken(
                    AuthUtil.firebaseAuthInstance,
                    loginResult.accessToken
                ).observe(this@FacebookLoginFragment, Observer { firebaseUser ->
                    viewModel.isUserAlreadyStoredInFirestore(firebaseUser.uid)
                        .observe(this@FacebookLoginFragment, Observer { isUserStoredInFirestore ->
                            if (!isUserStoredInFirestore) {
                                viewModel.storeFacebookUserInFirebase().observe(
                                    this@FacebookLoginFragment,
                                    Observer { isStoredSuccessfully ->
                                                    if (isStoredSuccessfully) {
                                                        navigateToHome()
                                                    }
                                                })
                                    } else {
                                        navigateToHome()
                                    }
                                })
                        })

            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {
                ErrorMessage.errorMessage = error.message
            }
        })

        viewModel.loadState.observe(this, Observer {
        })

    }

    private fun navigateToHome() {

        try {
            if (parentFragment?.javaClass.toString() == "class com.passion.chatapp.ui.login.LoginFragment") {

                this@FacebookLoginFragment.findNavController()
                    .navigate(R.id.action_loginFragment_to_homeFragment)
            } else if (parentFragment?.javaClass.toString() == "class com.passion.chatapp.ui.signup.SignupFragment") {
                this@FacebookLoginFragment.findNavController()
                    .navigate(R.id.action_signupFragment_to_homeFragment)
            }

        } catch (e: Exception) {
            println("FacebookLoginFragment.navigateToHome:${e.message}")
        }
    }


}
