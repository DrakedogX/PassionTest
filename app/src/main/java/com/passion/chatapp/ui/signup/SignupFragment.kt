package com.passion.chatapp.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.passion.chatapp.R
import com.passion.chatapp.Utils.AuthUtil
import com.passion.chatapp.Utils.ErrorMessage
import com.passion.chatapp.Utils.LoadState
import com.passion.chatapp.Utils.eventbus_events.KeyboardEvent
import com.passion.chatapp.databinding.SignupFragmentBinding
import kotlinx.android.synthetic.main.issue_layout.view.*
import org.greenrobot.eventbus.EventBus
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignupFragment : Fragment() {

    private lateinit var binding: SignupFragmentBinding
    private lateinit var pattern: Pattern

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var viewModel: SignupViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.signup_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)

        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"
        pattern = Pattern.compile(emailRegex)

        binding.registerButton.setOnClickListener {
            signUp()
        }

        binding.issueLayout.cancelImage.setOnClickListener {
            binding.issueLayout.visibility = View.GONE
        }

        viewModel.loadingState.observe(this, Observer {
            when (it) {
                LoadState.LOADING -> {
                    binding.loadingLayout.visibility = View.VISIBLE
                    binding.issueLayout.visibility = View.GONE
                }
                LoadState.SUCCESS -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.issueLayout.visibility = View.GONE
                }
                LoadState.FAILURE -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.issueLayout.visibility = View.VISIBLE
                    binding.issueLayout.textViewIssue.text = ErrorMessage.errorMessage
                }

            }
        })

        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            signUp()
            true
        }

    }

    private fun signUp() {
        EventBus.getDefault().post(KeyboardEvent())

        binding.userName.isErrorEnabled = false
        binding.email.isErrorEnabled = false
        binding.password.isErrorEnabled = false


        if (binding.userName.editText!!.text.length < 4) {
            binding.userName.error = "유저명은 4글자 이상 입니다."
            return
        }

        if (!binding.email.editText!!.text.isEmpty()) {
            val matcher: Matcher = pattern.matcher(binding.email.editText!!.text)
            if (!matcher.matches()) {
                binding.email.error = "올바르지 않은 이메일 형태 입니다."
                return
            }
        } else if (binding.email.editText!!.text.isEmpty()) {
            binding.email.error = "이메일 항목이 비어 있습니다."
            return
        }


        if (binding.password.editText!!.text.length < 6) {
            binding.password.error = "패스워드는 6자리 이상입니다."
            return
        }
        
        viewModel.registerEmail(
            AuthUtil.firebaseAuthInstance,
            binding.email.editText!!.text.toString(),
            binding.password.editText!!.text.toString(),
            binding.userName.editText!!.text.toString()
        )


        viewModel.navigateToHomeMutableLiveData.observe(this, Observer { navigateToHome ->
            if (navigateToHome != null && navigateToHome) {
                this@SignupFragment.findNavController()
                    .navigate(R.id.action_signupFragment_to_homeFragment)
                Toast.makeText(context, "회원가입 완료", Toast.LENGTH_LONG).show()
                viewModel.doneNavigating()
            }
        })

    }

}



