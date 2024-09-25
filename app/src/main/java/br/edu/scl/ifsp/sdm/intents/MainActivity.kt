package br.edu.scl.ifsp.sdm.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_DIAL
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import br.edu.scl.ifsp.sdm.intents.Extras.PARAMETER_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    /* Bloco não mais necessário pois agora implementamos o método registerForActivityResult
    companion object{
        private const val PAREMETER_REQUEST_CODE = 0
    }*/

    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var callPhonePermissionArl: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        parameterArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getStringExtra(PARAMETER_EXTRA)?.also {
                        activityMainBinding.parameterTv.text = it
                    }
                }
            }

        callPhonePermissionArl = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                callPhone(call = true)
            } else {
                Toast.makeText(this,
                    getString(R.string.permission_required_to_call), Toast.LENGTH_SHORT).show()
            }
        }

        activityMainBinding.apply {
            parameterBt.setOnClickListener {
                val parameterIntent =
                    Intent(this@MainActivity, ParameterActivity::class.java).apply {
                        putExtra(PARAMETER_EXTRA, parameterTv.text)
                    }
                parameterArl.launch(parameterIntent)
                //startActivityForResult(parameterIntent, PAREMETER_REQUEST_CODE) //startActivityForResult deprecated
            }
        }
    }

    /* Bloco não mais necessário pois agora implementamos o método registerForActivityResult

        @Deprecated("Deprecated in Java")
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PAREMETER_REQUEST_CODE && resultCode == RESULT_OK){
                data?.getStringExtra(PARAMETER_EXTRA)?.also {
                    activityMainBinding.parameterTv.text = it
                }
            }
        }
    */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openActivityMi -> {
                //pra criar a intent filter fui primeiro no AndroidManifest
                val parameterIntent = Intent("OPEN_PARAMETER_ACTIVITY_ACTION").apply {
                    putExtra(PARAMETER_EXTRA, activityMainBinding.parameterTv.text)
                }
                parameterArl.launch(parameterIntent)
                true
            }

            R.id.viewMi -> {
                //vamos criar uma intent do tipo action_view
                val url = Uri.parse(activityMainBinding.parameterTv.text.toString())
                val browserIntent = Intent(Intent.ACTION_VIEW, url)
                startActivity(browserIntent)
                true
            }

            R.id.callMi -> {
                //vamos criar uma intent do tipo action_call. Precisamos de permissão no AndroidManifest
                // No caso, estamos rodando com um nível de api menor que o 23, então não precisamos de permissão pois ela será perguntada na instalação.
                //mas vamos tratar a permissão de ligação como se estivéssemos rodando em um nível de api maior que 23

                //O método checkSelfPermission só existe a partir do nível de api 23. Por isso vou fazer um if antes de chamar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        callPhone(call = true)

                    } else {
                        callPhonePermissionArl.launch(CALL_PHONE)
                    }
                } else {
                    // Faz a chamada pois estamos em uma versão menor que 23 e a permissão já foi garantida na instalação
                    callPhone(call = true)
                }
                true
            }

            R.id.dialMi -> {
                callPhone(call = false)
                true
            }

            R.id.pickMi -> {
                true
            }

            R.id.chooserMi -> {
                true
            }

            else -> {
                false
            }
        }
    }

    //implementar a função que faz a chamada de telefone
    private fun callPhone(call: Boolean) {
        startActivity(
            Intent(if (call) ACTION_CALL else ACTION_DIAL).apply {
                "tel: ${activityMainBinding.parameterTv.text}".also {
                    data = Uri.parse(it)
                }
            }
        )
    }

}