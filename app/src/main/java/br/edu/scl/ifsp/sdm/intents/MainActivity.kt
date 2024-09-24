package br.edu.scl.ifsp.sdm.intents

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        parameterArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(PARAMETER_EXTRA)?.also {
                    activityMainBinding.parameterTv.text = it
                }
            }
        }

        activityMainBinding.apply{
            parameterBt.setOnClickListener {
                val parameterIntent = Intent(this@MainActivity, ParameterActivity::class.java).apply {
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
                true
            }

            R.id.dialMi -> {
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
}